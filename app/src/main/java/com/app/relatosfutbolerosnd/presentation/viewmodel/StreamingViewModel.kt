package com.app.relatosfutbolerosnd.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.relatosfutbolerosnd.data.model.MatchInfo
import com.app.relatosfutbolerosnd.data.model.StreamConfig
import com.app.relatosfutbolerosnd.data.model.StreamingUiState
import com.app.relatosfutbolerosnd.domain.usecase.ControlMatchUseCase
import com.app.relatosfutbolerosnd.domain.usecase.OverlayUseCase
import com.app.relatosfutbolerosnd.domain.usecase.StartStreamUseCase
import com.app.relatosfutbolerosnd.service.RtmpClient
import com.haishinkit.view.HkSurfaceView
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StreamingViewModel @Inject constructor(
    private val startStreamUseCase: StartStreamUseCase,
    private val controlMatchUseCase: ControlMatchUseCase,
    private val overlayUseCase: OverlayUseCase,
    private val rtmpClient: RtmpClient
) : ViewModel() {

    private val _uiState = MutableStateFlow(StreamingUiState())
    val uiState: StateFlow<StreamingUiState> = _uiState.asStateFlow()

    fun startCameraPreview(surfaceView: HkSurfaceView) {
        rtmpClient.startPreview(surfaceView)
    }

    fun stopCameraPreview() {
        rtmpClient.stopPreview()
    }

    fun updateTeam1(name: String) {
        _uiState.update { it.copy(team1Name = name, team1Error = null) }
    }

    fun updateTeam2(name: String) {
        _uiState.update { it.copy(team2Name = name, team2Error = null) }
    }

    fun updateStreamUrl(url: String) {
        _uiState.update { it.copy(streamUrl = url, streamUrlError = null) }
    }

    fun startStreaming() {
        val state = _uiState.value
        var hasError = false

        val team1Error = if (state.team1Name.isBlank()) "Ingresa el nombre del equipo" else null
        if (team1Error != null) hasError = true

        val team2Error = if (state.team2Name.isBlank()) "Ingresa el nombre del equipo" else null
        if (team2Error != null) hasError = true

        val urlError = if (state.streamUrl.isBlank()) {
            "La URL es obligatoria"
        } else if (!state.streamUrl.startsWith("rtmp://") && !state.streamUrl.startsWith("rtmps://")) {
            "La URL debe comenzar con rtmp:// o rtmps://"
        } else {
            null
        }
        if (urlError != null) hasError = true

        if (hasError) {
            _uiState.update { it.copy(
                team1Error = team1Error,
                team2Error = team2Error,
                streamUrlError = urlError
            )}
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val result = startStreamUseCase(
                StreamConfig(rtmpUrl = _uiState.value.streamUrl)
            )

            result.onSuccess {
                _uiState.update { state ->
                    state.copy(
                        isStreaming = true,
                        isLoading = false,
                        streamStatus = "Transmitiendo"
                    )
                }
                // Mostrar overlay automáticamente al iniciar stream
                toggleOverlay()
            }.onFailure { e ->
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        errorMessage = "Error al iniciar stream: ${e.message}"
                    )
                }
            }
        }
    }

    fun stopStreaming() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // TODO: Implementar stop stream use case
            _uiState.update { state ->
                state.copy(
                    isStreaming = false,
                    isLoading = false,
                    streamStatus = "No conectado"
                )
            }

            if (_uiState.value.isOverlayVisible) {
                toggleOverlay()
            }
        }
    }

    fun startMatch() {
        viewModelScope.launch {
            controlMatchUseCase.startMatch().onSuccess {
                _uiState.update { it.copy(isMatchRunning = true) }
            }
        }
    }

    fun pauseMatch() {
        viewModelScope.launch {
            controlMatchUseCase.pauseMatch().onSuccess {
                _uiState.update { it.copy(isMatchRunning = false) }
            }
        }
    }

    fun incrementTeam1() {
        viewModelScope.launch {
            val newScore = _uiState.value.team1Score + 1
            controlMatchUseCase.updateScore(newScore, _uiState.value.team2Score).onSuccess {
                _uiState.update { it.copy(team1Score = newScore) }
            }
        }
    }

    fun incrementTeam2() {
        viewModelScope.launch {
            val newScore = _uiState.value.team2Score + 1
            controlMatchUseCase.updateScore(_uiState.value.team1Score, newScore).onSuccess {
                _uiState.update { it.copy(team2Score = newScore) }
            }
        }
    }

    fun toggleOverlay() {
        viewModelScope.launch {
            if (_uiState.value.isOverlayVisible) {
                overlayUseCase.hideOverlay().onSuccess {
                    _uiState.update { it.copy(isOverlayVisible = false) }
                }
            } else {
                if (overlayUseCase.checkPermission()) {
                    // Creamos el objeto MatchInfo con el estado actual
                    val matchInfo = MatchInfo(
                        team1Name = _uiState.value.team1Name,
                        team2Name = _uiState.value.team2Name,
                        team1Score = _uiState.value.team1Score,
                        team2Score = _uiState.value.team2Score,
                        matchTime = _uiState.value.matchTime,
                        isMatchRunning = _uiState.value.isMatchRunning
                    )
                    // Y se lo pasamos al caso de uso
                    overlayUseCase.showOverlay(matchInfo).onSuccess {
                        _uiState.update { it.copy(isOverlayVisible = true) }
                    }
                } else {
                    _uiState.update { it.copy(errorMessage = "Se necesita permiso de overlay") }
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
