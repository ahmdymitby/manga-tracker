package uk.dominikdias.manga.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import uk.dominikdias.manga.data.Manga
import uk.dominikdias.manga.data.MangaRepository
import uk.dominikdias.manga.data.MangaStatus

class ReceivedViewModel(
    private val mangaRepository: MangaRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    val receivedManga: StateFlow<List<Manga>> = mangaRepository.getAllManga()
        .map { allManga ->
            allManga
                .filter { it.status == MangaStatus.RECEIVED }
                .sortedByDescending { it.actualPublicationDate ?: it.expectedPublicationDate } // Sort newest first
        }
        .onStart { _isLoading.value = true; _error.value = null } // Handle loading/error reset
        .onEach { _isLoading.value = false } // Stop loading on emission
        .catch { e -> // Handle errors
            _error.value = e.message ?: "Failed to load received manga"
            _isLoading.value = false
            emit(emptyList()) // Emit empty list on error
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun clearError() {
        _error.value = null
    }
}