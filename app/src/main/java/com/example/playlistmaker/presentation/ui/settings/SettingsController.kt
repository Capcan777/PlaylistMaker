package com.example.playlistmaker.presentation.ui.settings

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.Switch
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.App
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.databinding.ActivitySettingsBinding
import com.example.playlistmaker.settings.ui.view_model.SettingsViewModel

class SettingsController(private val activity: Activity) {

    private val settingsInteractor = Creator.provideSettingsInteractor()


    private lateinit var back: ImageView
    private lateinit var shareButton: ImageView
    private lateinit var supportButton: ImageView
    private lateinit var userAgreementButton: ImageView
    private lateinit var themeSwitcher: Switch

    fun onCreate() {
        back = activity.findViewById(R.id.back)
        shareButton = activity.findViewById(R.id.share)
        supportButton = activity.findViewById(R.id.support)
        userAgreementButton = activity.findViewById(R.id.userAgreement)
        themeSwitcher = activity.findViewById(R.id.theme_switcher)
    }

    themeSwitcher.isChecked = settingsInteractor.getDarkThemeState()

    themeSwitcher.setOnCheckedChangeListener
    {
        switcher, checked ->
        settingsInteractor.saveDarkThemeState(checked)
        (applicationContext as App).switchTheme(checked)
    }

    back.setOnClickListener
    {
        finish()
    }


    shareButton.setOnClickListener
    {
        val message = getString(R.string.practicum_link)
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, message)
        }
        startActivity(shareIntent)
    }

    supportButton.setOnClickListener
    {
        val topicEmail = getString(R.string.topic_of_email)
        val messageEmail = getString(R.string.text_in_body_email)
        val shareIntent = Intent(Intent.ACTION_SENDTO)
        shareIntent.data = Uri.parse("mailto:")
        shareIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.e_mail_developer)))
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, topicEmail)
        shareIntent.putExtra(Intent.EXTRA_TEXT, messageEmail)
        startActivity(shareIntent)
    }

    userAgreementButton.setOnClickListener
    {
        val linkUserAgreement = getString(R.string.legal_practicum_offer)
        val url = Uri.parse(linkUserAgreement)
        val urlIntent = Intent(Intent.ACTION_VIEW, url)
        startActivity(urlIntent)
    }

    fun onDestroy() {
    }
}
