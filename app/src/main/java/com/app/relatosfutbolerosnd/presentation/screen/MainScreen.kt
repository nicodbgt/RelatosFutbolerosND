package com.app.relatosfutbolerosnd.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.app.relatosfutbolerosnd.service.RtmpStreamingService
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.relatosfutbolerosnd.ui.theme.RelatosFutbolerosNDTheme
import com.haishinkit.view.HkSurfaceView
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.relatosfutbolerosnd.presentation.component.ControlSection
import com.app.relatosfutbolerosnd.presentation.component.MatchConfigurationSection
import com.app.relatosfutbolerosnd.presentation.component.StreamingConfigurationSection
import com.app.relatosfutbolerosnd.presentation.viewmodel.StreamingViewModel
import com.app.relatosfutbolerosnd.utils.PermissionHelper

@Composable
fun MainScreen(
    viewModel: StreamingViewModel,
    onRequestOverlayPermission: () -> Unit // Mantener por si se usa en el futuro
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var hasPermissions by remember {
        mutableStateOf(PermissionHelper.checkAllPermissions(context))
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasPermissions = permissions.values.all { it }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {Column(modifier = Modifier.padding(16.dp)) {
        // Header de la aplicación
        Text(
            text = "⚽ Relatos Futboleros",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        if(hasPermissions) {
            // 1. VISTA DE LA CÁMARA: Siempre visible para la preview y el stream
            AndroidView(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16 / 9f) // Proporción común de video
                    .padding(16.dp),
                factory = { context ->
                    HkSurfaceView(context).apply{}

                },
                update = { surfaceView -> viewModel.startCameraPreview(surfaceView) }

            )
            DisposableEffect(Unit) {
                onDispose {
                    // ESTO EVITA EL CRASH: Apagamos la cámara al salir
                    viewModel.stopCameraPreview()
                }
            }
        }else {
            // B) SI NO HAY PERMISOS: MOSTRAR BOTÓN
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(9 / 16f)
                    .padding(16.dp)
                    .background(Color.DarkGray, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Button(onClick = {
                    permissionLauncher.launch(PermissionHelper.getRequiredPermissions())
                }) {
                    Text("📸 Activar Cámara")
                }
            }
        }
        // SECCIONES DE CONTROL
        MatchConfigurationSection(
            team1 = state.team1Name,
            team2 = state.team2Name,
            team1Error = state.team1Error,
            team2Error = state.team2Error,
            onTeam1Change = viewModel::updateTeam1,
            onTeam2Change = viewModel::updateTeam2,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        StreamingConfigurationSection(
            streamUrl = state.streamUrl,
            streamUrlError = state.streamUrlError,
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