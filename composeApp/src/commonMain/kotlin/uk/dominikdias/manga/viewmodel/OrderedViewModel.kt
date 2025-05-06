package uk.dominikdias.manga.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import uk.dominikdias.manga.data.Manga
import uk.dominikdias.manga.data.MangaRepository
import uk.dominikdias.manga.data.MangaStatus

class OrderedViewModel(
    private val mangaRepository: MangaRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val allMangaFlow: Flow<List<Manga>> = flow {
        _isLoading.value = true
        _error.value = null
        try {
            mangaRepository.getAllManga().collect { mangaList ->
                emit(mangaList)
                _isLoading.value = false
            }
        } catch (e: Exception) {
            _error.value = e.message ?: "Failed to load manga"
            _isLoading.value = false
            emit(emptyList())
        }
    }

    val orderedManga: StateFlow<List<Manga>> = allMangaFlow
        .map { allManga ->
            allManga
                .filter { it.status == MangaStatus.ORDERED }
                .sortedBy { it.expectedPublicationDate }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val shippedManga: StateFlow<List<Manga>> = allMangaFlow
        .map { allManga ->
            allManga
                .filter { it.status == MangaStatus.SHIPPED }
                .sortedBy { it.shippingDate ?: it.expectedPublicationDate }
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
