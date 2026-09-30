package com.offlinevault.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.offlinevault.data.downloader.DownloadScheduler
import com.offlinevault.domain.model.VideoInfo
import com.offlinevault.domain.model.VideoStream
import com.offlinevault.domain.usecase.DownloadVideoUseCase
import com.offlinevault.domain.usecase.ExtractVideoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val url: String = "",
    val isLoading: Boolean = false,
    val videoInfo: VideoInfo? = null,
    val selectedStream: VideoStream? = null,
    val downloadProgress: Float = 0f,
    val isDownloading: Boolean = false,
    val error: String? = null,
    val downloadSuccess: Boolean = false,
    val backgroundQueued: Boolean = false
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val extractVideoUseCase: ExtractVideoUseCase,
    private val downloadVideoUseCase: DownloadVideoUseCase,
    private val downloadScheduler: DownloadScheduler
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    fun onUrlChange(url: String) {
        _uiState.update { it.copy(url = url, error = null) }
    }

    fun extract() {
        val url = _uiState.value.url
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    error = null,
                    videoInfo = null,
                    downloadSuccess = false,
                    backgroundQueued = false
                )
            }

            extractVideoUseCase(url)
                .onSuccess { info ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            videoInfo = info,
                            selectedStream = info.streams.firstOrNull { s -> !s.isAudioOnly }
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(isLoading = false, error = e.message ?: "Failed to extract video")
                    }
                }
        }
    }

    fun selectStream(stream: VideoStream) {
        _uiState.update { it.copy(selectedStream = stream) }
    }

    /** Foreground download with live progress in UI */
    fun download() {
        val state = _uiState.value
        val info = state.videoInfo ?: return
        val stream = state.selectedStream ?: return

        viewModelScope.launch {
            _uiState.update {
                it.copy(isDownloading = true, downloadProgress = 0f, error = null, downloadSuccess = false)
            }

            downloadVideoUseCase(info.id, stream) { progress ->
                _uiState.update { it.copy(downloadProgress = progress) }
            }.onSuccess {
                _uiState.update {
                    it.copy(isDownloading = false, downloadProgress = 1f, downloadSuccess = true)
                }
            }.onFailure { e ->
                _uiState.update {
                    it.copy(isDownloading = false, error = e.message ?: "Download failed")
                }
            }
        }
    }

    /** Background download via WorkManager (survives app close) */
    fun downloadInBackground() {
        val state = _uiState.value
        val info = state.videoInfo ?: return
        val stream = state.selectedStream ?: return

        downloadScheduler.enqueue(info.id, info.title, stream)
        _uiState.update {
            it.copy(backgroundQueued = true, error = null)
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
