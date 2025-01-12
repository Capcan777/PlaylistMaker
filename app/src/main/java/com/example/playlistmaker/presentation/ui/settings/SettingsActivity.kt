package com.example.playlistmaker.presentation.ui.settings


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.App
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.databinding.ActivitySettingsBinding
import com.example.playlistmaker.settings.ui.view_model.SettingsViewModel


class SettingsActivity : AppCompatActivity() {

//    private lateinit var viewModel: SettingsViewModel

//    private lateinit var binding: ActivitySettingsBinding

    private val settingsController = Creator.provideSettingsController(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        settingsController.onCreate()


//        viewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)

//        binding = ActivitySettingsBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//        val back = binding.back
//        val shareButton = binding.share
//        val supportButton = binding.support
//        val userAgreementButton = binding.userAgreement
//        val themeSwitcher = binding.themeSwitcher
//
//
//        themeSwitcher.isChecked = viewModel.getDarkThemeState()
//
//        themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
//            settingsInteractor.saveDarkThemeState(checked)
//            (applicationContext as App).switchTheme(checked)
//        }
//
//        back.setOnClickListener {
//            finish()
//        }
//
//
//        shareButton.setOnClickListener {
//            val message = getString(R.string.practicum_link)
//            val shareIntent = Intent(Intent.ACTION_SEND).apply {
//                type = "text/plain"
//                putExtra(Intent.EXTRA_TEXT, message)
//            }
//            startActivity(shareIntent)
//        }
//
//        supportButton.setOnClickListener {
//            val topicEmail = getString(R.string.topic_of_email)
//            val messageEmail = getString(R.string.text_in_body_email)
//            val shareIntent = Intent(Intent.ACTION_SENDTO)
//            shareIntent.data = Uri.parse("mailto:")
//            shareIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.e_mail_developer)))
//            shareIntent.putExtra(Intent.EXTRA_SUBJECT, topicEmail)
//            shareIntent.putExtra(Intent.EXTRA_TEXT, messageEmail)
//            startActivity(shareIntent)
//        }
//
//        userAgreementButton.setOnClickListener {
//            val linkUserAgreement = getString(R.string.legal_practicum_offer)
//            val url = Uri.parse(linkUserAgreement)
//            val urlIntent = Intent(Intent.ACTION_VIEW, url)
//            startActivity(urlIntent)
//        }


    }

    override fun onDestroy() {
        super.onDestroy()
        settingsController.onDestroy()
    }
}