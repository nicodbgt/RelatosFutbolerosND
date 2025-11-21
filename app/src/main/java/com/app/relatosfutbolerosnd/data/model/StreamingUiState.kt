package com.app.relatosfutbolerosnd.data.model

data class StreamingUiState(
    // Configuración Stream
    val streamUrl: String = "",
    val isStreaming: Boolean = false,
    val streamStatus: String = "No conectado",

    // Configuración Partido
    val team1Name: String = " ",
    val team2Name: String = " ",
    val team1Score: Int = 0,
    val team2Score: Int = 0,
    val matchTime: String = "00:00",
    val isMatchRunning: Boolean = false,

    // Overlay
    val isOverlayVisible: Boolean = false,
    val hasOverlayPermission: Boolean = false,

    // Estados de carga/error
    val isLoading: Boolean = false,
    val errorMessage: String? = null,

    // CAMPOS PARA VALIDACIÓN DE ERRORES
    val team1Error: String? = null,
    val team2Error: String? = null,
    val streamUrlError: String? = null
)
