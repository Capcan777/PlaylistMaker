package com.example.playlistmaker

import android.content.Context
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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.databinding.ActivitySearchBinding
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
    val tracks = ArrayList<Track>()
    val trackListAdapter = TrackAdapter()
    private lateinit var placeholderMessageNotInternet: LinearLayout
    private lateinit var placeholderMessageNotFound: LinearLayout
    private lateinit var inputEditText: EditText
    private lateinit var clearButton: ImageView
    private lateinit var refreshButton: Button
    private lateinit var back: ImageView

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

        val recyclerView = binding.recyclerView
        trackListAdapter.items = tracks
        recyclerView.adapter = trackListAdapter

        back.setOnClickListener {
            finish()
        }

        clearButton.setOnClickListener {
            inputEditText.setText(getString(R.string.empty_string))
            hideKeyboard(inputEditText)
            tracks.clear()
            trackListAdapter.notifyDataSetChanged()
        }

        inputEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                inputEditTextState = s.toString()
                clearButton.visibility = clearButtonVisibly(s)
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        refreshButton.setOnClickListener {
            searchTracks()
        }

        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchTracks()
                true
            }
            false
        }
    }

    private fun clearButtonVisibly(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(EDIT_TEXT_STATE, inputEditTextState)
    }

    companion object {
        const val EDIT_TEXT_STATE = "EDIT TEXT STATE"
        const val DEFAULT_EDIT_STATE = ""

    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        inputEditTextState = savedInstanceState.getString(EDIT_TEXT_STATE, inputEditTextState)
        inputEditText.setText(inputEditTextState)
    }

    private fun searchTracks() {
        iTunesService.search(inputEditText.text.toString())
            .enqueue(object : Callback<ApiResponse> {
                override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                    if (response.code() == 200) {
                        tracks.clear()

                        if (response.body()?.results?.isNotEmpty() == true) {
                            tracks.addAll(response.body()?.results!!)
                            trackListAdapter.notifyDataSetChanged()
                            placeholderMessageNotFound.visibility = View.GONE
                            placeholderMessageNotInternet.visibility = View.GONE
                        } else {
                            showMessage(placeholderMessageNotFound)
                        }
                    }
                }

                override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                    showMessage(placeholderMessageNotInternet)
                }

            })
    }


    fun hideKeyboard(view: View) {
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(view.windowToken, 0)
    }

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
                trackListAdapter.notifyDataSetChanged()


            }
        }


    }
}