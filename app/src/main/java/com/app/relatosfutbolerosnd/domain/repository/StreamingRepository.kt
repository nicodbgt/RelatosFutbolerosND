package com.app.relatosfutbolerosnd.domain.repository

import com.app.relatosfutbolerosnd.data.model.StreamConfig
import kotlinx.coroutines.flow.StateFlow

interface StreamingRepository {
    val streamStatus: StateFlow<String>
    suspend fun startStreaming(config: StreamConfig): Result<Boolean>
    suspend fun stopStreaming(): Result<Boolean>
    suspend fun switchCamera(): Result<Boolean>
    suspend fun getStreamStatus(): String
}