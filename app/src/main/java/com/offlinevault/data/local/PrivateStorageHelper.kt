package com.offlinevault.data.local

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Handles all private storage operations.
 * Files are stored in app-internal storage so they never appear in Gallery / Files app.
 */
@Singleton
class PrivateStorageHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val videosDir: File
        get() = File(context.filesDir, "videos").also { it.mkdirs() }

    fun getVideoFile(videoId: String, extension: String = "mp4"): File {
        return File(videosDir, "$videoId.$extension")
    }

    fun videoExists(videoId: String): Boolean {
        return videosDir.listFiles()?.any { it.nameWithoutExtension == videoId } == true
    }

    fun getAllDownloadedVideos(): List<File> {
        return videosDir.listFiles()?.filter { it.isFile }?.toList() ?: emptyList()
    }

    fun deleteVideo(videoId: String): Boolean {
        return videosDir.listFiles()
            ?.filter { it.nameWithoutExtension == videoId }
            ?.all { it.delete() } ?: false
    }

    fun getAvailableSpaceBytes(): Long {
        return context.filesDir.usableSpace
    }
}
