package uk.dominikdias.manga.application

import android.app.Application
import org.koin.android.ext.koin.androidContext
import uk.dominikdias.manga.di.initKoin

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        initKoin {
            androidContext(this@MyApplication)
        }
    }
}