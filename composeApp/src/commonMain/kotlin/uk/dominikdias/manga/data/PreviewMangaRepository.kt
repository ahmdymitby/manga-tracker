package uk.dominikdias.manga.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.datetime.LocalDate

class PreviewMangaRepository: MangaRepository {
    private val dummyManga = Manga(
        id = 1L,
        title = "Test",
        volume = 1,
        publisher = "Test",
        store = "Test",
        price = null,
        orderDate = LocalDate(2025, 5, 5),
        expectedPublicationDate = LocalDate(2025, 5, 6),
        actualPublicationDate = null,
        shippingDate = null,
        status = MangaStatus.ORDERED,
    )
    override fun getAllManga(): Flow<List<Manga>> {
        return MutableStateFlow(listOf(dummyManga))
    }

    override fun getMangaById(id: Long): Flow<Manga?> {
        return MutableStateFlow(dummyManga)
    }

    override fun getMangaByStatus(status: MangaStatus): Flow<List<Manga>> {
        return MutableStateFlow(listOf(dummyManga))
    }

    override fun getDelayedManga(): Flow<List<Manga>> {
        return MutableStateFlow(listOf(dummyManga))
    }

    override fun getPotentiallyDelayedManga(today: LocalDate): Flow<List<Manga>> {
        return MutableStateFlow(listOf(dummyManga))
    }

    override suspend fun upsertManga(manga: Manga): Long {
        return 1L
    }

    override suspend fun deleteManga(manga: Manga): Int {
        return 1
    }

    override suspend fun deleteMangaById(mangaId: Long): Int {
        return 1
    }
}