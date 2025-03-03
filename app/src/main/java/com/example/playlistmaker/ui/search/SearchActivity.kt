package com.example.playlistmaker.ui.search

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.presentation.ui.search.TracksSearchViewModel
import com.example.playlistmaker.ui.player.PlayerActivity
import com.example.playlistmaker.ui.search.state.SearchScreenState
import com.google.gson.Gson


class SearchActivity : AppCompatActivity() {

    private var inputEditTextState: String? = DEFAULT_EDIT_STATE

    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var binding: ActivitySearchBinding

    private lateinit var trackListAdapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter

    private lateinit var placeholderMessageNotInternet: LinearLayout
    private lateinit var placeholderMessageNotFound: LinearLayout
    private lateinit var inputEditText: EditText
    private lateinit var clearButton: ImageView
    private lateinit var refreshButton: Button
    private lateinit var back: ImageView
    private lateinit var historyLayout: LinearLayout
    private lateinit var clearHistoryButton: Button
    private lateinit var progressBar: FrameLayout

    private val viewModel: TracksSearchViewModel by viewModels()

    private var searchResults: ArrayList<Track>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        placeholderMessageNotInternet = binding.placeHolderMessageNotInternet
        placeholderMessageNotFound = binding.placeHolderNotFound
        inputEditText = binding.textSearch
        clearButton = binding.clearIcon
        refreshButton = binding.updateButton
        back = binding.back
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

        viewModel.screenState.observe(this, Observer { state ->
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
                    trackListAdapter.updateTrackList(state.trackSearch)
                    placeholderMessageNotFound.visibility = View.GONE
                    placeholderMessageNotInternet.visibility = View.GONE
                    historyLayout.visibility = View.GONE
                }
            }
        })
        viewModel.searchResults.observe(this) { results ->
            if (results != null && results.isNotEmpty()) {
                trackListAdapter.updateTrackList(results)
                binding.recyclerView.visibility = View.VISIBLE
            } else {
                binding.recyclerView.visibility = View.GONE
            }
        }

        back.setOnClickListener {
            finish()
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(EDIT_TEXT_STATE, inputEditTextState)
        searchResults?.let {
            outState.putString(SEARCH_RESULTS_STATE, Gson().toJson(it))
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        inputEditTextState = savedInstanceState.getString(EDIT_TEXT_STATE, inputEditTextState)
        inputEditText.setText(inputEditTextState)
    }

    private fun hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun onTrackSelected(track: Track) {
        viewModel.addTrackToHistory(track)
        if (clickDebounce()) {
            val playerIntent = Intent(this, PlayerActivity::class.java)
            playerIntent.putExtra(TRACK_INTENT, Gson().toJson(track))
            startActivity(playerIntent)
        }
    }


    private fun clickDebounce(): Boolean {
        val current: Boolean = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    companion object {
        const val EDIT_TEXT_STATE = "EDIT_TEXT_STATE"
        const val DEFAULT_EDIT_STATE = ""
        const val CLICK_DEBOUNCE_DELAY = 1000L
        const val SEARCH_RESULTS_STATE = "SEARCH_RESULTS_STATE"
        const val TRACK_INTENT = "track_intent"
    }
}

