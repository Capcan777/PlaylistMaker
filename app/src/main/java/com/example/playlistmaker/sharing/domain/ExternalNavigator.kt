package com.example.playlistmaker.sharing.domain

interface ExternalNavigator {
    fun shareLink(textLink: String, topicEmail: String, messageEmail: String)
    fun openLink(urlLink: String)
    fun openEmail(sharingInteractor: SharingInteractor)

}
