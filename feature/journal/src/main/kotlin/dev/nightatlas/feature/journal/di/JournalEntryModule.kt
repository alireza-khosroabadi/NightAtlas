package dev.nightatlas.feature.journal.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import dev.nightatlas.feature.journal.navigation.JournalEntry
import dev.nightatlas.navigation.feature.FeatureEntry

@Module
@InstallIn(SingletonComponent::class)
internal interface JournalEntryModule {
    @Binds @IntoSet fun bindEntry(entry: JournalEntry): FeatureEntry
}
