package com.example.playlistmaker.presentation.ui.search

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import com.example.playlistmaker.R
import com.example.playlistmaker.TrackConsumer
import com.example.playlistmaker.constants.Constants
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.presentation.ui.player.PlayerActivity
import com.google.gson.Gson


class SearchActivity : Activity() {

    private var inputEditTextState: String? = DEFAULT_EDIT_STATE
    private var isClickAllowed = true
//    private lateinit var binding: ActivitySearchBinding


    // Настройка адаптеров
    private val trackListAdapter = TrackAdapter { track ->
        onTrackSelected(track)
    }
    private val historyAdapter =
        TrackAdapter() { track ->
            onTrackSelected(track)
        }

    private val provideTracksSearchController =
        Creator.provideTrackSearchController(this, trackListAdapter, historyAdapter)


    private val handler = Handler(Looper.getMainLooper())
    private val runnable = Runnable { searchTracks() }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        provideTracksSearchController.onCreate()


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

//    // Поиск треков через iTunes API
//    private fun searchTracks() {
//        if (inputEditText.text.isNotEmpty()) {
//            binding.recyclerView.visibility = View.GONE
//            historyLayout.visibility = View.GONE
//            progressBar.visibility = View.VISIBLE
//
//            getTracksUseCase.execute(inputEditText.text.toString(), object : TrackConsumer {
//                override fun onSuccess(response: ArrayList<Track>) {
//                    runOnUiThread {
//
//                        binding.recyclerView.visibility = View.VISIBLE
//                        tracks.addAll(response)
//                        showFoundTracks(response)
//
//                    }
//                }
//
//                override fun onNoResult() {
//                    runOnUiThread {
//                        showMessage(placeholderMessageNotFound)
//                    }
//                }
//
//                override fun onNetworkError() {
//                    runOnUiThread {
//                        showMessage(placeholderMessageNotInternet)
//                    }
//                }
//
//            })
//
//        }
//    }
//
//    // Скрытие клавиатуры
//    private fun hideKeyboard(view: View) {
//        val inputMethodManager =
//            getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
//        inputMethodManager?.hideSoftInputFromWindow(view.windowToken, 0)
//    }
//
//    // Отображение сообщений о состоянии поиска
//    private fun showMessage(linearLayout: LinearLayout) {
//        when (linearLayout) {
//            placeholderMessageNotInternet -> {
//                tracks.clear()
//                placeholderMessageNotInternet.visibility = View.VISIBLE
//                placeholderMessageNotFound.visibility = View.GONE
//                trackListAdapter.notifyDataSetChanged()
//            }
//
//            placeholderMessageNotFound -> {
//                tracks.clear()
//                placeholderMessageNotFound.visibility = View.VISIBLE
//                placeholderMessageNotInternet.visibility = View.GONE
//                historyLayout.visibility = View.GONE
//                trackListAdapter.notifyDataSetChanged()
//            }
//        }
//    }
//
//    private fun showFoundTracks(trackList: ArrayList<Track>) {
//        progressBar.visibility = View.GONE
//        binding.recyclerView.visibility = View.VISIBLE
//        placeholderMessageNotFound.visibility = View.GONE
//        placeholderMessageNotInternet.visibility = View.GONE
//        historyLayout.visibility = View.GONE
//        progressBar.visibility = View.GONE
//        trackListAdapter.trackArrayList = trackList
//        trackListAdapter.notifyDataSetChanged()
//
//    }
//
//    // Отображение истории поиска
//    private fun showHistory() {
//        if (historyAdapter.itemCount > 0) {
//            historyLayout.visibility = View.VISIBLE
//            getHistory()
//        } else {
//            historyLayout.visibility = View.GONE
//        }
//    }
//
//    // Чтение истории поиска
//    private fun getHistory() {
//        historyAdapter.updateTrackList(provideSearchHistoryInteractor.readTracksFromHistory())
//        historyAdapter.notifyDataSetChanged()
//    }
//
//    // Удаление истории поиска
//    private fun clearHistory() {
//        historyAdapter.updateTrackList(tracks)
//        provideSearchHistoryInteractor.clearHistoryPref()
//    }

    // Функция нажатия кнопки выбора трека
    private fun onTrackSelected(track: Track) {
        val currentHistory = provideSearchHistoryInteractor.readTracksFromHistory()
        provideSearchHistoryInteractor.addTrackToHistory(track, currentHistory)
        historyAdapter.updateTrackList(currentHistory)
        val playerIntent = Intent(this, PlayerActivity::class.java)
        playerIntent.putExtra(Constants.TRACK_INTENT, Gson().toJson(track))
        startActivity(playerIntent)
    }


    private fun clickDebounce(): Boolean {
        val current: Boolean = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed(runnable, Constants.CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    companion object {
        const val EDIT_TEXT_STATE = "EDIT_TEXT_STATE"
        const val DEFAULT_EDIT_STATE = ""
    }
}

