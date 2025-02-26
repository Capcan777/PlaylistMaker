package com.example.playlistmaker.domain.sharing.impl

import com.example.playlistmaker.data.sharing.ExternalNavigator
import com.example.playlistmaker.domain.sharing.SharingInteractor
import com.example.playlistmaker.domain.sharing.SharingRepository

class SharingInteractorImpl(private val externalNavigator: ExternalNavigator, private val sharingRepository: SharingRepository) : SharingInteractor {
    override fun shareLink() {
        externalNavigator.shareLink(sharingRepository.getShareAppLink())
    }

    override fun openTerms() {
        externalNavigator.openLink(sharingRepository.getTermsLink())
    }

    override fun openSupport() {
        externalNavigator.openEmail(sharingRepository.getSupportEmailData())
    }
}