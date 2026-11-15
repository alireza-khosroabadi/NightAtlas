package dev.nightatlas.feature.journal.navigation

import dev.nightatlas.feature.journal.Section
import dev.nightatlas.navigation.feature.FeatureEntry
import dev.nightatlas.navigation.model.JournalTab
import dev.nightatlas.navigation.registry.NavigationRegistry
import dev.nightatlas.navigation.registry.handler.TypedRouteHandler
import dev.nightatlas.navigation.service.AtlasServices
import dev.nightatlas.navigation.service.JournalInput
import javax.inject.Inject

class JournalEntry @Inject constructor() : FeatureEntry {
    override fun registerNavigation(registry: NavigationRegistry) {
        registry.register(TypedRouteHandler(JournalObservation::class))
        registry.registerService(AtlasServices.Journal, setOf("journal", "book"), ::decode) { input ->
            when (input) {
                JournalInput.Default -> JournalTab()
                JournalInput.Guide -> JournalTab(Section.Guide.key)
                JournalInput.Saved -> JournalTab(Section.Saved.key)
                is JournalInput.Observation -> JournalObservation(input.id)
            }
        }
    }

    private fun decode(fields: Map<String, String>): JournalInput {
        val path = fields["path"].orEmpty().trim('/')
        if (path.startsWith("observation/")) {
            path.substringAfter('/').takeIf { it.isNotBlank() }?.let { return JournalInput.Observation(it) }
        }
        return Section.match(path)?.serviceInput ?: JournalInput.Default
    }
}
