package com.app.relatosfutbolerosnd.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.app.relatosfutbolerosnd.R
import com.app.relatosfutbolerosnd.data.model.StreamConfig
import com.app.relatosfutbolerosnd.service.RtmpClient
import com.haishinkit.media.Stream
import com.haishinkit.view.HkSurfaceView
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Singleton

@AndroidEntryPoint
@Singleton
class RtmpStreamingService : Service(), RtmpClient.Listener {

    companion object {
        var surfaceView: HkSurfaceView? = null
        var isServiceRunning = false
        var isStreamOn = false
        var listener: StreamingServiceListener? = null
        var currentUrl = ""
        var localStream: Stream? = null

        const val ACTION_START_STREAM = "START_STREAM"
        const val ACTION_STOP_STREAM = "STOP_STREAM"
        const val ACTION_SWITCH_CAMERA = "SWITCH_CAMERA"
        const val EXTRA_STREAM_CONFIG = "EXTRA_STREAM_CONFIG"

        private val FOREGROUND_SERVICE_TYPES = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            ServiceInfo.FOREGROUND_SERVICE_TYPE_CAMERA or
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE or
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION
        } else {
            0
        }
    }

    private lateinit var notificationBuilder: NotificationCompat.Builder
    private lateinit var notificationManager: NotificationManager

    @Inject
    lateinit var rtmpClient: RtmpClient

    override fun onCreate() {
        super.onCreate()
        setupNotification()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        intent?.let { incomingIntent ->
            when (incomingIntent.action) {
                ACTION_START_STREAM -> handleStartStream(intent)
                ACTION_STOP_STREAM -> handleStopStream()
                ACTION_SWITCH_CAMERA -> handleSwitchCamera()
            }
        }

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun handleStartStream(intent: Intent) {
        if (surfaceView == null) {
            Log.e("RtmpStreamingService", "Error: No se puede iniciar el stream porque HkSurfaceView es nula.")
            listener?.onStreamError("La vista de la cámara no está lista.")
            stopSelf()
            return
        }

        if (!isServiceRunning) {
            isServiceRunning = true

            val config = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(EXTRA_STREAM_CONFIG, StreamConfig::class.java)
            } else {
                @Suppress("DEPRECATION")
                intent.getParcelableExtra(EXTRA_STREAM_CONFIG)
            } ?: StreamConfig()

            currentUrl = "${config.rtmpUrl}/${config.streamKey}"

            rtmpClient.initialize(surfaceView!!, this)
            rtmpClient.start(currentUrl, config) { stream ->
                localStream = stream
                startServiceWithNotification()
            }

            listener?.onStreamStarted()
        }
    }

    fun handleStopStream() {
        stopForeground(STOP_FOREGROUND_REMOVE)
        isServiceRunning = false
        isStreamOn = false

        rtmpClient.stop()
        // NO anular la vista, la UI sigue siendo la propietaria
        // surfaceView = null 

        listener?.onStreamStopped()
        stopSelf()
    }

    private fun handleSwitchCamera() {
        rtmpClient.switchCamera()
    }

    private fun setupNotification() {
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificationChannel = NotificationChannel(
            "streaming_channel",
            "Transmisión en Vivo",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Notificación para transmisión RTMP en vivo"
        }

        val exitIntent = Intent(this, RtmpBroadcastReceiver::class.java).apply {
            action = RtmpBroadcastReceiver.ACTION_EXIT
        }
        val pendingIntent = PendingIntent.getBroadcast(
            this, 0, exitIntent, PendingIntent.FLAG_IMMUTABLE
        )

        notificationManager.createNotificationChannel(notificationChannel)

        notificationBuilder = NotificationCompat.Builder(this, "streaming_channel")
            .setSmallIcon(R.drawable.ic_sports_soccer)
            .setContentTitle("Relatos Futboleros")
            .setContentText("Transmitiendo en vivo")
            .setOngoing(true)
            .addAction(R.drawable.ic_stop, "Finalizar", pendingIntent)
    }

    @Suppress("DEPRECATION")
    private fun startServiceWithNotification() {
        val notification = notificationBuilder.build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(1, notification, FOREGROUND_SERVICE_TYPES)
        } else {
            startForeground(1, notification)
        }
    }

    // Callbacks del cliente RTMP
    override fun onStreamConnected() {
        isStreamOn = true
        listener?.onStreamConnected()
    }

    override fun onStreamClosed() {
        isStreamOn = false
        listener?.onStreamClosed()
    }

    override fun onStreamError(error: String) {
        listener?.onStreamError(error)
    }

    interface StreamingServiceListener {
        fun onStreamStarted()
        fun onStreamStopped()
        fun onStreamConnected()
        fun onStreamClosed()
        fun onStreamError(error: String)
    }
}