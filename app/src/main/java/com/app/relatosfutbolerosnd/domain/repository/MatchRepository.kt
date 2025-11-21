package com.app.relatosfutbolerosnd.domain.repository

import com.app.relatosfutbolerosnd.data.model.MatchInfo
import kotlinx.coroutines.flow.StateFlow

interface MatchRepository {
    val matchInfo: StateFlow<MatchInfo>
    suspend fun startMatch(): Result<Boolean>
    suspend fun pauseMatch(): Result<Boolean>
    suspend fun resetMatch(): Result<Boolean>
    suspend fun updateScore(team1Score: Int, team2Score: Int): Result<Boolean>
    suspend fun getMatchInfo(): MatchInfo
    suspend fun showOverlay(): Result<Boolean>
    suspend fun hideOverlay(): Result<Boolean>
    suspend fun checkOverlayPermission(): Boolean
    suspend fun requestOverlayPermission(): Boolean
}