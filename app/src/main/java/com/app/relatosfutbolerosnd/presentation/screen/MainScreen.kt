package com.app.relatosfutbolerosnd.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import android.content.Intent
import androidx.compose.ui.platform.LocalContext
import com.app.relatosfutbolerosnd.service.RtmpStreamingService
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.relatosfutbolerosnd.presentation.component.ControlSection
import com.app.relatosfutbolerosnd.presentation.component.MatchConfigurationSection
import com.app.relatosfutbolerosnd.presentation.component.MatchControlsSection
import com.app.relatosfutbolerosnd.presentation.component.PreviewOverlay
import com.app.relatosfutbolerosnd.presentation.component.StreamingConfigurationSection
import com.app.relatosfutbolerosnd.presentation.viewmodel.StreamingViewModel
import com.app.relatosfutbolerosnd.ui.theme.RelatosFutbolerosNDTheme
import com.haishinkit.view.HkSurfaceView

@Composable
fun MainScreen(
    viewModel: StreamingViewModel,
    onRequestOverlayPermission: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header de la aplicación
            Text(
                text = "⚽ Relatos Futboleros",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 24.dp)
            )

        // 1. VISTA DE LA CÁMARA: Siempre visible para la preview y el stream
        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16 / 9f) // Proporción común de video
                .padding(16.dp),
            factory = {
                // Creamos la vista aquí
                val surfaceView = HkSurfaceView(it)
                // Se la entregamos al ViewModel para que el servicio la use
                viewModel.setCameraView(surfaceView)
                val intent = Intent(context, RtmpStreamingService::class.java).apply{
                    action = RtmpStreamingService.ACTION_INIT_PREVIEW
                }
                context.startService(intent)

                surfaceView
            }
        )

            // SECCIONES DE CONTROL
            MatchConfigurationSection(
                team1 = state.team1Name,
                team2 = state.team2Name,
                onTeam1Change = viewModel::updateTeam1,
                onTeam2Change = viewModel::updateTeam2,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            StreamingConfigurationSection(
                streamUrl = state.streamUrl,
                onStreamUrlChange = viewModel::updateStreamUrl,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            ControlSection(
                isStreaming = state.isStreaming,
                isOverlayVisible = state.isOverlayVisible,
                onStartStream = viewModel::startStreaming,
                onStopStream = viewModel::stopStreaming,
                onToggleOverlay = viewModel::toggleOverlay,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (state.isStreaming) {
                MatchControlsSection(
                    team1Name = state.team1Name,
                    team2Name = state.team2Name,
                    team1Score = state.team1Score,
                    team2Score = state.team2Score,
                    matchTime = state.matchTime,
                    isMatchRunning = state.isMatchRunning,
                    onStartMatch = viewModel::startMatch,
                    onPauseMatch = viewModel::pauseMatch,
                    onGoalTeam1 = viewModel::incrementTeam1,
                    onGoalTeam2 = viewModel::incrementTeam2,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                if (state.isOverlayVisible) {
                    Text(
                        text = "Vista previa del marcador en stream:",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    PreviewOverlay(
                        team1Name = state.team1Name,
                        team2Name = state.team2Name,
                        team1Score = state.team1Score,
                        team2Score = state.team2Score,
                        matchTime = state.matchTime,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }
            }

            if (state.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            state.errorMessage?.let { error ->
                Text(
                    text = "❌ $error",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MainScreenPreview() {
    RelatosFutbolerosNDTheme {
        MainScreen(viewModel = hiltViewModel(), onRequestOverlayPermission = {})
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MainScreenStreamingPreview() {
    RelatosFutbolerosNDTheme {
        MainScreen(viewModel = hiltViewModel(), onRequestOverlayPermission = {})
    }
}