package uk.dominikdias.manga.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import uk.dominikdias.manga.data.Manga
import uk.dominikdias.manga.data.MangaRepository
import uk.dominikdias.manga.data.MangaStatus
import uk.dominikdias.manga.model.EditMangaFormState

class EditMangaViewModel(
    private val mangaRepository: MangaRepository,
    private val mangaId: Long
) : ViewModel() {

    private val _formState = MutableStateFlow(EditMangaFormState(mangaId = mangaId))
    val formState = _formState.asStateFlow()

    private val _showOrderDatePicker = MutableStateFlow(false)
    val showOrderDatePicker = _showOrderDatePicker.asStateFlow()
    private val _showExpectedDatePicker = MutableStateFlow(false)
    val showExpectedDatePicker = _showExpectedDatePicker.asStateFlow()
    private val _showActualDatePicker = MutableStateFlow(false)
    val showActualDatePicker = _showActualDatePicker.asStateFlow()
    private val _showShippingDatePicker = MutableStateFlow(false)
    val showShippingDatePicker = _showShippingDatePicker.asStateFlow()

    private val _statusMenuExpanded = MutableStateFlow(false)
    val statusMenuExpanded = _statusMenuExpanded.asStateFlow()

    private val _showDeleteConfirmDialog = MutableStateFlow(false)
    val showDeleteConfirmDialog = _showDeleteConfirmDialog.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<Unit>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    init {
        loadMangaData()
    }

    private fun loadMangaData() {
        _formState.update { it.copy(isLoading = true, initialLoadError = null) }
        viewModelScope.launch {
            mangaRepository.getMangaById(mangaId)
                .filterNotNull()
                .firstOrNull()
                ?.let { manga ->
                    _formState.update {
                        it.copy(
                            isLoading = false,
                            title = manga.title,
                            volume = manga.volume.toString(),
                            publisher = manga.publisher,
                            store = manga.store ?: "",
                            price = manga.price?.toString() ?: "",
                            orderDate = manga.orderDate,
                            expectedPublicationDate = manga.expectedPublicationDate,
                            actualPublicationDate = manga.actualPublicationDate,
                            shippingDate = manga.shippingDate,
                            status = manga.status
                        )
                    }
                } ?: run {
                _formState.update { it.copy(isLoading = false, initialLoadError = "Manga with ID $mangaId not found.") }
            }
        }
    }

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

    fun onStatusChange(newStatus: MangaStatus) {
        _formState.update { it.copy(status = newStatus, saveError = null) }
        _statusMenuExpanded.value = false
    }

    fun setStatusMenuExpanded(isExpanded: Boolean) {
        _statusMenuExpanded.value = isExpanded
    }
    fun setShowDeleteConfirmDialog(show: Boolean) {
        if (!show) {
            _formState.update { it.copy(deleteError = null) }
        }
        _showDeleteConfirmDialog.value = show
    }

    fun showOrderDatePicker(show: Boolean) { _showOrderDatePicker.value = show }
    fun showExpectedDatePicker(show: Boolean) { _showExpectedDatePicker.value = show }
    fun showActualDatePicker(show: Boolean) { _showActualDatePicker.value = show }
    fun showShippingDatePicker(show: Boolean) { _showShippingDatePicker.value = show }

    fun onOrderDateChange(newDate: LocalDate?) {
        _formState.update { it.copy(orderDate = newDate, orderDateError = null, saveError = null) }
        _showOrderDatePicker.value = false
    }
    fun onExpectedPublicationDateChange(newDate: LocalDate?) {
        _formState.update { it.copy(expectedPublicationDate = newDate, expectedPublicationDateError = null, saveError = null) }
        _showExpectedDatePicker.value = false
    }
    fun onActualPublicationDateChange(newDate: LocalDate?) {
        _formState.update { it.copy(actualPublicationDate = newDate, saveError = null) }
        _showActualDatePicker.value = false
    }
    fun onShippingDateChange(newDate: LocalDate?) {
        _formState.update { it.copy(shippingDate = newDate, saveError = null) }
        _showShippingDatePicker.value = false
    }

    fun saveChanges() {
        if (!validateForm()) return
        _formState.update { it.copy(isSaving = true, saveError = null) }
        viewModelScope.launch {
            try {
                val currentState = _formState.value
                val updatedManga = Manga(
                    id = currentState.mangaId,
                    title = currentState.title.trim(),
                    volume = currentState.volume.toInt(),
                    publisher = currentState.publisher.trim(),
                    store = currentState.store.trim(),
                    price = currentState.price.toDoubleOrNull(),
                    orderDate = currentState.orderDate!!,
                    expectedPublicationDate = currentState.expectedPublicationDate!!,
                    actualPublicationDate = currentState.actualPublicationDate,
                    shippingDate = currentState.shippingDate,
                    status = currentState.status
                )
                mangaRepository.upsertManga(updatedManga)
                _formState.update { it.copy(isSaving = false) }
                _navigationEvent.emit(Unit)
            } catch (e: Exception) {
                _formState.update { it.copy(isSaving = false, saveError = "Failed to save changes: ${e.message}") }
            }
        }
    }

    fun confirmDeleteManga() {
        _formState.update { it.copy(isDeleting = true, deleteError = null) }
        viewModelScope.launch {
            try {
                val deletedRows = mangaRepository.deleteMangaById(mangaId)
                if (deletedRows > 0) {
                    _formState.update { it.copy(isDeleting = false) }
                    _navigationEvent.emit(Unit)
                } else {
                    _formState.update { it.copy(isDeleting = false, deleteError = "Manga not found for deletion (ID: $mangaId).") }
                }
            } catch (e: Exception) {
                _formState.update { it.copy(isDeleting = false, deleteError = "Failed to delete manga: ${e.message}") }
            } finally {
                _showDeleteConfirmDialog.value = false
            }
        }
    }

    private fun validateForm(): Boolean {
        val state = _formState.value
        val titleError = "Title cannot be empty".takeIf { state.title.isBlank() }
        val volumeError = "Volume must be a number".takeIf { state.volume.toIntOrNull() == null }
        val publisherError = "Publisher cannot be empty".takeIf { state.publisher.isBlank() }
        val storeError = "Store cannot be empty".takeIf { state.store.isBlank() }
        val orderDateError = "Order date must be selected".takeIf { state.orderDate == null }
        val expectedDateError = "Expected date must be selected".takeIf { state.expectedPublicationDate == null }
        val priceError = "Price must be a valid number".takeIf { state.price.isNotEmpty() && state.price.toDoubleOrNull() == null }

        val isValid = !(titleError != null || volumeError != null || publisherError != null || storeError != null || orderDateError != null || expectedDateError != null || priceError != null)
        _formState.update {
            it.copy(
                titleError = titleError, volumeError = volumeError, publisherError = publisherError,
                storeError = storeError, orderDateError = orderDateError,
                expectedPublicationDateError = expectedDateError, priceError = priceError
            )
        }
        return isValid
    }
}
