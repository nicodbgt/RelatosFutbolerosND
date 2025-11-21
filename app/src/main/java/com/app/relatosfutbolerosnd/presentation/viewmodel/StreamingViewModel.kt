package com.app.relatosfutbolerosnd.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.relatosfutbolerosnd.data.model.StreamConfig
import com.app.relatosfutbolerosnd.data.model.StreamingUiState
import com.app.relatosfutbolerosnd.domain.usecase.ControlMatchUseCase
import com.app.relatosfutbolerosnd.domain.usecase.OverlayUseCase
import com.app.relatosfutbolerosnd.domain.usecase.StartStreamUseCase
import com.app.relatosfutbolerosnd.service.RtmpStreamingService
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
    private val overlayUseCase: OverlayUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(StreamingUiState())
    val uiState: StateFlow<StreamingUiState> = _uiState.asStateFlow()

    // 🎥 ASIGNAR VISTA DE LA CÁMARA
    fun setCameraView(surfaceView: HkSurfaceView) {
        // Asignamos la vista de la cámara al servicio para que pueda usarla
        RtmpStreamingService.surfaceView = surfaceView
    }

    // 🎯 ACTUALIZACIONES DE CONFIGURACIÓN
    fun updateTeam1(name: String) {
        _uiState.update { it.copy(team1Name = name) }
    }

    fun updateTeam2(name: String) {
        _uiState.update { it.copy(team2Name = name) }
    }

    fun updateStreamUrl(url: String) {
        _uiState.update { it.copy(streamUrl = url) }
    }

    // 📹 CONTROL DE STREAMING
    fun startStreaming() {
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

            // Ocultar overlay al detener stream
            if (_uiState.value.isOverlayVisible) {
                toggleOverlay()
            }
        }
    }

    // ⚽ CONTROL DE PARTIDO
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

    // 🖥️ CONTROL DE OVERLAY
    fun toggleOverlay() {
        viewModelScope.launch {
            if (_uiState.value.isOverlayVisible) {
                overlayUseCase.hideOverlay().onSuccess {
                    _uiState.update { it.copy(isOverlayVisible = false) }
                }
            } else {
                // Verificar permisos primero
                if (overlayUseCase.checkPermission()) {
                    overlayUseCase.showOverlay().onSuccess {
                        _uiState.update { it.copy(isOverlayVisible = true) }
                    }
                } else {
                    _uiState.update { it.copy(errorMessage = "Se necesita permiso de overlay") }
                }
            }
        }
    }

    // 🧹 LIMPIAR ERRORES
    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}