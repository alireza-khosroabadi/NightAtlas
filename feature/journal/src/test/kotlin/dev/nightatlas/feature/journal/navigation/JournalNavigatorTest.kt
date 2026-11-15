package dev.nightatlas.feature.journal.navigation

import dev.nightatlas.feature.journal.Section
import dev.nightatlas.navigation.model.ActionDto
import dev.nightatlas.navigation.model.ActionEntity
import dev.nightatlas.navigation.model.DestinationType
import dev.nightatlas.navigation.registry.NavigationRegistry
import dev.nightatlas.navigation.service.AtlasServices
import dev.nightatlas.navigation.service.JournalInput
import org.junit.Assert.*
import org.junit.Test

class JournalNavigatorTest {
    private val opened = mutableListOf<ActionEntity>()
    private val nav = JournalNavigator { opened.add(it) }

    private val registry = NavigationRegistry().apply { JournalEntry().registerNavigation(this) }

    @Test
    fun tabServiceAliasAndBottomBarCanSelectTheSameLocalSection() {
        val actions = listOf(
            Section.Guide.selectAction(),
            ActionDto("", "BOOK", "SERVICE", mapOf("path" to "Field_Guide")).toDomainModel(registry),
            ActionEntity.bottomBar("journal", "guide"),
            ActionEntity.UnsupportedService("field-guide", DestinationType.DEEP_LINK),
        )
        actions.forEach { assertEquals(Section.Guide, nav.handleAction(it)) }
        assertTrue(opened.isEmpty())
    }

    @Test
    fun unknownTabIsIgnoredButCrossFeatureActionsAndNonTabServicesAreDispatched() {
        val crossFeature = ActionEntity.webView("https://example.org")
        assertNull(nav.handleAction(ActionEntity.Tab("missing")))
        assertNull(nav.handleAction(crossFeature))
        val observation = JournalInput.Observation("orion")
        assertNull(nav.openFromService(observation))
        assertEquals(listOf(crossFeature, ActionEntity.Service(AtlasServices.Journal(observation))), opened)
    }
}
