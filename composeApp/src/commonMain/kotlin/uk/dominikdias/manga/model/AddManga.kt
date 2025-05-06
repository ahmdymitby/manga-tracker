package uk.dominikdias.manga.model

import kotlinx.datetime.LocalDate

data class AddMangaFormState(
    val title: String = "",
    val volume: String = "",
    val publisher: String = "",
    val store: String = "",
    val price: String = "",
    val orderDate: LocalDate? = null,
    val expectedPublicationDate: LocalDate? = null,
    val titleError: String? = null,
    val volumeError: String? = null,
    val publisherError: String? = null,
    val storeError: String? = null,
    val orderDateError: String? = null,
    val expectedPublicationDateError: String? = null,
    val priceError: String? = null,
    val isSaving: Boolean = false,
    val saveError: String? = null
)