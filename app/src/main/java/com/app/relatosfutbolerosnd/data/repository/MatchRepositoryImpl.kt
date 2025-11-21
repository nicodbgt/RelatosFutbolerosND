package com.app.relatosfutbolerosnd.data.repository

import android.content.Context
import android.content.Intent
import android.provider.Settings
import com.app.relatosfutbolerosnd.data.model.MatchInfo
import com.app.relatosfutbolerosnd.domain.repository.MatchRepository
import com.app.relatosfutbolerosnd.service.MatchOverlayService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MatchRepositoryImpl @Inject constructor(
    private val context: Context
) : MatchRepository {

    private val _matchInfo = MutableStateFlow(
        MatchInfo(
            team1Name = " ",
            team2Name = " "
        )
    )

    override val matchInfo: StateFlow<MatchInfo> = _matchInfo

    // Configurar listener del servicio de overlay
    init {
        MatchOverlayService.listener = object : MatchOverlayService.OverlayServiceListener {
            override fun onOverlayStarted() {
                // Overlay visible
            }

            override fun onOverlayStopped() {
                // Overlay oculto
            }

            override fun onMatchStarted() {
                updateMatchInfo { it.copy(isMatchRunning = true) }
            }

            override fun onMatchPaused() {
                updateMatchInfo { it.copy(isMatchRunning = false) }
            }

            override fun onMatchReset() {
                updateMatchInfo {
                    it.copy(
                        team1Score = 0,
                        team2Score = 0,
                        matchTime = "00:00",
                        isMatchRunning = false,
                        totalSeconds = 0
                    )
                }
            }

            override fun onScoreUpdated(team1Score: Int, team2Score: Int) {
                updateMatchInfo {
                    it.copy(
                        team1Score = team1Score,
                        team2Score = team2Score
                    )
                }
            }

            override fun onTimeUpdated(time: String) {
                updateMatchInfo { it.copy(matchTime = time) }
            }
        }
    }

    private fun updateMatchInfo(update: (MatchInfo) -> MatchInfo) {
        _matchInfo.value = update(_matchInfo.value)
    }

    override suspend fun startMatch(): Result<Boolean> {
        return try {
            // El overlay service maneja el timer internamente
            // Solo actualizamos el estado
            updateMatchInfo { it.copy(isMatchRunning = true) }
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun pauseMatch(): Result<Boolean> {
        return try {
            updateMatchInfo { it.copy(isMatchRunning = false) }
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun resetMatch(): Result<Boolean> {
        return try {
            val intent = Intent(context, MatchOverlayService::class.java).apply {
                action = MatchOverlayService.ACTION_UPDATE_SCORE
                putExtra(MatchOverlayService.EXTRA_TEAM1_SCORE, 0)
                putExtra(MatchOverlayService.EXTRA_TEAM2_SCORE, 0)
            }
            context.startService(intent)

            updateMatchInfo {
                it.copy(
                    team1Score = 0,
                    team2Score = 0,
                    matchTime = "00:00",
                    isMatchRunning = false,
                    totalSeconds = 0
                )
            }
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateScore(team1Score: Int, team2Score: Int): Result<Boolean> {
        return try {
            val intent = Intent(context, MatchOverlayService::class.java).apply {
                action = MatchOverlayService.ACTION_UPDATE_SCORE
                putExtra(MatchOverlayService.EXTRA_TEAM1_SCORE, team1Score)
                putExtra(MatchOverlayService.EXTRA_TEAM2_SCORE, team2Score)
            }
            context.startService(intent)

            updateMatchInfo {
                it.copy(
                    team1Score = team1Score,
                    team2Score = team2Score
                )
            }
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getMatchInfo(): MatchInfo {
        return _matchInfo.value
    }

    override suspend fun showOverlay(): Result<Boolean> {
        return try {
            if (checkOverlayPermission()) {
                val currentInfo = _matchInfo.value
                val intent = Intent(context, MatchOverlayService::class.java).apply {
                    action = MatchOverlayService.ACTION_START_OVERLAY
                    putExtra(MatchOverlayService.EXTRA_TEAM1, currentInfo.team1Name)
                    putExtra(MatchOverlayService.EXTRA_TEAM2, currentInfo.team2Name)
                    putExtra(MatchOverlayService.EXTRA_TEAM1_SCORE, currentInfo.team1Score)
                    putExtra(MatchOverlayService.EXTRA_TEAM2_SCORE, currentInfo.team2Score)
                }
                context.startService(intent)
                Result.success(true)
            } else {
                Result.failure(Exception("No overlay permission"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun hideOverlay(): Result<Boolean> {
        return try {
            val intent = Intent(context, MatchOverlayService::class.java).apply {
                action = MatchOverlayService.ACTION_STOP_OVERLAY
            }
            context.startService(intent)
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun checkOverlayPermission(): Boolean {
        return Settings.canDrawOverlays(context)
    }

    override suspend fun requestOverlayPermission(): Boolean {
        // Esto se maneja en la Activity
        return false
    }
}
