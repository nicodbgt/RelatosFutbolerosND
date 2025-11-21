package com.app.relatosfutbolerosnd.service

import android.content.Context
import android.hardware.camera2.CameraCharacteristics
import android.util.Log
import com.app.relatosfutbolerosnd.data.model.StreamConfig
import com.haishinkit.event.Event
import com.haishinkit.event.IEventListener
import com.haishinkit.media.AudioRecordSource
import com.haishinkit.media.AudioSource
import com.haishinkit.media.Camera2Source
import com.haishinkit.media.Stream
import com.haishinkit.rtmp.RtmpConnection
import com.haishinkit.rtmp.RtmpStream
import com.haishinkit.view.HkSurfaceView
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RtmpClient @Inject constructor(
    private val context: Context
) : IEventListener {

    private lateinit var connection: RtmpConnection
    private lateinit var stream: RtmpStream
    private lateinit var videoSource: Camera2Source
    private lateinit var audioSource: AudioSource
    private lateinit var surfaceView: HkSurfaceView

    private var listener: Listener? = null
    private var isInitialize = false

    fun initialize(surfaceView: HkSurfaceView, listener: Listener) {

        if(isInitialize) return

        this.surfaceView = surfaceView
        this.listener = listener

        connection = RtmpConnection()
        stream = RtmpStream(context, connection)

        videoSource = Camera2Source(context).apply {
            open(CameraCharacteristics.LENS_FACING_BACK)
        }

        audioSource = AudioRecordSource(context)

        // Configurar y conectar fuentes
        stream.attachAudio(audioSource)
        stream.attachVideo(videoSource)
        connection.addEventListener(Event.RTMP_STATUS, this)
        surfaceView.attachStream(stream)
        isInitialize = true
    }
    fun updateListeener(newListener: Listener){
        this.listener= newListener
    }
    fun start(streamUrl: String, config: StreamConfig, onStreamStarted: (Stream) -> Unit) {
        // Configurar calidad del stream
        stream.audioSetting.bitRate = config.audioBitrate
        stream.videoSetting.width = config.videoWidth
        stream.videoSetting.height = config.videoHeight
        stream.videoSetting.bitRate = config.videoBitrate

        // Conectar y publicar
        val baseUrl = streamUrl.substringBeforeLast("/")
        val streamKey = streamUrl.substringAfterLast("/")

        connection.connect(baseUrl)
        stream.publish(streamKey)
        onStreamStarted.invoke(stream)
    }

    fun stop() {
        try {
            videoSource.close()
            audioSource.stopRunning()
            stream.close()
            connection.close()
        } catch (e: Exception) {
            Log.e("RtmpClient", "Error stopping stream", e)
        }
    }

    fun switchCamera() {
        videoSource.switchCamera()
    }

    override fun handleEvent(event: Event) {
        Log.d("RtmpClient", "Event: ${event.type} Data: ${event.data}")

        when {
            event.data.toString().contains("code=NetConnection.Connect.Success")
                    && event.type == "rtmpStatus" -> {
                listener?.onStreamConnected()
            }
            event.data.toString().contains("code=NetConnection.Connect.Closed")
                    && event.type == "rtmpStatus" -> {
                listener?.onStreamClosed()
            }
            event.data.toString().contains("code=NetConnection.Connect.Failed")
                    && event.type == "rtmpStatus" -> {
                listener?.onStreamError("Error de conexión RTMP")
            }
        }
    }

    interface Listener {
        fun onStreamConnected()
        fun onStreamClosed()
        fun onStreamError(error: String)
    }
}