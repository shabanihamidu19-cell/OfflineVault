package com.offlinevault.domain.usecase

import com.offlinevault.domain.model.VideoInfo
import com.offlinevault.domain.repository.VideoRepository
import javax.inject.Inject

class ExtractVideoUseCase @Inject constructor(
    private val repository: VideoRepository
) {
    suspend operator fun invoke(url: String): Result<VideoInfo> {
        if (url.isBlank()) {
            return Result.failure(IllegalArgumentException("URL cannot be empty"))
        }
        if (!url.contains("youtube.com") && !url.contains("youtu.be")) {
            return Result.failure(IllegalArgumentException("Only YouTube links are supported"))
        }
        return repository.extractVideoInfo(url.trim())
    }
}
