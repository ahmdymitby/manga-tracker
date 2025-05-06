package uk.dominikdias.manga.data

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

interface MangaRepository {
    fun getAllManga(): Flow<List<Manga>>
    fun getMangaById(id: Long): Flow<Manga?>
    fun getMangaByStatus(status: MangaStatus): Flow<List<Manga>>
    fun getDelayedManga(): Flow<List<Manga>>
    fun getPotentiallyDelayedManga(today: LocalDate): Flow<List<Manga>>
    suspend fun upsertManga(manga: Manga): Long
    suspend fun deleteManga(manga: Manga): Int
    suspend fun deleteMangaById(mangaId: Long): Int
}
