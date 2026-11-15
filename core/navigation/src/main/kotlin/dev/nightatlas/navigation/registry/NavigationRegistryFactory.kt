package dev.nightatlas.navigation.registry

import dev.nightatlas.navigation.feature.FeatureEntry
import dev.nightatlas.navigation.logging.NavigationLogger
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NavigationRegistryFactory
@Inject
constructor(private val featureEntries: Set<@JvmSuppressWildcards FeatureEntry>) {

    // Entries register once and all containers share the assembled registry.
    private val registry: NavigationRegistry by
        lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            NavigationRegistry().apply {
                featureEntries.forEach { entry -> entry.registerNavigation(this) }
                reportValidationProblems()
            }
        }

    private fun NavigationRegistry.reportValidationProblems() {
        validate().forEach { problem -> NavigationLogger.e(TAG, problem) }
    }

    fun create(): NavigationRegistry = registry

    private companion object {
        const val TAG = "NavigationRegistryFactory"
    }
}
