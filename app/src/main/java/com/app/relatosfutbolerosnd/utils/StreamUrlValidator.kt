package com.app.relatosfutbolerosnd.utils

object StreamUrlValidator {

    fun isValidRtmpUrl(url: String): Boolean {
        return url.startsWith("rtmp://") || url.startsWith("rtmps://")
    }

    fun extractStreamKey(url: String): String {
        return url.substringAfterLast("/")
    }

    fun extractBaseUrl(url: String): String {
        return url.substringBeforeLast("/")
    }

    fun validateYouTubeUrl(url: String): Boolean {
        return url.contains("youtube.com/live2") || url.contains("youtube.com/live")
    }

    fun validateTwitchUrl(url: String): Boolean {
        return url.contains("twitch.tv") || url.contains("live-twitch")
    }

    fun getPlatformName(url: String): String {
        return when {
            validateYouTubeUrl(url) -> "YouTube"
            validateTwitchUrl(url) -> "Twitch"
            url.contains("facebook") -> "Facebook"
            else -> "RTMP Server"
        }
    }
}