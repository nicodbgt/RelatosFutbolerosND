package com.app.relatosfutbolerosnd.domain.usecase

import com.app.relatosfutbolerosnd.data.model.MatchInfo
import com.app.relatosfutbolerosnd.domain.repository.MatchRepository
import javax.inject.Inject

class OverlayUseCase @Inject constructor(
    private val matchRepository: MatchRepository
) {
    suspend fun showOverlay(matchInfo: MatchInfo) = matchRepository.showOverlay(matchInfo)
    suspend fun hideOverlay() = matchRepository.hideOverlay()
    suspend fun checkPermission() = matchRepository.checkOverlayPermission()
    suspend fun requestPermission() = matchRepository.requestOverlayPermission()
}