package com.example.playlistmaker.ui.playlist

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.ui.mediatec.NewPlaylistFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class PlaylistEditFragment : NewPlaylistFragment() {

    private var playlistToEdit: Playlist? = null
    private val editViewModel: PlaylistEditViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)

        playlistToEdit = arguments?.getParcelable<Playlist>("playlist_edit_key")

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupEditUI()
        populateFieldsWithPlaylistData()
    }

    private fun setupEditUI() {
        binding.textView.text = getString(R.string.edit_playlist_title)
        binding.createPlaylistButton.text = getString(R.string.save)

        confirmDialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.complete_editing))
            .setMessage(getString(R.string.all_changes_will_be_lost))
            .setNeutralButton(getString(R.string.cancel)) { dialog, which ->
                dialog.dismiss()
            }
            .setPositiveButton(getString(R.string.yes)) { dialog, which ->
                findNavController().navigateUp()
            }
    }

    private fun populateFieldsWithPlaylistData() {
        playlistToEdit?.let { playlist ->
            binding.titleEdittext.setText(playlist.title)
            binding.descriptionEdittext.setText(playlist.description)

            loadPlaylistImage(playlist.pathUrl)
            updateCreateButtonState(false)
        }
    }

    private fun loadPlaylistImage(imagePath: String?) {
        if (!imagePath.isNullOrEmpty()) {
            val source = if (imagePath.startsWith("/")) File(imagePath) else imagePath

            Glide.with(this)
                .load(source)
                .placeholder(R.drawable.placeholder_add_picture)
                .transform(CenterCrop())
                .into(binding.picturePlace)
        }
    }

    override fun createPlaylist(picUrl: Uri?) {
        playlistToEdit?.let { playlist ->
            editViewModel.updatePlaylist(
                playlist.id,
                binding.titleEdittext.text.toString(),
                binding.descriptionEdittext.text.toString(),
                picUrl
            )
            Toast.makeText(
                requireContext(),
                getString(R.string.playlist_updated_success, binding.titleEdittext.text.toString()),
                Toast.LENGTH_SHORT
            ).show()
            playlistToEdit = playlist.copy(
                title = binding.titleEdittext.text.toString(),
                description = binding.descriptionEdittext.text.toString(),
                pathUrl = editViewModel.playlistImage ?: picUrl?.toString()
            )
        }
    }
}