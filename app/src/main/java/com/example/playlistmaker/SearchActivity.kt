package com.example.playlistmaker

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.constants.Constants
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class SearchActivity : AppCompatActivity() {

    private var inputEditTextState: String? = DEFAULT_EDIT_STATE
    private lateinit var binding: ActivitySearchBinding
    private val iTunesBaseUrl = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder()
        .baseUrl(iTunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val iTunesService = retrofit.create(ItunesApiService::class.java)
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
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var searchHistory: SearchHistory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Инициализация View
        placeholderMessageNotInternet = binding.placeHolderMessageNotInternet
        placeholderMessageNotFound = binding.placeHolderNotFound
        inputEditText = binding.textSearch
        clearButton = binding.clearIcon
        refreshButton = binding.updateButton
        back = binding.back
        historyLayout = binding.history
        clearHistoryButton = binding.clearHistoryButton

        // Настройка SharedPreferences
        sharedPreferences = getSharedPreferences(Constants.PLAYLIST_MAKER_PREFERENCES, MODE_PRIVATE)
        searchHistory = SearchHistory(sharedPreferences)

        // Настройка адаптеров
        trackListAdapter = TrackAdapter(tracks) { track ->
            onTrackSelected(track)
        }
        historyAdapter = TrackAdapter(searchHistory.readTracksFromHistory()) { track ->
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

    override fun onResume() {
        super.onResume()
        getHistory()
        binding.recyclerViewHistory.invalidate()
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
        iTunesService.search(inputEditText.text.toString())
            .enqueue(object : Callback<ApiResponse> {
                override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                    if (response.isSuccessful) {
                        tracks.clear()
                        val results = response.body()?.results
                        if (!results.isNullOrEmpty()) {
                            tracks.addAll(results)
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

                override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                    showMessage(placeholderMessageNotInternet)
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
        playerIntent.putExtra(getString(R.string.track_intent), Gson().toJson(track))
        startActivity(playerIntent)


    }

    companion object {
        const val EDIT_TEXT_STATE = "EDIT_TEXT_STATE"
        const val DEFAULT_EDIT_STATE = ""
    }
}

