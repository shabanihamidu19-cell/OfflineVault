package com.offlinevault.data.extractor

import com.offlinevault.domain.model.VideoInfo
import com.offlinevault.domain.model.VideoStream
import org.schabi.newpipe.extractor.ServiceList
import org.schabi.newpipe.extractor.stream.StreamInfo
import org.schabi.newpipe.extractor.stream.VideoStream as NewPipeVideoStream
import org.schabi.newpipe.extractor.stream.AudioStream
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Clean wrapper around NewPipe Extractor.
 * Converts NewPipe models → our domain models.
 */
@Singleton
class YoutubeExtractor @Inject constructor() {

    suspend fun extract(url: String): Result<VideoInfo> {
        return try {
            val service = ServiceList.YouTube
            val streamInfo = StreamInfo.getInfo(service, url)

            val videoStreams = streamInfo.videoStreams
                .filter { it.url != null }
                .map { it.toDomain() }

            val audioStreams = streamInfo.audioStreams
                .filter { it.url != null }
                .map { it.toDomainAudio() }

            val info = VideoInfo(
                id = streamInfo.id,
                title = streamInfo.name ?: "Unknown",
                uploader = streamInfo.uploaderName ?: "Unknown",
                durationSeconds = streamInfo.duration,
                thumbnailUrl = streamInfo.thumbnails.firstOrNull()?.url,
                streams = videoStreams + audioStreams
            )

            Result.success(info)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun NewPipeVideoStream.toDomain(): VideoStream {
        return VideoStream(
            url = this.url!!,
            quality = "${this.height}p",
            height = this.height,
            format = this.format?.suffix ?: "mp4",
            sizeBytes = this.itagItem?.contentLength,
            isAudioOnly = false
        )
    }

    private fun AudioStream.toDomainAudio(): VideoStream {
        return VideoStream(
            url = this.url!!,
            quality = "Audio only",
            height = null,
            format = this.format?.suffix ?: "m4a",
            sizeBytes = this.itagItem?.contentLength,
            isAudioOnly = true
        )
    }
}
