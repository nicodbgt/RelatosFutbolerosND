package com.app.relatosfutbolerosnd.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.app.relatosfutbolerosnd.R
import com.app.relatosfutbolerosnd.data.model.StreamConfig
import com.app.relatosfutbolerosnd.presentation.screen.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Singleton

@AndroidEntryPoint
@Singleton
class RtmpStreamingService : Service(), RtmpClient.Listener {

    @Inject
    lateinit var rtmpClient: RtmpClient

    companion object {
        const val ACTION_START_STREAM = "ACTION_START_STREAM"
        const val ACTION_STOP_STREAM = "ACTION_STOP_STREAM"
        const val ACTION_SWITCH_CAMERA = "ACTION_SWITCH_CAMERA"

        const val CHANNEL_ID = "RtmpStreamChannel"
        const val NOTIFICATION_ID = 1
        private const val TAG = "RtmpStreamingService"

        var isServiceRunning = false
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        Log.d(TAG, "onStartCommand: $action")

        when (action) {
            ACTION_START_STREAM -> handleStartStream(intent)
            ACTION_STOP_STREAM -> handleStopStream()
            ACTION_SWITCH_CAMERA -> handleSwitchCamera()
        }

        return START_NOT_STICKY
    }

    private fun handleStartStream(intent: Intent) {
        val streamUrl = intent.getStringExtra("stream_url")

        if (streamUrl.isNullOrBlank()) {
            Log.e(TAG, "Error: URL de stream vacía.")
            stopSelf()
            return
        }

        if (!isServiceRunning) {
            Log.d(TAG, "Iniciando servicio de streaming...")
            isServiceRunning = true

            startServiceWithNotification()

            val config = StreamConfig(
                videoWidth = 720,
                videoHeight = 1280,
                videoBitrate = 2500 * 1000,
                audioBitrate = 128 * 1000
            )

            rtmpClient.setListener(this)

            // CORRECCIÓN: La lambda final se ha eliminado.
            rtmpClient.start(streamUrl, config)
        }
    }

    private fun handleStopStream() {
        Log.d(TAG, "Deteniendo servicio...")
        try {
            rtmpClient.stop()

        } catch (e: Exception) {
            Log.e(TAG, "Error deteniendo cliente", e)
        }

        isServiceRunning = false
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun handleSwitchCamera() {
        rtmpClient.switchCamera()
    }

    private fun startServiceWithNotification() {
        createNotificationChannel()

        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val stopIntent = Intent(this, RtmpBroadcastReceiver::class.java).apply {
            action = ACTION_STOP_STREAM
        }
        val stopPendingIntent = PendingIntent.getBroadcast(
            this, 0, stopIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Transmitiendo en vivo")
            .setContentText("Relatos Futboleros está en directo 🔴")
            .setSmallIcon(R.drawable.ic_live_tv)
            .setContentIntent(pendingIntent)
            .addAction(R.drawable.ic_stop, "Finalizar", stopPendingIntent)
            .setOngoing(true)
            .build()

        startForeground(NOTIFICATION_ID, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Canal de Streaming RTMP",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    // --- Implementación de RtmpClient.Listener ---

    override fun onStreamConnected() {
        Log.d(TAG, "Conexión RTMP exitosa")
    }

    override fun onStreamClosed() {
        Log.d(TAG, "Conexión RTMP cerrada")
        handleStopStream()
    }

    override fun onStreamError(error: String) {
        Log.e(TAG, "Error de RTMP: $error")
        handleStopStream()
    }
}
