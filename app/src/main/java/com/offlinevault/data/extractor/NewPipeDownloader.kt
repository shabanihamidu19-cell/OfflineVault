package com.offlinevault.data.extractor

import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import org.schabi.newpipe.extractor.downloader.Downloader
import org.schabi.newpipe.extractor.downloader.Request
import org.schabi.newpipe.extractor.downloader.Response
import org.schabi.newpipe.extractor.exceptions.ReCaptchaException
import java.util.concurrent.TimeUnit

/**
 * Minimal OkHttp-based Downloader required by NewPipe Extractor.
 */
class NewPipeDownloader private constructor(
    private val client: OkHttpClient
) : Downloader() {

    override fun execute(request: Request): Response {
        val httpMethod = request.httpMethod()
        val url = request.url()
        val headers = request.headers()
        val dataToSend = request.dataToSend()

        val requestBuilder = okhttp3.Request.Builder().url(url)

        headers.forEach { (key, values) ->
            values.forEach { value ->
                requestBuilder.addHeader(key, value)
            }
        }

        val body = dataToSend?.toRequestBody(null)
        requestBuilder.method(httpMethod, body)

        val response = client.newCall(requestBuilder.build()).execute()

        if (response.code == 429) {
            throw ReCaptchaException("reCaptcha Challenge requested", url)
        }

        val responseBody = response.body?.string() ?: ""
        val latestUrl = response.request.url.toString()

        return Response(
            response.code,
            response.message,
            response.headers.toMultimap(),
            responseBody,
            latestUrl
        )
    }

    companion object {
        @Volatile
        private var instance: NewPipeDownloader? = null

        fun getInstance(): NewPipeDownloader {
            return instance ?: synchronized(this) {
                instance ?: NewPipeDownloader(
                    OkHttpClient.Builder()
                        .readTimeout(30, TimeUnit.SECONDS)
                        .build()
                ).also { instance = it }
            }
        }
    }
}
