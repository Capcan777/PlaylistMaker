package com.example.playlistmaker.presentation.ui.search

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
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.ApiResponse
import com.example.playlistmaker.Creator
import com.example.playlistmaker.presentation.ui.player.PlayerActivity
import com.example.playlistmaker.R
import com.example.playlistmaker.constants.Constants
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.domain.api.HistoryInteractor
import com.example.playlistmaker.domain.api.SearchHistoryRepository
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.models.Track
import com.google.gson.Gson
import retrofit2.Call


class SearchActivity : AppCompatActivity() {

    private var inputEditTextState: String? = DEFAULT_EDIT_STATE
    private lateinit var binding: ActivitySearchBinding

    //    private val iTunesBaseUrl = "https://itunes.apple.com"
//    private val retrofit = Retrofit.Builder()
//        .baseUrl(iTunesBaseUrl)
//        .addConverterFactory(GsonConverterFactory.create())
//        .build()
//    private val iTunesService = retrofit.create(ItunesApiService::class.java)
    private var tracks = ArrayList<Track>()
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

    //    private lateinit var sharedPreferences: SharedPreferences
//    private lateinit var searchHistory: SearchHistory
    private lateinit var progressBar: FrameLayout
    private val handler = Handler(Looper.getMainLooper())
    private val runnable = Runnable { searchTracks() }
    private lateinit var searchHistoryRepository: SearchHistoryRepository
    private lateinit var tracksInteractor: TracksInteractor
    private lateinit var historyInteractor: HistoryInteractor


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tracksInteractor = Creator.provideTracksInteractor()
        searchHistoryRepository = Creator.provideSearchHistoryRepository(this)

        // Инициализация View
        placeholderMessageNotInternet = binding.placeHolderMessageNotInternet
        placeholderMessageNotFound = binding.placeHolderNotFound
        inputEditText = binding.textSearch
        clearButton = binding.clearIcon
        refreshButton = binding.updateButton
        back = binding.back
        historyLayout = binding.history
        clearHistoryButton = binding.clearHistoryButton
        progressBar = binding.progressBar

//        // Настройка SharedPreferences
//        sharedPreferences = getSharedPreferences(Constants.PLAYLIST_MAKER_PREFERENCES, MODE_PRIVATE)
//        searchHistory = SearchHistory(sharedPreferences)

        // Настройка адаптеров
        trackListAdapter = TrackAdapter(tracks) { track ->
            onTrackSelected(track)
        }
        historyAdapter = TrackAdapter(searchHistoryRepository.readTracksFromHistory()) { track ->
            onTrackSelected(track)
        }

        binding.recyclerView.adapter = trackListAdapter
        binding.recyclerViewHistory.adapter = historyAdapter

        // Обработка нажатия на кнопку "Назад"
        back.setOnClickListener {
            finish()
        }

        // Очистка текста поиска и скрытие клавиатуры
        clearButton.setOnClickListener {
            inputEditText.setText(getString(R.string.empty_string))
            hideKeyboard(inputEditText)
            tracks.clear()
            trackListAdapter.notifyDataSetChanged()
            historyLayout.visibility = View.VISIBLE
        }

        // Очистка истории поиска по нажатию кнопки "Очистить"
        clearHistoryButton.setOnClickListener {
            clearHistory()
            historyLayout.visibility = View.GONE
        }

