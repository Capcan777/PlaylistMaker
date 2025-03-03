package com.example.playlistmaker.ui.main

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.R
import com.example.playlistmaker.ui.mediatec.MediatecActivity
import com.example.playlistmaker.ui.search.SearchActivity
import com.example.playlistmaker.ui.settings.SettingsActivity


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val searching = findViewById<Button>(R.id.search)
        val mediateca = findViewById<Button>(R.id.mediateca)
        val settings = findViewById<Button>(R.id.settings)

        settings.setOnClickListener {
            val settingsIntent = Intent(this, SettingsActivity::class.java)
            startActivity(settingsIntent)

        }
        searching.setOnClickListener {
            val searchingIntent = Intent(this, SearchActivity::class.java)
            startActivity(searchingIntent)
        }
        mediateca.setOnClickListener {
            val mediatecaIntent = Intent(this, MediatecActivity::class.java)
            startActivity(mediatecaIntent)
        }
    }
}