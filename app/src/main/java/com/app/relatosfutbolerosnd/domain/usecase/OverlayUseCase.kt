package com.app.relatosfutbolerosnd.domain.usecase

import com.app.relatosfutbolerosnd.domain.repository.MatchRepository
import javax.inject.Inject

class OverlayUseCase @Inject constructor(
    private val matchRepository: MatchRepository
) {
    suspend fun showOverlay() = matchRepository.showOverlay()
    suspend fun hideOverlay() = matchRepository.hideOverlay()
    suspend fun checkPermission() = matchRepository.checkOverlayPermission()
    suspend fun requestPermission() = matchRepository.requestOverlayPermission()
}