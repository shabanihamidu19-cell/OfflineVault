package com.offlinevault.data.downloader

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.offlinevault.domain.model.VideoStream
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DownloadScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val workManager = WorkManager.getInstance(context)

    fun enqueue(
        videoId: String,
        title: String,
        stream: VideoStream
    ) {
        val inputData = workDataOf(
            DownloadWorker.KEY_VIDEO_ID to videoId,
            DownloadWorker.KEY_STREAM_URL to stream.url,
            DownloadWorker.KEY_QUALITY to stream.quality,
            DownloadWorker.KEY_FORMAT to stream.format,
            DownloadWorker.KEY_IS_AUDIO to stream.isAudioOnly,
            DownloadWorker.KEY_TITLE to title
        )

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val request = OneTimeWorkRequestBuilder<DownloadWorker>()
            .setInputData(inputData)
            .setConstraints(constraints)
            .addTag(videoId)
            .build()

        workManager.enqueueUniqueWork(
            DownloadWorker.WORK_NAME_PREFIX + videoId,
            ExistingWorkPolicy.KEEP,
            request
        )
    }

    fun cancel(videoId: String) {
        workManager.cancelUniqueWork(DownloadWorker.WORK_NAME_PREFIX + videoId)
    }
}
