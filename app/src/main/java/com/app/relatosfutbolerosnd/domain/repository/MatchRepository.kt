package com.app.relatosfutbolerosnd.domain.repository

import com.app.relatosfutbolerosnd.data.model.MatchInfo

interface MatchRepository {
    suspend fun startMatch(): Result<Boolean>
    suspend fun pauseMatch(): Result<Boolean>
    suspend fun resetMatch(): Result<Boolean>
    suspend fun updateScore(team1Score: Int, team2Score: Int): Result<Boolean>
    suspend fun showOverlay(matchInfo: MatchInfo): Result<Boolean> // MODIFICADO
    suspend fun hideOverlay(): Result<Boolean>
    suspend fun checkOverlayPermission(): Boolean
    suspend fun requestOverlayPermission(): Boolean
}
