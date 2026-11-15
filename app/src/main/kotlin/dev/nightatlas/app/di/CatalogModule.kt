package dev.nightatlas.app.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.nightatlas.data.catalog.PocketCatalog
import dev.nightatlas.data.journal.LocalObservationBook
import dev.nightatlas.domain.catalog.SkyCatalog
import dev.nightatlas.domain.journal.ObservationBook
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object CatalogModule {
    @Provides fun catalog(): SkyCatalog = PocketCatalog

    @Provides
    @Singleton
    fun book(@ApplicationContext context: Context): ObservationBook = LocalObservationBook(context)
}
