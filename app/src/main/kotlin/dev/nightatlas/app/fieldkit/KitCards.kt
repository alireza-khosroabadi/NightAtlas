package dev.nightatlas.app.fieldkit

import dev.nightatlas.navigation.model.ActionDto
import dev.nightatlas.navigation.registry.NavigationRegistry
import kotlinx.serialization.json.Json

/** Wire examples used by the field kit to exercise JSON, aliases and legacy links. */
internal class KitCards(registry: NavigationRegistry) {
    val orion = Json.decodeFromString<ActionDto>(
        """{"title":"Tonight’s observation","destination":"star","destinationType":"service","parameters":{"id":"orion","note":null}}"""
    ).toDomainModel(registry)
    val cassiopeia = ActionDto("", "sky/object", "SERVICE", mapOf("id" to "cassiopeia")).toDomainModel(registry)
    val record = ActionDto("", "nightatlas://field/lyra/record", "DEEP_LINK").toDomainModel(registry)
    val legacyStar = ActionDto("", "star", "DEEP_LINK", mapOf("id" to "orion")).toDomainModel(registry)
    val unknown = ActionDto("", "MOON_PHASES", "SERVICE").toDomainModel(registry)
}
