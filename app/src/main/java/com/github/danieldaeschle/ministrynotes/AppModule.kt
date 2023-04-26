package com.github.danieldaeschle.ministrynotes

import android.app.Application
import androidx.room.Room
import com.github.danieldaeschle.ministrynotes.data.AppDatabase
import com.github.danieldaeschle.ministrynotes.data.BibleStudyEntryRepository
import com.github.danieldaeschle.ministrynotes.data.EntryRepository
import com.github.danieldaeschle.ministrynotes.data.SettingsDataStore
import com.github.danieldaeschle.ministrynotes.ui.home.viewmodels.EntryDetailsViewModel
import com.github.danieldaeschle.ministrynotes.ui.home.viewmodels.HomeViewModel
import com.github.danieldaeschle.ministrynotes.ui.home.viewmodels.StudiesDetailsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "db").build()
    }
    single { get<AppDatabase>().bibleStudyEntryDao() }
    single { get<AppDatabase>().entryDao() }
    single { EntryRepository(get()) }
    single { BibleStudyEntryRepository(get()) }
    single { SettingsDataStore(androidContext()) }
    viewModel { params ->
        HomeViewModel(params.get(), androidContext() as Application, get(), get(), get())
    }
    viewModel { params -> StudiesDetailsViewModel(params.get(), get()) }
    viewModel { params -> EntryDetailsViewModel(params.get(), params.getOrNull(), get()) }
}