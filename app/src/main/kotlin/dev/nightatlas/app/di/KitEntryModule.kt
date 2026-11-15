package dev.nightatlas.app.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import dev.nightatlas.app.fieldkit.navigation.KitEntry
import dev.nightatlas.navigation.feature.FeatureEntry

@Module
@InstallIn(SingletonComponent::class)
internal interface KitEntryModule {
    @Binds @IntoSet fun bindEntry(entry: KitEntry): FeatureEntry
}
