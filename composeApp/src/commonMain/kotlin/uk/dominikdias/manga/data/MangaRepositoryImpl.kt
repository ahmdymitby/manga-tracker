package uk.dominikdias.manga.data

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

class MangaRepositoryImpl(private val mangaDao: MangaDao) : MangaRepository {
    override fun getAllManga(): Flow<List<Manga>> {
        return mangaDao.getAllManga()
    }

    override fun getMangaById(id: Long): Flow<Manga?> {
        return mangaDao.getMangaById(id)
    }

    override fun getMangaByStatus(status: MangaStatus): Flow<List<Manga>> {
        return mangaDao.getMangaByStatus(status)
    }

    override fun getDelayedManga(): Flow<List<Manga>> {
        return mangaDao.getDelayedManga()
    }

    override fun getPotentiallyDelayedManga(today: LocalDate): Flow<List<Manga>> {
        return mangaDao.getPotentiallyDelayedManga(today)
    }

    override suspend fun upsertManga(manga: Manga): Long {
        return mangaDao.upsertManga(manga)
    }

    override suspend fun deleteManga(manga: Manga): Int {
        return mangaDao.deleteManga(manga)
    }

    override suspend fun deleteMangaById(mangaId: Long): Int {
        return mangaDao.deleteMangaById(mangaId)
    }
}
