package com.app.relatosfutbolerosnd.service

import android.content.Context
import android.hardware.camera2.CameraCharacteristics
import android.util.Log
import com.app.relatosfutbolerosnd.data.model.StreamConfig
import com.haishinkit.media.AudioSource
import com.haishinkit.media.Camera2Source
import com.haishinkit.media.Stream
import com.haishinkit.view.HkSurfaceView
import com.haishinkit.event.Event
import com.haishinkit.event.IEventListener
import com.haishinkit.media.AudioRecordSource
import com.haishinkit.rtmp.RtmpConnection
import com.haishinkit.rtmp.RtmpStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RtmpClient @Inject constructor(
    private val context: Context
) : IEventListener {

    private var connection: RtmpConnection? = null
    private var stream: RtmpStream? = null
    private var videoSource: Camera2Source? = null
    private var audioSource: AudioSource? = null
    private var surfaceView: HkSurfaceView? = null
    private var listener: Listener? = null
    private var isStreaming = false

    private fun initialize() {
        if (connection == null) {
            connection = RtmpConnection().apply {
                addEventListener(Event.RTMP_STATUS, this@RtmpClient)
            }
        }
        if (stream == null) {
            stream = RtmpStream(context, connection!!)
        }
        if (audioSource == null) {
            audioSource = AudioRecordSource(context)
            stream?.attachAudio(audioSource)
        }
        if (videoSource == null) {
            videoSource = Camera2Source(context)
            stream?.attachVideo(videoSource)
        }
    }

    fun setListener(listener: Listener?) {
        this.listener = listener
    }

    fun startPreview(surfaceView: HkSurfaceView, facing: Int = CameraCharacteristics.LENS_FACING_BACK) {
        this.surfaceView = surfaceView
        initialize()
        videoSource?.open(facing)
        this.surfaceView?.attachStream(stream)
    }

    fun stopPreview() {
        surfaceView?.attachStream(null)
        surfaceView = null
        videoSource?.close()
    }

    fun start(streamUrl: String, config: StreamConfig) {
        isStreaming = true
        initialize()
        stream?.apply {
            audioSetting.bitRate = config.audioBitrate
            videoSetting.width = config.videoWidth
            videoSetting.height = config.videoHeight
            videoSetting.bitRate = config.videoBitrate
        }

        val baseUrl = streamUrl.substringBeforeLast("/")
        val streamKey = streamUrl.substringAfterLast("/")

        connection?.connect(baseUrl)
        stream?.publish(streamKey)
    }

    fun stop() {
        if (!isStreaming) return
        isStreaming = false
        try {
            connection?.close()
            // Don't close the stream here if you want to reuse it.
            // stream?.close()
        } catch (e: Exception) {
            Log.e("RtmpClient", "Error stopping stream", e)
        }
    }

    fun switchCamera() {
        videoSource?.switchCamera()
    }

    override fun handleEvent(event: Event) {
        Log.d("RtmpClient", "Event: ${event.type} Data: ${event.data}")

        val data = event.data as? Map<*, *>
        when (data?.get("code")) {
            "NetConnection.Connect.Success" -> {
                listener?.onStreamConnected()
            }
            "NetConnection.Connect.Closed" -> {
                listener?.onStreamClosed()
            }
            "NetConnection.Connect.Failed" -> {
                listener?.onStreamError("Error de conexión RTMP")
            }
        }
    }

    fun release() {
        videoSource?.close()
        videoSource = null
        audioSource?.stopRunning()
        audioSource = null
        stream?.close()
        stream = null
        connection?.close()
        connection = null
    }

    interface Listener {
        fun onStreamConnected()
        fun onStreamClosed()
        fun onStreamError(error: String)
    }
}