package com.offlinevault.domain.model

data class VideoInfo(
    val id: String,
    val title: String,
    val uploader: String,
    val durationSeconds: Long,
    val thumbnailUrl: String?,
    val streams: List<VideoStream>
)

data class VideoStream(
    val url: String,
    val quality: String,          // e.g. "720p", "1080p", "Audio only"
    val height: Int?,             // null for audio
    val format: String,           // mp4, webm, m4a...
    val sizeBytes: Long?,
    val isAudioOnly: Boolean
)
