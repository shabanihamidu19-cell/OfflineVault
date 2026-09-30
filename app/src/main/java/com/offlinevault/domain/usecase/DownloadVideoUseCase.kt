package com.offlinevault.domain.usecase

import com.offlinevault.domain.model.VideoStream
import com.offlinevault.domain.repository.VideoRepository
import java.io.File
import javax.inject.Inject

class DownloadVideoUseCase @Inject constructor(
    private val repository: VideoRepository
) {
    suspend operator fun invoke(
        videoId: String,
        stream: VideoStream,
        onProgress: (Float) -> Unit = {}
    ): Result<File> {
        return repository.downloadVideo(videoId, stream, onProgress)
    }
}
