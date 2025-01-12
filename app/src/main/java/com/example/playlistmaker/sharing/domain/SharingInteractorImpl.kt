package com.example.playlistmaker.sharing.domain

import android.content.Context
import com.example.playlistmaker.R

class SharingInteractorImpl(
    private val externalNavigator: ExternalNavigator,
    val context: Context
) : SharingInteractor {
    override fun shareApp() {
        externalNavigator.shareLink(getshareAppLink())
    }


    override fun openTerms() {
        externalNavigator.openLink(gatTermsLink())
    }


    override fun openSupport() {
        externalNavigator.openEmail(getSupportEmailData())
    }

    private fun getshareAppLink(): String {
        return context.getString(R.string.e_mail_developer)
    }

    private fun gatTermsLink(): String {
        return context.getString(R.string.practicum_link)
    }

    private fun getSupportEmailData(): List<String> {
        val email = context.getString(R.string.e_mail_developer)
        val subject = context.getString(R.string.topic_email)
        val message = context.getString(R.string.message_email)
        return listOf(email, subject, message)
    }
}