package com.app.relatosfutbolerosnd.data.repository

import android.content.Context
import android.content.Intent
import android.provider.Settings
import com.app.relatosfutbolerosnd.data.model.MatchInfo
import com.app.relatosfutbolerosnd.domain.repository.MatchRepository
import com.app.relatosfutbolerosnd.service.MatchOverlayService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MatchRepositoryImpl @Inject constructor(
    private val context: Context
) : MatchRepository {

    // Se eliminó el StateFlow y el bloque init. El repositorio ya no guarda estado.

    override suspend fun startMatch(): Result<Boolean> {
        return try {
            val intent = Intent(context, MatchOverlayService::class.java).apply {
                action = MatchOverlayService.ACTION_START_MATCH
            }
            context.startService(intent)
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun pauseMatch(): Result<Boolean> {
        return try {
            val intent = Intent(context, MatchOverlayService::class.java).apply {
                action = MatchOverlayService.ACTION_PAUSE_MATCH
            }
            context.startService(intent)
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun resetMatch(): Result<Boolean> {
        return try {
            val intent = Intent(context, MatchOverlayService::class.java).apply {
                action = MatchOverlayService.ACTION_RESET_MATCH
            }
            context.startService(intent)
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
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun showOverlay(matchInfo: MatchInfo): Result<Boolean> {
        return try {
            if (checkOverlayPermission()) {
                val intent = Intent(context, MatchOverlayService::class.java).apply {
                    action = MatchOverlayService.ACTION_START_OVERLAY
                    // Ahora usamos el matchInfo que viene como parámetro
                    putExtra(MatchOverlayService.EXTRA_TEAM1, matchInfo.team1Name)
                    putExtra(MatchOverlayService.EXTRA_TEAM2, matchInfo.team2Name)
                    putExtra(MatchOverlayService.EXTRA_TEAM1_SCORE, matchInfo.team1Score)
                    putExtra(MatchOverlayService.EXTRA_TEAM2_SCORE, matchInfo.team2Score)
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
        // La petición de permisos se gestiona desde la UI (Activity/Composable)
        return false
    }
}
