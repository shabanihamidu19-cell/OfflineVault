package com.offlinevault.presentation.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.offlinevault.domain.repository.VideoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

data class LibraryUiState(
    val videos: List<File> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val repository: VideoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LibraryUiState())
    val uiState: StateFlow<LibraryUiState> = _uiState.asStateFlow()

    init {
        loadVideos()
    }

    fun loadVideos() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val files = repository.getDownloadedVideos()
            _uiState.update { it.copy(videos = files, isLoading = false) }
        }
    }

    fun deleteVideo(file: File) {
        viewModelScope.launch {
            val videoId = file.nameWithoutExtension
            repository.deleteDownloadedVideo(videoId)
            loadVideos()
        }
    }
}
