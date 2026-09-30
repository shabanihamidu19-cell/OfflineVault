package com.offlinevault.domain.repository

import com.offlinevault.domain.model.VideoInfo
import com.offlinevault.domain.model.VideoStream
import java.io.File

interface VideoRepository {
    suspend fun extractVideoInfo(url: String): Result<VideoInfo>
    suspend fun downloadVideo(
        videoId: String,
        stream: VideoStream,
        onProgress: (Float) -> Unit
    ): Result<File>
    fun getDownloadedVideos(): List<File>
    fun deleteDownloadedVideo(videoId: String): Boolean
}
