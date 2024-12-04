package com.example.playlistmaker.presentation.ui.settings


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.App
import com.example.playlistmaker.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivitySettingsBinding


class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val settingsInteractor = Creator.

        val back = binding.back
        val shareButton = binding.share
        val supportButton = binding.support
        val userAgreementButton = binding.userAgreement
        val themeSwitcher = binding.themeSwitcher

        themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            (applicationContext as App).switchTheme(checked)
        }

        back.setOnClickListener {
            finish()
        }


        shareButton.setOnClickListener {
            val message = getString(R.string.practicum_link)
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, message)
            }
            startActivity(shareIntent)
        }

        supportButton.setOnClickListener {
            val topicEmail = getString(R.string.topic_of_email)
            val messageEmail = getString(R.string.text_in_body_email)
            val shareIntent = Intent(Intent.ACTION_SENDTO)
            shareIntent.data = Uri.parse("mailto:")
            shareIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.e_mail_developer)))
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, topicEmail)
            shareIntent.putExtra(Intent.EXTRA_TEXT, messageEmail)
            startActivity(shareIntent)
        }

        userAgreementButton.setOnClickListener {
            val linkUserAgreement = getString(R.string.legal_practicum_offer)
            val url = Uri.parse(linkUserAgreement)
            val urlIntent = Intent(Intent.ACTION_VIEW, url)
            startActivity(urlIntent)
        }


    }
}