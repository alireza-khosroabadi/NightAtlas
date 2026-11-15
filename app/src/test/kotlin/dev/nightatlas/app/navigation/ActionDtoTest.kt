package dev.nightatlas.app.navigation

import dev.nightatlas.feature.atlas.navigation.AtlasEntry
import dev.nightatlas.navigation.model.ActionDto
import dev.nightatlas.navigation.model.ActionEntity
import dev.nightatlas.navigation.platform.PhoneDialDestination
import dev.nightatlas.navigation.registry.NavigationRegistry
import dev.nightatlas.navigation.service.AtlasServices
import dev.nightatlas.navigation.service.ObjectInput
import org.junit.Assert.*
import org.junit.Test

class ActionDtoTest {
    private val registry = NavigationRegistry().apply { AtlasEntry().registerNavigation(this) }

    @Test
    fun dtoDropsNullParametersAndRecognizesWireSpelling() {
        val card =
            ActionDto("Tonight", "star", " service ", mapOf("id" to "lyra", "note" to null))
                .toDomainModel(registry)
        assertEquals(ActionEntity.Service(AtlasServices.Object(ObjectInput.Detail("lyra", "Tonight"))), card)
        assertNull(PhoneDialDestination.normalize("https://example.com"))
    }

    @Test
    fun explicitTitleIncludingEmptyWinsAndNullFallsBackToCardTitle() {
        for (override in listOf(null, "", "custom")) {
            val card = ActionDto("Card", "star", "SERVICE", mapOf("title" to override, "path" to "lyra"))
            assertEquals(
                ActionEntity.Service(AtlasServices.Object(ObjectInput.Detail("lyra", override ?: "Card"))),
                card.toDomainModel(registry),
            )
        }
    }
}
