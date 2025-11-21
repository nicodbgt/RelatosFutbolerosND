package com.app.relatosfutbolerosnd.domain.usecase

import com.app.relatosfutbolerosnd.data.model.StreamConfig
import com.app.relatosfutbolerosnd.domain.repository.StreamingRepository
import javax.inject.Inject

class StartStreamUseCase @Inject constructor(
    private val streamingRepository: StreamingRepository
) {
    suspend operator fun invoke(config: StreamConfig): Result<Boolean> {
        return streamingRepository.startStreaming(config)
    }
}