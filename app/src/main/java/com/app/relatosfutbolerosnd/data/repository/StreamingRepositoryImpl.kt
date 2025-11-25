//package com.app.relatosfutbolerosnd.data.repository
//
//import android.content.Context
//import android.content.Intent
//import com.app.relatosfutbolerosnd.data.model.StreamConfig
//import com.app.relatosfutbolerosnd.domain.repository.StreamingRepository
//import com.app.relatosfutbolerosnd.service.RtmpStreamingService
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import javax.inject.Inject
//import javax.inject.Singleton
//
//@Singleton
//class StreamingRepositoryImpl @Inject constructor(
//    private val context: Context
//) : StreamingRepository {
//
//    private val _streamStatus = MutableStateFlow("No conectado")
//    override val streamStatus: StateFlow<String> = _streamStatus
//
//    // Configurar listeners del servicio
//    init {
//        RtmpStreamingService.listener = object : RtmpStreamingService.StreamingServiceListener {
//            override fun onStreamStarted() {
//                _streamStatus.value = "Iniciando..."
//            }
//
//            override fun onStreamStopped() {
//                _streamStatus.value = "Detenido"
//            }
//
//            override fun onStreamConnected() {
//                _streamStatus.value = "Transmitiendo"
//            }
//
//            override fun onStreamClosed() {
//                _streamStatus.value = "Conexión cerrada"
//            }
//
//            override fun onStreamError(error: String) {
//                _streamStatus.value = "Error: $error"
//            }
//        }
//    }
//
//    override suspend fun startStreaming(config: StreamConfig): Result<Boolean> {
//        return try {
//            val intent = Intent(context, RtmpStreamingService::class.java).apply {
//                action = RtmpStreamingService.ACTION_START_STREAM
//                putExtra(RtmpStreamingService.EXTRA_STREAM_CONFIG, config)
//            }
//            context.startService(intent)
//            Result.success(true)
//        } catch (e: Exception) {
//            Result.failure(e)
//        }
//    }
//
//    override suspend fun stopStreaming(): Result<Boolean> {
//        return try {
//            val intent = Intent(context, RtmpStreamingService::class.java).apply {
//                action = RtmpStreamingService.ACTION_STOP_STREAM
//            }
//            context.startService(intent)
//            Result.success(true)
//        } catch (e: Exception) {
//            Result.failure(e)
//        }
//    }
//
//    override suspend fun switchCamera(): Result<Boolean> {
//        return try {
//            val intent = Intent(context, RtmpStreamingService::class.java).apply {
//                action = RtmpStreamingService.ACTION_SWITCH_CAMERA
//            }
//            context.startService(intent)
//            Result.success(true)
//        } catch (e: Exception) {
//            Result.failure(e)
//        }
//    }
//
//    override suspend fun getStreamStatus(): String {
//        return _streamStatus.value
//    }
//}

package com.app.relatosfutbolerosnd.data.repository

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.core.content.ContextCompat
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

    // 1. Creamos un receptor para escuchar los mensajes del Servicio
    private val serviceReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val status = intent?.getStringExtra("status")
            val message = intent?.getStringExtra("message")

            when (status) {
                "connected" -> _streamStatus.value = "Transmitiendo 🔴"
                "error" -> _streamStatus.value = "Error: $message"
                "closed" -> _streamStatus.value = "Conexión cerrada"
                // Puedes agregar más estados si los envías desde el servicio
            }
        }
    }

    init {
        // 2. Registramos el receptor para empezar a escuchar
        val filter = IntentFilter("com.app.relatosfutbolerosnd.STREAM_STATUS")

        // Usamos ContextCompat para compatibilidad con Android 12+ (flag exportación)
        ContextCompat.registerReceiver(
            context,
            serviceReceiver,
            filter,
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
    }

    override suspend fun startStreaming(config: StreamConfig): Result<Boolean> {
        _streamStatus.value = "Conectando..." // Feedback inmediato
        return try {
            val intent = Intent(context, RtmpStreamingService::class.java).apply {
                action = RtmpStreamingService.ACTION_START_STREAM
                putExtra("stream_url", config.rtmpUrl) // Asegúrate de pasar la URL aquí
                // Si necesitas pasar más config, usa putExtra o Parcelable
            }
            context.startService(intent)
            Result.success(true)
        } catch (e: Exception) {
            _streamStatus.value = "Error al iniciar"
            Result.failure(e)
        }
    }

    override suspend fun stopStreaming(): Result<Boolean> {
        return try {
            val intent = Intent(context, RtmpStreamingService::class.java).apply {
                action = RtmpStreamingService.ACTION_STOP_STREAM
            }
            context.startService(intent)
            _streamStatus.value = "Detenido"
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