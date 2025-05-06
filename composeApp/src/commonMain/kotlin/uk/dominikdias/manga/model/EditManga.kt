package uk.dominikdias.manga.model

import kotlinx.datetime.LocalDate
import uk.dominikdias.manga.data.MangaStatus

data class EditMangaFormState(
    val mangaId: Long = 0,
    val title: String = "",
    val volume: String = "",
    val publisher: String = "",
    val store: String = "",
    val price: String = "",
    val orderDate: LocalDate? = null,
    val expectedPublicationDate: LocalDate? = null,
    val actualPublicationDate: LocalDate? = null,
    val shippingDate: LocalDate? = null,
    val status: MangaStatus = MangaStatus.ORDERED,
    val titleError: String? = null,
    val volumeError: String? = null,
    val publisherError: String? = null,
    val storeError: String? = null,
    val orderDateError: String? = null,
    val expectedPublicationDateError: String? = null,
    val priceError: String? = null,
    val isLoading: Boolean = true,
    val initialLoadError: String? = null,
    val isSaving: Boolean = false,
    val saveError: String? = null,
    val isDeleting: Boolean = false,
    val deleteError: String? = null
)
