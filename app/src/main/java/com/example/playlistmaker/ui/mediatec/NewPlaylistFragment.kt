package com.example.playlistmaker.ui.mediatec

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.navigateUp
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentNewPlaylistBinding
import com.example.playlistmaker.ui.mediatec.view_model.NewPlaylistViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel

class NewPlaylistFragment : Fragment() {

    companion object {
        fun newInstance() = NewPlaylistFragment()
    }

    private var _binding: FragmentNewPlaylistBinding? = null
    private val binding get() = _binding!!
    private val viewModel: NewPlaylistViewModel by viewModel()


    private lateinit var tvTitle: EditText
    private lateinit var tvDescription: EditText

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
                } else {
                    Log.d("PhotoPicker", "No media selected")
                }
            }
        binding.titleEdittext.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrEmpty()) {
                    binding.createPlaylistButton.setBackgroundColor(requireContext().getColor(R.color.grey))
                    binding.createPlaylistButton.isEnabled = false
                } else {
                    binding.createPlaylistButton.setBackgroundColor(requireContext().getColor(R.color.dark_blue))
                    binding.createPlaylistButton.isEnabled = true
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })
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
            showSuccesToast()
            findNavController().navigateUp()
        }

    }


    private fun showSuccesToast() {
        Toast.makeText(requireContext(), "Плейлист успешно создан", Toast.LENGTH_SHORT).show()
    }

}