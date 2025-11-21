package com.app.relatosfutbolerosnd.domain.usecase


import com.app.relatosfutbolerosnd.domain.repository.MatchRepository
import javax.inject.Inject

class ControlMatchUseCase @Inject constructor(
    private val matchRepository: MatchRepository
) {
    suspend fun startMatch() = matchRepository.startMatch()
    suspend fun pauseMatch() = matchRepository.pauseMatch()
    suspend fun resetMatch() = matchRepository.resetMatch()
    suspend fun updateScore(team1Score: Int, team2Score: Int) =
        matchRepository.updateScore(team1Score, team2Score)
    suspend fun getMatchInfo() = matchRepository.getMatchInfo()
}