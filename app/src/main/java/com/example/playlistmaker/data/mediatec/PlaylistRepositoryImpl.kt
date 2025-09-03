package com.example.playlistmaker.data.mediatec

import android.util.Log
import com.example.playlistmaker.converters.PlaylistDbConvertor
import com.example.playlistmaker.converters.TrackDbConvertor
import com.example.playlistmaker.data.db.AppDataBase
import com.example.playlistmaker.data.db.PlaylistDataBase
import com.example.playlistmaker.data.db.PlaylistTrackDataBase
import com.example.playlistmaker.data.db.entity.PlaylistEntity
import com.example.playlistmaker.data.db.entity.PlaylistTrackEntity
import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.repository.PlaylistRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class PlaylistRepositoryImpl(
    private val dataBase: PlaylistDataBase,
    private val playlistTrackDataBase: PlaylistTrackDataBase,
    private val appDataBase: AppDataBase,
    private val playlistDbConvertor: PlaylistDbConvertor,
    private val trackDbConvertor: TrackDbConvertor
) : PlaylistRepository {
    override suspend fun createPlaylist(playlist: Playlist) {
        require(playlist.title.isNotBlank())
        val playlistEntity = playlistDbConvertor.map(playlist)
        dataBase.playlistDao().createPlaylist(playlistEntity)
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        val playlistEntity = playlistDbConvertor.map(playlist)
        dataBase.playlistDao().deletePlaylistEntity(playlistEntity)

    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        val playlistEntity = playlistDbConvertor.map(playlist)
        dataBase.playlistDao().updatePlaylist(playlistEntity)
    }

    override fun getPlaylists(): Flow<List<Playlist>> = flow {
        val playlists = dataBase.playlistDao().getPlaylists()
        val models = convertFromPlaylistEntity(playlists).map { playlist ->
            val trackIds = playlistTrackDataBase
                .playlistTrackDao()
                .getTrackIdsForPlaylist(playlist.id.toString())
            playlist.copy(trackIds = trackIds)
        }
        emit(models)
    }

    override suspend fun addTrackToPlaylist(
        track: Track,
        playlist: Playlist
    ) {
        val playlistEntity = playlistDbConvertor.map(playlist)
        val playlistTrackEntity = PlaylistTrackEntity(
            trackId = track.trackId,
            artworkUrl100 = track.artworkUrl100,
            trackName = track.trackName,
            artistName = track.artistName,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            trackTimeMillis = track.trackTimeMillis,
            previewUrl = track.previewUrl,
            isFavorite = track.isFavorite,
            playlistId = playlistEntity.id.toString()
        )
        playlistTrackDataBase.playlistTrackDao().addTrackToPlaylist(playlistTrackEntity)
        // Инкрементируем счётчик треков в плейлисте
        val updated = playlistEntity.copy(numberOfTracks = playlistEntity.numberOfTracks + 1)
        dataBase.playlistDao().updatePlaylist(updated)
    }

    override suspend fun getPlaylistById(playlistId: Int): Playlist {
        val playlistEntity = dataBase.playlistDao().getPlaylistById(playlistId)
        return playlistDbConvertor.map(playlistEntity)
    }

    override fun getTracksOfPlaylist(playlistId: String): Flow<List<Track>> = flow {
        try {
            val playlistTrackEntities = withContext(Dispatchers.IO) {
                playlistTrackDataBase.playlistTrackDao()
                    .getTracksOfPlaylist(playlistId)
            }
            val tracksList = convertFromPlaylistTrackEntity(playlistTrackEntities)
            emit(tracksList)
        } catch (e: Exception) {
            Log.e("PlaylistRepository", "Ошибка при получении треков плейлиста", e)
            throw e
        }
    }


    override fun getTracksByIds(trackIds: List<Int>): Flow<List<Track>> = flow {
        val trackEntities = appDataBase.trackDao().getTracksByIds(trackIds)
        val tracks = trackEntities.map { trackEntity ->
            trackDbConvertor.map(trackEntity)
        }
        emit(tracks)
    }

    override suspend fun deleteTrackFromPlaylist(track: Track, playlist: Playlist?) {
        val trackId = track.trackId
        val playlistId = playlist?.id?.toString() ?: return

        // 1) Удаляем связь трека с плейлистом
        playlistTrackDataBase.playlistTrackDao().deleteTrackFromPlaylist(trackId, playlistId)

        // 2) Обновляем счётчик треков в плейлисте
        val playlistEntity = playlistDbConvertor.map(playlist)
        val updated = playlistEntity.copy(numberOfTracks = (playlistEntity.numberOfTracks - 1).coerceAtLeast(0))
        dataBase.playlistDao().updatePlaylist(updated)

        // 3) Проверяем, остался ли трек в других плейлистах
        val usageCount = playlistTrackDataBase.playlistTrackDao().getTrackUsageCount(trackId)
        if (usageCount == 0) {
            // Здесь можно удалить запись о треке из общей таблицы треков, если она создаётся при добавлении в плейлист.
            // В текущей архитектуре плейлист хранит полную копию трека в playlist_tracks_table, поэтому дополнительных действий не требуется.
        }
    }


    override suspend fun isTrackInAnyPlaylist(trackId: Int): Boolean {
        val playlists = dataBase.playlistDao().getPlaylists()
        for (playlist in playlists) {
            val trackIds = playlistTrackDataBase.playlistTrackDao()
                .getTrackIdsForPlaylist(playlist.id.toString())
            if (trackIds.contains(trackId)) return true
        }
        return false
    }


    private fun convertFromPlaylistEntity(playlists: List<PlaylistEntity>): List<Playlist> {
        return playlists.map { playlist -> playlistDbConvertor.map(playlist) }
    }

    private fun convertFromPlaylistTrackEntity(playlists: List<PlaylistTrackEntity>): List<Track> {
        return playlists.map { playlist -> playlistDbConvertor.map(playlist) }
    }
}