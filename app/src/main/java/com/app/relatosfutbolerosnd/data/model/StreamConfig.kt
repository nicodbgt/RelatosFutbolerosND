package com.app.relatosfutbolerosnd.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class StreamConfig(
    val rtmpUrl: String = "",
    val streamKey: String = "",
    val videoWidth: Int = 1280,
    val videoHeight: Int = 720,
    val videoBitrate: Int = 4000000,
    val audioBitrate: Int = 128000
) : Parcelable