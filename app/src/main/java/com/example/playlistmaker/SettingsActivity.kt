package com.example.playlistmaker


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity


class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        val back = findViewById<ImageView>(R.id.back)
        val shareButton = findViewById<LinearLayout>(R.id.share)
        val supportButton = findViewById<LinearLayout>(R.id.support)
        val userAgreementButton = findViewById<LinearLayout>(R.id.userAgreement)

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