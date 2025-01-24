package com.example.playlistmaker.presentation.ui

import android.app.Activity
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
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.TrackConsumer
import com.example.playlistmaker.constants.Constants
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.presentation.ui.player.PlayerActivity
import com.example.playlistmaker.presentation.ui.search.TrackAdapter
import com.google.gson.Gson

class TracksSearchController(private val activity: Activity, private val adapter: TrackAdapter) {
    private var inputEditTextState: String? = DEFAULT_EDIT_STATE

    private val getTracksUseCase = Creator.provideTracksUseCase()
    private val provideSearchHistoryInteractor = Creator.provideSearchHistoryInteractor()

    private var tracks = ArrayList<Track>()

    private lateinit var placeholderMessageNotInternet: LinearLayout
    private lateinit var placeholderMessageNotFound: LinearLayout
    private lateinit var inputEditText: EditText
    private lateinit var clearButton: ImageView
    private lateinit var refreshButton: Button
    private lateinit var back: ImageView
    private lateinit var historyLayout: LinearLayout
    private lateinit var clearHistoryButton: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewHistory: RecyclerView


    //    private lateinit var searchHistory: SearchHistory
    private lateinit var progressBar: FrameLayout
    private val handler = Handler(Looper.getMainLooper())
    private val runnable = Runnable { searchTracks() }

    fun onCreate() {

        // Инициализация View
        placeholderMessageNotInternet = activity.findViewById(R.id.place_holder_message_not_internet)
        placeholderMessageNotFound = activity.findViewById(R.id.place_holder_not_found)
        inputEditText = activity.findViewById(R.id.textSearch)
        clearButton = activity.findViewById(R.id.clearIcon)
        refreshButton = activity.findViewById(R.id.update_button)
        back = activity.findViewById(R.id.back)
        historyLayout = activity.findViewById(R.id.history)
        clearHistoryButton = activity.findViewById(R.id.clear_history_button)
        progressBar = activity.findViewById(R.id.progressBar)
        recyclerView = activity.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerViewHistory = activity.findViewById<RecyclerView>(R.id.recyclerViewHistory)

        // Настройка адаптеров
        adapter.tracks = tracks
        recyclerViewHistory.adapter = adapter


//        trackListAdapter = TrackAdapter(tracks) { track ->
//            onTrackSelected(track)
//        }
//        historyAdapter =
//            TrackAdapter(provideSearchHistoryInteractor.readTracksFromHistory()) { track ->
//                onTrackSelected(track)
//            }

        recyclerView.adapter = trackListAdapter
        recyclerViewHistory.adapter = historyAdapter

        // Обработка нажатия на кнопку "Назад"
        back.setOnClickListener {
            activity.finish()
        }

        // Очистка текста поиска и скрытие клавиатуры
        clearButton.setOnClickListener {
            inputEditText.setText(activity.getString(R.string.empty_string))
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
                searchDebounce()
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

    fun onDestroy() {
        handler.removeCallbacks(runnable)
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
            recyclerView.visibility = View.GONE
            historyLayout.visibility = View.GONE
            progressBar.visibility = View.VISIBLE

            getTracksUseCase.execute(inputEditText.text.toString(), object : TrackConsumer {
                override fun onSuccess(response: ArrayList<Track>) {
                    activity.runOnUiThread {

                        recyclerView.visibility = View.VISIBLE
                        tracks.addAll(response)
                        showFoundTracks(response)

                    }
                }

                override fun onNoResult() {
                    activity.runOnUiThread {
                        showMessage(placeholderMessageNotFound)
                    }
                }

                override fun onNetworkError() {
                    activity.runOnUiThread {
                        showMessage(placeholderMessageNotInternet)
                    }
                }

            })

        }
    }

    // Скрытие клавиатуры
    private fun hideKeyboard(view: View) {
        val inputMethodManager =
            view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
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

    private fun showFoundTracks(trackList: ArrayList<Track>) {
        progressBar.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
        placeholderMessageNotFound.visibility = View.GONE
        placeholderMessageNotInternet.visibility = View.GONE
        historyLayout.visibility = View.GONE
        progressBar.visibility = View.GONE
        trackListAdapter.trackArrayList = trackList
        trackListAdapter.notifyDataSetChanged()

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
        historyAdapter.updateTrackList(provideSearchHistoryInteractor.readTracksFromHistory())
        historyAdapter.notifyDataSetChanged()
    }

    // Удаление истории поиска
    private fun clearHistory() {
        historyAdapter.updateTrackList(tracks)
        provideSearchHistoryInteractor.clearHistoryPref()
    }

    // Функция нажатия кнопки выбора трека
    private fun onTrackSelected(track: Track) {
        val currentHistory = provideSearchHistoryInteractor.readTracksFromHistory()
        provideSearchHistoryInteractor.addTrackToHistory(track, currentHistory)
        historyAdapter.updateTrackList(currentHistory)
        val playerIntent = Intent(activity, PlayerActivity::class.java)
        playerIntent.putExtra(Constants.TRACK_INTENT, Gson().toJson(track))
        activity?.startActivity(playerIntent)
    }


    private fun searchDebounce() {
        handler.removeCallbacks(runnable)
        handler.postDelayed(runnable, Constants.CLICK_DEBOUNCE_DELAY)
    }

    companion object {
        const val EDIT_TEXT_STATE = "EDIT_TEXT_STATE"
        const val DEFAULT_EDIT_STATE = ""
    }


}