package com.example.playlistmaker.data.sharing.repository_impl

import android.content.Context
import com.example.playlistmaker.R
import com.example.playlistmaker.data.sharing.model.EmailData
import com.example.playlistmaker.domain.sharing.SharingRepository

class SharingRepositoryImpl(private val context: Context) : SharingRepository {
    override fun getShareAppLink(): String {
        return context.getString(R.string.practicum_link)
    }

    override fun getTermsLink(): String {
        return context.getString(R.string.legal_practicum_offer)
    }

    override fun getSupportEmailData(): EmailData {
        return EmailData(recipient = context.getString(R.string.e_mail_developer), subject = context.getString(R.string.topic_of_email), body = context.getString(R.string.text_in_body_email))
    }

}