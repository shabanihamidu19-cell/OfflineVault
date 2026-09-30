package com.offlinevault.data.repository

import com.offlinevault.data.extractor.YoutubeExtractor
import com.offlinevault.data.local.PrivateStorageHelper
import com.offlinevault.domain.model.VideoInfo
import com.offlinevault.domain.model.VideoStream
import com.offlinevault.domain.repository.VideoRepository
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VideoRepositoryImpl @Inject constructor(
    private val extractor: YoutubeExtractor,
    private val storageHelper: PrivateStorageHelper,
    private val okHttpClient: OkHttpClient
) : VideoRepository {

    override suspend fun extractVideoInfo(url: String): Result<VideoInfo> {
        return extractor.extract(url)
    }

    override suspend fun downloadVideo(
        videoId: String,
        stream: VideoStream,
        onProgress: (Float) -> Unit
    ): Result<File> {
        return try {
            val request = Request.Builder()
                .url(stream.url)
                .build()

            val response = okHttpClient.newCall(request).execute()

            if (!response.isSuccessful) {
                return Result.failure(Exception("Download failed: HTTP ${response.code}"))
            }

            val body = response.body ?: return Result.failure(Exception("Empty response body"))
            val totalBytes = body.contentLength()
            val file = storageHelper.getVideoFile(videoId, stream.format)

            FileOutputStream(file).use { output ->
                val input = body.byteStream()
                val buffer = ByteArray(8 * 1024)
                var downloaded = 0L
                var read: Int

                while (input.read(buffer).also { read = it } != -1) {
                    output.write(buffer, 0, read)
                    downloaded += read

                    if (totalBytes > 0) {
                        onProgress(downloaded.toFloat() / totalBytes)
                    }
                }
            }

            Result.success(file)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getDownloadedVideos(): List<File> {
        return storageHelper.getAllDownloadedVideos()
    }

    override fun deleteDownloadedVideo(videoId: String): Boolean {
        return storageHelper.deleteVideo(videoId)
    }
}
