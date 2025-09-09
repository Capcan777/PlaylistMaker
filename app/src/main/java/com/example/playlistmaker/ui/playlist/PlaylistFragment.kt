package com.example.playlistmaker.ui.playlist

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
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
import com.google.android.material.bottomsheet.BottomSheetBehavior
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
    private var currentPlaylist: Playlist? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bottomSheetBehaviorMenu = BottomSheetBehavior.from(binding.bottomSheetMenu).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        bottomSheetBehaviorMenu.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay.isVisible = false
                    }

                    else -> binding.overlay.isVisible = true
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                binding.overlay.alpha = slideOffset + 1f
            }
        })


        val playlist = arguments?.getParcelable<Playlist>("playlist_key")
        currentPlaylist = playlist
        playlist?.let { viewModel.loadPlaylist(it.id) }
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
                .setNegativeButton(getString(R.string.no)) { dialog, which ->
                    dialog.dismiss()
                }
                .setPositiveButton("Да") { dialog, which ->
                    viewModel.deleteTrackFromPlaylist(track, playlist!!.id)
                    adapter.notifyDataSetChanged()
                    if (playlist.numberOfTracks == 0) binding.emptyPlaylistMessage.isVisible = true

                }
                .show()
        }
        binding.recyclerView.adapter = adapter

        viewModel.loadTracksList(playlist?.id.toString())

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.tracks.collect { tracks ->
                    val totalDurationMillis = tracks.sumOf { it.trackTimeMillis }
                    val totalDurationMinutes =
                        SimpleDateFormat("mm", Locale.getDefault()).format(totalDurationMillis)
                    binding.tvPlaylistTime.text = "$totalDurationMinutes мин"
                    binding.tvPlaylistTracksNumbers.text = getStringCountTracks(tracks.size)
                    binding.emptyPlaylistMessage.isVisible = tracks.isEmpty()
                    adapter.updateTrackList(ArrayList(tracks))
                }
            }
        }

//        binding.ivShare.setOnClickListener {
//
//            if (playlist == null || playlist.numberOfTracks == 0) {
//                binding.emptyPlaylistMessage.isVisible = true
//            } else {
//
//                viewModel.sharePlaylist(playlist)
//            }
//        }

        binding.ivMore.setOnClickListener {
            bottomSheetBehaviorMenu.state = BottomSheetBehavior.STATE_COLLAPSED
            binding.bottomSheetTracks.isVisible = true
            showPlaylistInfoInBottomMenu(playlist!!)
            val pl = viewModel.currentPlaylist.value ?: playlist
            showPlaylistInfoInBottomMenu(pl!!)
        }

        binding.share.setOnClickListener {
            if (adapter.itemCount == 0) {
                binding.emptyPlaylistMessage.isVisible = true
                Toast.makeText(
                    requireContext(),
                    getString(R.string.no_tracks_in_playlist),
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            val pl = viewModel.currentPlaylist.value ?: playlist
            viewModel.sharePlaylist(pl)
        }

        binding.delete.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.do_you_wanna_delete_playlist, playlist?.title))
                .setNegativeButton(getString(R.string.no)) { dialog, which ->
                    dialog.dismiss()
                }
                .setPositiveButton(getString(R.string.yes)) { dialog, which ->
                    viewModel.deletePlaylist(playlist)
                    findNavController().navigateUp()
                }
                .show()
        }

        binding.edit.setOnClickListener {
            val bundle = Bundle().apply {
                putParcelable("playlist_edit_key", playlist)
            }
            findNavController().navigate(
                R.id.action_playlistFragment_to_playlistEditFragment,
                bundle
            )

        }
    }

    private fun setupPlaylistData(playlist: Playlist) {
        val source = playlist.pathUrl?.let { path ->
            if (path.startsWith("/")) File(path) else path
        }

        binding.tvPlaylistTitle.text = playlist.title
        binding.tvPlaylistDescription.text = playlist.description
        binding.tvPlaylistTracksNumbers.text = getStringCountTracks(playlist.numberOfTracks)

        Glide.with(this)
            .load(source)
            .placeholder(R.drawable.placeholder_playlist)
            .transform(CenterCrop())
            .into(binding.ivPlaylistImage)
    }

    private fun updatePlaylistData(playlist: Playlist) {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.currentPlaylist.collect { updatedPlaylist ->
                    val target = updatedPlaylist ?: playlist
                    currentPlaylist = target
                    setupPlaylistData(target)
                    showPlaylistInfoInBottomMenu(target)
                }
            }
        }
    }


    private fun setupClickListeners() {
        binding.fallBack.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.ivShare.setOnClickListener { handleShareClick() }
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


    private fun showPlaylistInfoInBottomMenu(playlist: Playlist) {
        val source = playlist.pathUrl?.let { path ->
            if (path.startsWith("/")) File(path) else path
        }
        Glide.with(this)
            .load(source)
            .placeholder(R.drawable.placeholder_playlist)
            .transform(CenterCrop())
            .into(binding.playlistImageBottom)

        binding.tvTitlePlaylist.text = playlist.title
        binding.tvTracksNumberInPlaylist.text = getStringCountTracks(adapter.itemCount)
    }

    override fun onResume() {
        super.onResume()
        currentPlaylist?.let { playlist ->
            updatePlaylistData(playlist)
        }
    }

    private fun handleShareClick() {
        currentPlaylist?.let { playlist ->
            if (adapter.itemCount == 0) {
                binding.emptyPlaylistMessage.isVisible = true
            } else {
                viewModel.sharePlaylist(playlist)
            }
        }
    }

    private fun getStringCountTracks(count: Int): String {
        val countTracks = count
        val stringTracks = requireContext().resources.getQuantityString(
            R.plurals.numberOfTracks,
            countTracks,
            countTracks
        )
        return stringTracks
    }

}