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
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import uk.dominikdias.manga.data.Manga
import uk.dominikdias.manga.data.MangaRepository
import uk.dominikdias.manga.data.MangaStatus

class HomeViewModel(
    private val mangaRepository: MangaRepository
) : ViewModel() {

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val allMangaFlow: Flow<List<Manga>> = flow {
        _isLoading.value = true
        _error.value = null
        try {
            mangaRepository.getAllManga().collect { mangaList ->
                emit(mangaList)
                _isLoading.value = false
            }
        } catch (e: Exception) {
            _error.value = e.message ?: "An unknown error occurred"
            _isLoading.value = false
            emit(emptyList())
        }
    }

    val upcomingManga: StateFlow<List<Manga>> = allMangaFlow
        .map { allManga ->
            val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
            allManga.filter {
                it.status == MangaStatus.ORDERED &&
                        it.expectedPublicationDate >= today
            }.sortedBy { it.expectedPublicationDate }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val potentiallyDelayedManga: StateFlow<List<Manga>> = allMangaFlow
        .map { allManga ->
            val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
            allManga.filter {
                it.status == MangaStatus.ORDERED &&
                        it.expectedPublicationDate < today &&
                        it.shippingDate == null
            }.sortedBy { it.expectedPublicationDate }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Function to manually clear errors if needed
    fun clearError() {
        _error.value = null
    }
}

