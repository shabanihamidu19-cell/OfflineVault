package com.offlinevault.data.downloader

import android.content.Context
import android.content.pm.ServiceInfo
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.offlinevault.R
import com.offlinevault.domain.model.VideoStream
import com.offlinevault.domain.repository.VideoRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
class DownloadWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted params: WorkerParameters,
    private val repository: VideoRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val videoId = inputData.getString(KEY_VIDEO_ID) ?: return@withContext Result.failure()
        val streamUrl = inputData.getString(KEY_STREAM_URL) ?: return@withContext Result.failure()
        val quality = inputData.getString(KEY_QUALITY) ?: "unknown"
        val format = inputData.getString(KEY_FORMAT) ?: "mp4"
        val isAudioOnly = inputData.getBoolean(KEY_IS_AUDIO, false)
        val title = inputData.getString(KEY_TITLE) ?: "Video"

        val stream = VideoStream(
            url = streamUrl,
            quality = quality,
            height = null,
            format = format,
            sizeBytes = null,
            isAudioOnly = isAudioOnly
        )

        setForeground(createForegroundInfo(title, 0))

        val result = repository.downloadVideo(videoId, stream) { progress ->
            val percent = (progress * 100).toInt()
            setProgressAsync(workDataOf(KEY_PROGRESS to percent))
            // Update notification periodically would go here
        }

        result.fold(
            onSuccess = { Result.success() },
            onFailure = { Result.failure() }
        )
    }

    private fun createForegroundInfo(title: String, progress: Int): ForegroundInfo {
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("Downloading")
            .setContentText(title)
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setOngoing(true)
            .setProgress(100, progress, progress == 0)
            .build()

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ForegroundInfo(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC)
        } else {
            ForegroundInfo(NOTIFICATION_ID, notification)
        }
    }

    companion object {
        const val KEY_VIDEO_ID = "video_id"
        const val KEY_STREAM_URL = "stream_url"
        const val KEY_QUALITY = "quality"
        const val KEY_FORMAT = "format"
        const val KEY_IS_AUDIO = "is_audio"
        const val KEY_TITLE = "title"
        const val KEY_PROGRESS = "progress"
        const val CHANNEL_ID = "download_channel"
        const val NOTIFICATION_ID = 1001
        const val WORK_NAME_PREFIX = "download_"
    }
}
