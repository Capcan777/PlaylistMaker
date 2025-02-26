package com.example.playlistmaker.data.sharing

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.playlistmaker.data.sharing.model.EmailData

class ExternalNavigator(private val context: Context) {
    fun shareLink(textLink: String){
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, textLink)
        }
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(shareIntent)
    }

    fun openLink(textLink: String) {
        val url = Uri.parse(textLink)
        val urlIntent = Intent(Intent.ACTION_VIEW, url)
        urlIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(urlIntent)
    }

    fun openEmail(emailData: EmailData) {
        val shareIntent = Intent(Intent.ACTION_SENDTO)
        shareIntent.data = Uri.parse("mailto:")
        shareIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(emailData.recipient))
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, emailData.subject)
        shareIntent.putExtra(Intent.EXTRA_TEXT, emailData.body)
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(shareIntent)
    }
}