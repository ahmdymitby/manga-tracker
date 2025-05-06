package uk.dominikdias.manga.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

@Dao
interface MangaDao {
    @Upsert
    suspend fun upsertManga(manga: Manga): Long

    @Query("SELECT * FROM Manga WHERE id = :id")
    fun getMangaById(id: Long): Flow<Manga?>

    @Query("SELECT * FROM Manga ORDER BY title ASC, volume ASC")
    fun getAllManga(): Flow<List<Manga>>

    @Query("SELECT * FROM Manga WHERE status = :status ORDER BY expectedPublicationDate ASC")
    fun getMangaByStatus(status: MangaStatus): Flow<List<Manga>>

    @Query("SELECT * FROM Manga WHERE actualPublicationDate IS NOT NULL AND actualPublicationDate > expectedPublicationDate ORDER BY expectedPublicationDate ASC")
    fun getDelayedManga(): Flow<List<Manga>>

    @Query("SELECT * FROM Manga WHERE status NOT IN ('RECEIVED', 'CANCELLED', 'SHIPPED') AND actualPublicationDate IS NULL AND expectedPublicationDate < :today ORDER BY expectedPublicationDate ASC")
    fun getPotentiallyDelayedManga(today: LocalDate): Flow<List<Manga>>

    @Delete
    suspend fun deleteManga(manga: Manga): Int

    @Query("DELETE FROM Manga WHERE id = :mangaId")
    suspend fun deleteMangaById(mangaId: Long): Int
}
