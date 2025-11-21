package com.app.relatosfutbolerosnd.data.repository

import android.content.Context
import android.content.Intent
import com.app.relatosfutbolerosnd.data.model.StreamConfig
import com.app.relatosfutbolerosnd.domain.repository.StreamingRepository
import com.app.relatosfutbolerosnd.service.RtmpStreamingService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StreamingRepositoryImpl @Inject constructor(
    private val context: Context
) : StreamingRepository {

    private val _streamStatus = MutableStateFlow("No conectado")
    override val streamStatus: StateFlow<String> = _streamStatus

    // Configurar listeners del servicio
    init {
        RtmpStreamingService.listener = object : RtmpStreamingService.StreamingServiceListener {
            override fun onStreamStarted() {
                _streamStatus.value = "Iniciando..."
            }

            override fun onStreamStopped() {
                _streamStatus.value = "Detenido"
            }

            override fun onStreamConnected() {
                _streamStatus.value = "Transmitiendo"
            }

            override fun onStreamClosed() {
                _streamStatus.value = "Conexión cerrada"
            }

            override fun onStreamError(error: String) {
                _streamStatus.value = "Error: $error"
            }
        }
    }

    override suspend fun startStreaming(config: StreamConfig): Result<Boolean> {
        return try {
            val intent = Intent(context, RtmpStreamingService::class.java).apply {
                action = RtmpStreamingService.ACTION_START_STREAM
                putExtra(RtmpStreamingService.EXTRA_STREAM_CONFIG, config)
            }
            context.startService(intent)
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun stopStreaming(): Result<Boolean> {
        return try {
            val intent = Intent(context, RtmpStreamingService::class.java).apply {
                action = RtmpStreamingService.ACTION_STOP_STREAM
            }
            context.startService(intent)
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun switchCamera(): Result<Boolean> {
        return try {
            val intent = Intent(context, RtmpStreamingService::class.java).apply {
                action = RtmpStreamingService.ACTION_SWITCH_CAMERA
            }
            context.startService(intent)
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getStreamStatus(): String {
        return _streamStatus.value
    }
}