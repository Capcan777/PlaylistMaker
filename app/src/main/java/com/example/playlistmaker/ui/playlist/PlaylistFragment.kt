package com.example.playlistmaker.ui.playlist

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistBinding
import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.ui.player.PlayerActivity
import com.example.playlistmaker.ui.search.SearchFragment.Companion.TRACK_INTENT
import com.example.playlistmaker.ui.search.TrackAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

class PlaylistFragment : Fragment() {

    companion object {
        fun newInstance() = PlaylistFragment()
    }

    private var _binding: FragmentPlaylistBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<PlaylistViewModel>()

    private lateinit var adapter: TrackAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val playlist = arguments?.getParcelable<Playlist>("playlist_key")
        if (playlist != null) {
            setupPlaylistData(playlist)
        }

        setupClickListeners()

        adapter = TrackAdapter { track ->
            onTrackSelected(track)
        }
        adapter.onTrackHoldListener = TrackAdapter.TrackHoldListener { track ->
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.do_you_want_delete_track))
                .setNegativeButton("Нет") { dialog, which ->
                    dialog.dismiss()
                }
                .setPositiveButton("Да") { dialog, which ->
                    viewModel.deleteTrackFromPlaylist(track, playlist)
                    adapter.notifyDataSetChanged()
                }
                .show()
        }
        binding.recyclerView.adapter = adapter

        viewModel.loadTimeTracks(playlist?.id.toString())

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.tracks.collect { tracks ->
                    val totalDurationMillis = tracks.sumOf { it.trackTimeMillis }
                    val totalDurationMinutes =
                        SimpleDateFormat("mm", Locale.getDefault()).format(totalDurationMillis)
                    binding.tvPlaylistTime.text = "$totalDurationMinutes мин"
                    binding.tvPlaylistTracksNumbers.text = tracks.size.getTrackString()
                    adapter.updateTrackList(ArrayList(tracks))
                }
            }
        }

        binding.ivShare.setOnClickListener {
            if(playlist?.numberOfTracks == 0) {
                //  В этом плейлисте нет списка треков, которым можно поделиться
                Toast.makeText(requireContext(), "В этом плейлисте нет списка треков, которым можно поделиться",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupPlaylistData(playlist: Playlist) {
        val source = playlist.pathUrl?.let { path ->
            if (path.startsWith("/")) File(path) else path
        }

        binding.tvPlaylistTitle.text = playlist.title
        binding.tvPlaylistDescription.text = playlist.description
        binding.tvPlaylistTracksNumbers.text = playlist.numberOfTracks.getTrackString()

        Glide.with(this)
            .load(source)
            .placeholder(R.drawable.placeholder_playlist)
            .transform(CenterCrop())
            .into(binding.ivPlaylistImage)
    }


    private fun setupClickListeners() {
        binding.fallBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun onTrackSelected(track: Track) {
        val playerIntent = Intent(requireContext(), PlayerActivity::class.java)
        playerIntent.putExtra(TRACK_INTENT, Gson().toJson(track))
        startActivity(playerIntent)
    }

    private fun Int.getTrackString(): String {
        return when {
            this % 100 in 11..14 -> "$this треков"
            this % 10 == 1 -> "$this трек"
            this % 10 in 2..4 -> "$this трека"
            else -> "$this треков"
        }
    }

}