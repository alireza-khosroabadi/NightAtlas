package dev.nightatlas.navigation.feature

import dev.nightatlas.navigation.registry.NavigationRegistry

interface FeatureEntry {
    fun registerNavigation(registry: NavigationRegistry)
}