        // Отображение истории поиска при фокусе на EditText
        inputEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && inputEditText.text.isEmpty()) {
                showHistory()
                placeholderMessageNotFound.visibility = View.GONE

            }
        }

        // Слушатель изменения текста в EditText
        inputEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clickDebounce()
                inputEditTextState = s.toString()
                clearButton.visibility = clearButtonVisibility(s)

                if (s.isNullOrEmpty()) {
                    tracks.clear()
                    showHistory()
                    trackListAdapter.notifyDataSetChanged()
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        // Обновление списка треков при нажатии на кнопку обновления
        refreshButton.setOnClickListener {
            searchTracks()
        }

        // Поиск треков по нажатию на клавишу "Done"
        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchTracks()
                true
            } else {
                false
            }
        }
    }

    // Определение видимости кнопки очистки
    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    // Сохранение состояния EditText при повороте экрана
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(EDIT_TEXT_STATE, inputEditTextState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        inputEditTextState = savedInstanceState.getString(EDIT_TEXT_STATE, inputEditTextState)
        inputEditText.setText(inputEditTextState)
    }


    // Поиск треков через iTunes API
    private fun searchTracks() {
        if (inputEditText.text.isNotEmpty()) {
            binding.recyclerView.visibility = View.GONE
            historyLayout.visibility = View.GONE
            progressBar.visibility = View.VISIBLE
            tracksInteractor.searchTracks(
                inputEditText.text.toString(),
                object : TracksInteractor.TracksConsumer {
                    override fun consume(foundTracks: List<Track>) {
                        runOnUiThread {
                            progressBar.visibility = View.GONE
                            tracks.clear()
                            if (foundTracks.isNotEmpty()) {
                                tracks.addAll(foundTracks)
                                trackListAdapter.notifyDataSetChanged()
                                placeholderMessageNotFound.visibility = View.GONE
                                placeholderMessageNotInternet.visibility = View.GONE
                                historyLayout.visibility = View.GONE

                            } else {
                                showMessage(placeholderMessageNotFound)
                            }
                        } else {
                            showMessage(placeholderMessageNotInternet)
                        }
                    }


                    // создается поток

//                    override fun onResponse(
//                        call: Call<ApiResponse>,
//                        response: Response<ApiResponse>
//                    ) {
//                        if ()
//                            progressBar.visibility = View.GONE
//                        if (response.isSuccessful) {
//                            tracks.clear()
//                            val results = response.body()?.results
//                            if (!results.isNullOrEmpty()) {
//                                binding.recyclerView.visibility = View.VISIBLE
//
//                    }

                    override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                        progressBar.visibility = View.GONE
                        showMessage(placeholderMessageNotInternet)
                    }

                    override fun onSuccess(foundTracks: ArrayList<Track>) {
                        TODO("Not yet implemented")
                    }

                    override fun onNoResult() {
                        showMessage(placeholderMessageNotFound)
                    }

                    override fun onNetworkError() {
                        TODO("Not yet implemented")
                    }


                }

                        override fun onError(errorMessage: String) {
                    progressBar.visibility = View.GONE
                }

        })
    }

    // Скрытие клавиатуры
    private fun hideKeyboard(view: View) {
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(view.windowToken, 0)
    }

    // Отображение сообщений о состоянии поиска
    private fun showMessage(linearLayout: LinearLayout) {
        when (linearLayout) {
            placeholderMessageNotInternet -> {
                tracks.clear()
                placeholderMessageNotInternet.visibility = View.VISIBLE
                placeholderMessageNotFound.visibility = View.GONE
                trackListAdapter.notifyDataSetChanged()
            }

            placeholderMessageNotFound -> {
                tracks.clear()
                placeholderMessageNotFound.visibility = View.VISIBLE
                placeholderMessageNotInternet.visibility = View.GONE
                historyLayout.visibility = View.GONE
                trackListAdapter.notifyDataSetChanged()
            }
        }
    }

    // Отображение истории поиска
    private fun showHistory() {
        if (historyAdapter.itemCount > 0) {
            historyLayout.visibility = View.VISIBLE
            getHistory()
        } else {
            historyLayout.visibility = View.GONE
        }
    }

    // Чтение истории поиска
    private fun getHistory() {
        historyAdapter.updateTrackList(searchHistory.readTracksFromHistory())
        historyAdapter.notifyDataSetChanged()
    }

    // Удаление истории поиска
    private fun clearHistory() {
        historyAdapter.updateTrackList(tracks)
        searchHistory.clearHistoryPref()
    }

    // Функция нажатия кнопки выбора трека
    private fun onTrackSelected(track: Track) {
        val currentHistory = searchHistory.readTracksFromHistory()
        currentHistory.removeAll {
            it.trackId == track.trackId
        }
        currentHistory.add(0, track)
        // Установка ограничения списка треков в истории
        if (currentHistory.size > 10) {
            currentHistory.removeAt(10)
        }
        searchHistory.saveTrackToHistory(currentHistory)
        historyAdapter.updateTrackList(currentHistory)
        val playerIntent = Intent(this, PlayerActivity::class.java)
        playerIntent.putExtra(Constants.TRACK_INTENT, Gson().toJson(track))
        startActivity(playerIntent)
    }


    private fun clickDebounce() {
        handler.removeCallbacks(runnable)
        handler.postDelayed(runnable, Constants.CLICK_DEBOUNCE_DELAY)
    }

    companion object {
        const val EDIT_TEXT_STATE = "EDIT_TEXT_STATE"
        const val DEFAULT_EDIT_STATE = ""
    }
}

