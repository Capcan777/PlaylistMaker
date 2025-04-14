package com.example.playlistmaker.ui.search

import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.presentation.ui.search.TracksSearchViewModel
import com.example.playlistmaker.ui.player.PlayerActivity
import com.example.playlistmaker.ui.search.state.SearchScreenState
import com.google.gson.Gson
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    val binding get() = _binding!!

    private val viewModel by viewModel<TracksSearchViewModel>()

    private var inputEditTextState: String? = DEFAULT_EDIT_STATE

    private var isClickAllowed = true

    private lateinit var trackListAdapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter

    private lateinit var placeholderMessageNotInternet: LinearLayout
    private lateinit var placeholderMessageNotFound: LinearLayout
    private lateinit var inputEditText: EditText
    private lateinit var clearButton: ImageView
    private lateinit var refreshButton: Button
    private lateinit var historyLayout: LinearLayout
    private lateinit var clearHistoryButton: Button
    private lateinit var progressBar: FrameLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        placeholderMessageNotInternet = binding.placeHolderMessageNotInternet
        placeholderMessageNotFound = binding.placeHolderNotFound
        inputEditText = binding.textSearch
        clearButton = binding.clearIcon
        refreshButton = binding.updateButton
        historyLayout = binding.history
        clearHistoryButton = binding.clearHistoryButton
        progressBar = binding.progressBar

        historyAdapter = TrackAdapter { track ->
            onTrackSelected(track)
        }
        trackListAdapter = TrackAdapter { track ->
            onTrackSelected(track)
        }

        binding.recyclerView.adapter = trackListAdapter
        binding.recyclerViewHistory.adapter = historyAdapter

        viewModel.screenState.observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                is SearchScreenState.Loading -> {
                    progressBar.visibility = View.VISIBLE
                    binding.recyclerView.visibility = View.GONE
                    placeholderMessageNotFound.visibility = View.GONE
                    placeholderMessageNotInternet.visibility = View.GONE
                    historyLayout.visibility = View.GONE
                }

                SearchScreenState.EmptyResult -> {
                    progressBar.visibility = View.GONE
                    binding.recyclerView.visibility = View.GONE
                    placeholderMessageNotFound.visibility = View.VISIBLE
                    placeholderMessageNotInternet.visibility = View.GONE
                    historyLayout.visibility = View.GONE
                }

                is SearchScreenState.History -> {
                    progressBar.visibility = View.GONE
                    historyLayout.visibility = View.VISIBLE
                    historyAdapter.updateTrackList(state.historyList)
                    binding.recyclerView.visibility = View.GONE
                    placeholderMessageNotFound.visibility = View.GONE
                    placeholderMessageNotInternet.visibility = View.GONE

                }

                SearchScreenState.NetworkError -> {
                    progressBar.visibility = View.GONE
                    binding.recyclerView.visibility = View.GONE
                    placeholderMessageNotFound.visibility = View.GONE
                    placeholderMessageNotInternet.visibility = View.VISIBLE
                    historyLayout.visibility = View.GONE
                }

                SearchScreenState.Nothing -> {
                    progressBar.visibility = View.GONE
                    binding.recyclerView.visibility = View.GONE
                    placeholderMessageNotFound.visibility = View.GONE
                    placeholderMessageNotInternet.visibility = View.GONE
                    historyLayout.visibility = View.GONE
                }

                is SearchScreenState.Tracks -> {
                    progressBar.visibility = View.GONE
                    binding.recyclerView.visibility = View.VISIBLE
                    state.trackSearch?.let { trackListAdapter.updateTrackList(it) }
                    placeholderMessageNotFound.visibility = View.GONE
                    placeholderMessageNotInternet.visibility = View.GONE
                    historyLayout.visibility = View.GONE
                }
            }
        })
        viewModel.searchResults.observe(viewLifecycleOwner) { results ->
            if (results != null && results.isNotEmpty()) {
                trackListAdapter.updateTrackList(results)
                binding.recyclerView.visibility = View.VISIBLE
            } else {
                binding.recyclerView.visibility = View.GONE
            }
        }


        clearButton.setOnClickListener {
            inputEditText.setText(getString(R.string.empty_string))
            hideKeyboard(inputEditText)
            trackListAdapter.updateTrackList(arrayListOf())
            viewModel.updateHistory()
        }
        clearHistoryButton.setOnClickListener {
            viewModel.clearHistory()
            historyLayout.visibility = View.GONE
            historyAdapter.updateTrackList(arrayListOf())
        }

        inputEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && inputEditText.text.isEmpty()) {
                viewModel.updateHistory()
            }
        }

        inputEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.searchDebounce(changedText = s?.toString() ?: "")
                inputEditTextState = s.toString()
                clearButton.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE

                if (s.isNullOrEmpty()) {
                    trackListAdapter.updateTrackList(arrayListOf())
                    viewModel.updateHistory()
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        refreshButton.setOnClickListener {
            viewModel.searchDebounce(inputEditText.text.toString())
        }

        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.searchDebounce(inputEditText.text.toString())
                true
            } else {
                false
            }

        }
    }


    private fun hideKeyboard(view: View) {
        val inputMethodManager =
            requireContext().getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun onTrackSelected(track: Track) {
        viewModel.addTrackToHistory(track)
        if (clickDebounce()) {
            val playerIntent = Intent(requireContext(), PlayerActivity::class.java)
            playerIntent.putExtra(TRACK_INTENT, Gson().toJson(track))
            startActivity(playerIntent)
        }
    }

    private fun clickDebounce(): Boolean {
        val current: Boolean = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            viewLifecycleOwner.lifecycleScope.launch {
                delay(CLICK_DEBOUNCE_DELAY)
                isClickAllowed = true
            }
        }
        return current
    }

    companion object {
        const val DEFAULT_EDIT_STATE = ""
        const val CLICK_DEBOUNCE_DELAY = 1000L
        const val TRACK_INTENT = "track_intent"
    }
}

