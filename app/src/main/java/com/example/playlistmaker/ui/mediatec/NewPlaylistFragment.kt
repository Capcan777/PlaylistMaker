package com.example.playlistmaker.ui.mediatec

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentNewPlaylistBinding
import com.example.playlistmaker.ui.mediatec.view_model.NewPlaylistViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel

open class NewPlaylistFragment : Fragment() {

    private var _binding: FragmentNewPlaylistBinding? = null
    protected val binding get() = _binding!!

    private val viewModel: NewPlaylistViewModel by viewModel()

    var picUrl: Uri? = null

    lateinit var confirmDialog: MaterialAlertDialogBuilder


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNewPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        confirmDialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Завершить создание плейлиста?")
            .setMessage("Все несохраненные данные будут потеряны")
            .setNeutralButton("Отмена") { dialog, which ->
                dialog.dismiss()
            }
            .setPositiveButton("Да") { dialog, which ->
                findNavController().navigateUp()
            }
        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    binding.picturePlace.setImageURI(uri)
                    viewModel.savePictureToStorage(uri)
                    picUrl = uri
                } else {
                    Log.d("PhotoPicker", "No media selected")
                }
            }

        binding.titleEdittext.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateCreateButtonState(s.isNullOrEmpty())
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })

        binding.titleEdittext.setOnClickListener {
            binding.titleEdittext.requestFocus()
            showKeyboard(binding.titleEdittext)
        }

        binding.titleEdittext.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                showKeyboard(binding.titleEdittext)
            }
        }
        binding.picturePlace.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.fallBack.setOnClickListener {
            confirmDialog.show()
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    confirmDialog.show()
                }
            })
        binding.createPlaylistButton.setOnClickListener {
            createPlaylist(picUrl)
            findNavController().navigateUp()
        }
    }

    private fun showKeyboard(view: View) {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        view.post {
            view.requestFocus()
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    protected open fun updateCreateButtonState(isDisabled: Boolean) {
        binding.createPlaylistButton.isEnabled = !isDisabled
        binding.createPlaylistButton.setBackgroundColor(
            if (isDisabled) requireContext().getColor(R.color.grey) else requireContext().getColor(
                R.color.dark_blue
            )
        )
    }

    protected open fun createPlaylist(picUrl: Uri?) {
        viewModel.createPlaylist(
            binding.titleEdittext.text.toString(),
            binding.descriptionEdittext.text.toString(),
            picUrl,
        )
        Toast.makeText(requireContext(), "Плейлист ${binding.titleEdittext.text.toString()} успешно создан",
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}