package uk.dominikdias.manga.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import uk.dominikdias.manga.data.Manga
import uk.dominikdias.manga.data.MangaRepository
import uk.dominikdias.manga.data.MangaStatus
import uk.dominikdias.manga.model.AddMangaFormState

class AddMangaViewModel(
    private val mangaRepository: MangaRepository
) : ViewModel() {

    private val _formState = MutableStateFlow(AddMangaFormState())
    val formState = _formState.asStateFlow()

    private val _showOrderDatePicker = MutableStateFlow(false)
    val showOrderDatePicker = _showOrderDatePicker.asStateFlow()

    private val _showExpectedDatePicker = MutableStateFlow(false)
    val showExpectedDatePicker = _showExpectedDatePicker.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<Unit>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    fun onTitleChange(newTitle: String) {
        _formState.update { it.copy(title = newTitle, titleError = null, saveError = null) }
    }
    fun onVolumeChange(newVolume: String) {
        _formState.update { it.copy(volume = newVolume, volumeError = null, saveError = null) }
    }
    fun onPublisherChange(newPublisher: String) {
        _formState.update { it.copy(publisher = newPublisher, publisherError = null, saveError = null) }
    }
    fun onStoreChange(newStore: String) {
        _formState.update { it.copy(store = newStore, storeError = null, saveError = null) }
    }
    fun onPriceChange(newPrice: String) {
        _formState.update { it.copy(price = newPrice, priceError = null, saveError = null) }
    }

    fun showOrderDatePicker() {
        _showOrderDatePicker.value = true
    }
    fun dismissShowOrderDatePicker() {
        _showOrderDatePicker.value = false
    }

    fun showExpectedDatePicker() {
        _showExpectedDatePicker.value = true
    }
    fun dismissShowExpectedDatePicker() {
        _showExpectedDatePicker.value = false
    }

    fun onOrderDateChange(newDate: LocalDate?) {
        _formState.update {
            it.copy(
                orderDate = newDate,
                orderDateError = null,
                saveError = null
            )
        }
        _showOrderDatePicker.value = false
    }

    fun onExpectedPublicationDateChange(newDate: LocalDate?) {
        _formState.update {
            it.copy(
                expectedPublicationDate = newDate,
                expectedPublicationDateError = null,
                saveError = null
            )
        }
        _showExpectedDatePicker.value = false
    }


    fun saveManga() {
        if (!validateForm()) {
            return
        }
        _formState.update { it.copy(isSaving = true, saveError = null) }
        viewModelScope.launch {
            try {
                val currentState = _formState.value
                val newManga = Manga(
                    title = currentState.title.trim(),
                    volume = currentState.volume.toInt(),
                    publisher = currentState.publisher.trim(),
                    store = currentState.store.trim(),
                    price = currentState.price.toDoubleOrNull(),
                    orderDate = currentState.orderDate!!,
                    expectedPublicationDate = currentState.expectedPublicationDate!!,
                    actualPublicationDate = null,
                    shippingDate = null,
                    status = MangaStatus.ORDERED
                )
                mangaRepository.upsertManga(newManga)
                _formState.update { it.copy(isSaving = false) }
                _navigationEvent.emit(Unit)
            } catch (e: Exception) {
                _formState.update { it.copy(isSaving = false, saveError = "Failed to save manga: ${e.message}") }
            }
        }
    }

    private fun validateForm(): Boolean {
        val state = _formState.value
        val titleError = if (state.title.isBlank()) "Title cannot be empty" else null
        val volumeError = if (state.volume.toIntOrNull() == null) "Volume must be a number" else null
        val publisherError = if (state.publisher.isBlank()) "Publisher cannot be empty" else null
        val storeError = if (state.store.isBlank()) "Store cannot be empty" else null
        val orderDateError = if (state.orderDate == null) "Order date must be selected" else null
        val expectedDateError = if (state.expectedPublicationDate == null) "Expected date must be selected" else null
        val priceError = if (state.price.isNotEmpty() && state.price.toDoubleOrNull() == null) "Price must be a valid number" else null

        val isValid = !(titleError != null ||
                volumeError != null ||
                publisherError != null ||
                storeError != null ||
                orderDateError != null ||
                expectedDateError != null ||
                priceError != null)
        _formState.update {
            it.copy(
                titleError = titleError,
                volumeError = volumeError,
                publisherError = publisherError,
                storeError = storeError,
                orderDateError = orderDateError,
                expectedPublicationDateError = expectedDateError,
                priceError = priceError
            )
        }
        return isValid
    }
}

