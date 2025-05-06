package uk.dominikdias.manga.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.LocalDate

@Entity
data class Manga(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val volume: Int,
    val publisher: String,
    val store: String? = null,
    val price: Double? = null,
    val orderDate: LocalDate,
    val expectedPublicationDate: LocalDate,
    val actualPublicationDate: LocalDate?,
    val shippingDate: LocalDate?,
    val status: MangaStatus = MangaStatus.ORDERED,
) {
    val wasPublicationDelayed: Boolean
        get() = actualPublicationDate != null && actualPublicationDate > expectedPublicationDate

    val isCompleted: Boolean
        get() = status == MangaStatus.RECEIVED
}
