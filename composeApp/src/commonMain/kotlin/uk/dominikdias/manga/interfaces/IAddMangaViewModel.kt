package uk.dominikdias.manga.interfaces

import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.datetime.LocalDate
import uk.dominikdias.manga.model.AddMangaFormState

interface IAddMangaViewModel {
    val formState: StateFlow<AddMangaFormState>
    val showOrderDatePicker: StateFlow<Boolean>
    val showExpectedDatePicker: StateFlow<Boolean>
    val navigationEvent: SharedFlow<Unit>

    fun onTitleChange(newTitle: String)
    fun onVolumeChange(newVolume: String)
    fun onPublisherChange(newPublisher: String)
    fun onStoreChange(newStore: String)
    fun onPriceChange(newPrice: String)
    fun showOrderDatePicker()
    fun dismissShowOrderDatePicker()
    fun showExpectedDatePicker()
    fun dismissShowExpectedDatePicker()
    fun onOrderDateChange(newDate: LocalDate?)
    fun onExpectedPublicationDateChange(newDate: LocalDate?)
    fun saveManga()
}