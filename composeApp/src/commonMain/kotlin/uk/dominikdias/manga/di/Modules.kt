package uk.dominikdias.manga.di

import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module
import uk.dominikdias.manga.data.AppDatabase
import uk.dominikdias.manga.data.MangaDao
import uk.dominikdias.manga.data.MangaRepository
import uk.dominikdias.manga.data.MangaRepositoryImpl
import uk.dominikdias.manga.data.createRoomDatabase
import uk.dominikdias.manga.data.getDatabaseBuilder
import uk.dominikdias.manga.viewmodel.AddMangaViewModel
import uk.dominikdias.manga.viewmodel.EditMangaViewModel
import uk.dominikdias.manga.viewmodel.HomeViewModel
import uk.dominikdias.manga.viewmodel.MainContentViewModel
import uk.dominikdias.manga.viewmodel.OrderedViewModel
import uk.dominikdias.manga.viewmodel.ReceivedViewModel
import uk.dominikdias.manga.viewmodel.TopBarViewModel

expect fun platformModule(): Module

fun appModule() = module {
    single<AppDatabase> {
        val builder = getDatabaseBuilder()
        createRoomDatabase(builder)
    }

    single<MangaDao> {
        val database: AppDatabase = get()
        database.getDao()
    }

    single<MangaRepository> {
        MangaRepositoryImpl(get())
    }
    viewModelOf(::HomeViewModel)
    viewModelOf(::MainContentViewModel)
    viewModelOf(::AddMangaViewModel)
    viewModelOf(::OrderedViewModel)
    viewModelOf(::ReceivedViewModel)
    viewModelOf(::TopBarViewModel)

    viewModel { (mangaId: Long) ->
        EditMangaViewModel(
            mangaRepository = get(),
            mangaId = mangaId
        )
    }
}

fun initKoin(appDeclaration: KoinAppDeclaration = {}) {
    startKoin {
        appDeclaration()
        modules(
            listOf(appModule(), platformModule())
        )
    }
}