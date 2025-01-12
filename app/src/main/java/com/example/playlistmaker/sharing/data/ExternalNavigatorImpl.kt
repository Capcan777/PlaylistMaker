package com.example.playlistmaker.sharing.data

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.playlistmaker.R
import com.example.playlistmaker.sharing.domain.ExternalNavigator
import com.example.playlistmaker.sharing.domain.SharingInteractor

class ExternalNavigatorImpl(val context: Context) : ExternalNavigator {
    override fun shareLink(emailAdress: String, topicEmail: String, messageEmail: String) {
        val shareIntent = Intent(Intent.ACTION_SENDTO)
        shareIntent.data = Uri.parse("mailto:")
        shareIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(emailAdress))
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, topicEmail)
        shareIntent.putExtra(Intent.EXTRA_TEXT, messageEmail)
        context.startActivity(shareIntent)

    }

    override fun openLink(urlLink: String) {
        val url = Uri.parse(urlLink)
        val urlIntent = Intent(Intent.ACTION_VIEW, url)
        context.startActivity(urlIntent)
    }

    override fun openEmail(sharingInteractor: SharingInteractor) {
        TODO("Not yet implemented")
    }
}