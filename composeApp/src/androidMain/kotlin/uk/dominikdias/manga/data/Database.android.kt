package uk.dominikdias.manga.data

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import org.koin.java.KoinJavaComponent.get

actual fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
    val appContext: Context = get(Context::class.java)
    val dbFile = appContext.getDatabasePath("my_room.db")
    return Room.databaseBuilder<AppDatabase>(
        context = appContext,
        name = dbFile.absolutePath
    )
}