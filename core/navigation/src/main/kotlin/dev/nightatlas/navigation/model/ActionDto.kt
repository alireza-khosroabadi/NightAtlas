package dev.nightatlas.navigation.model

import androidx.core.net.toUri
import dev.nightatlas.navigation.deeplink.AtlasLinkMapper
import dev.nightatlas.navigation.deeplink.isADeeplink
import dev.nightatlas.navigation.platform.SystemActionKey
import dev.nightatlas.navigation.registry.NavigationRegistry
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ActionDto(
    @SerialName("title") val title: String? = null,
    @SerialName("destination") val destination: String? = null,
    @SerialName("destinationType") val destinationType: String? = null,
    @SerialName("parameters") val parameters: Map<String, String?>? = null,
) {
    fun toDomainModel(registry: NavigationRegistry): ActionEntity {
        val key = destination.orEmpty()
        val type = DestinationType.getDestinationType(destinationType)
        return when (type) {
            DestinationType.SERVICE -> decodeService(key, type, registry)
            DestinationType.DEEP_LINK ->
                if (key.isADeeplink()) AtlasLinkMapper(registry).decode(key.toUri()) else decodeService(key, type, registry)
            DestinationType.BOTTOM_BAR -> ActionEntity.BottomBar(key, parameters?.get("tabScreen").orEmpty())
            DestinationType.TAB_BAR -> ActionEntity.Tab(key)
            DestinationType.WEB_VIEW -> ActionEntity.WebView(key)
            DestinationType.EXTERNAL_WEB -> ActionEntity.ExternalWeb(key)
            DestinationType.TEL_DEEP_LINK -> ActionEntity.Dial(key)
            DestinationType.SYSTEM_ACTION ->
                SystemActionKey.parse(key)?.let(ActionEntity::System) ?: ActionEntity.Unhandled
        }
    }

    private fun decodeService(key: String, type: DestinationType, registry: NavigationRegistry): ActionEntity {
        val fields = buildMap {
            if (!title.isNullOrBlank()) put("title", title)
            parameters?.forEach { (name, value) -> if (value != null) put(name, value) }
        }
        return registry.decodeServiceAction(key, type, fields)
    }
}

internal fun NavigationRegistry.decodeServiceAction(
    key: String,
    type: DestinationType,
    fields: Map<String, String>,
): ActionEntity {
    if (key.isBlank()) return ActionEntity.Unhandled
    val id = resolveServiceId(key, fields["path"]) ?: return ActionEntity.UnsupportedService(key, type)
    return decodeService(id, fields)?.let(ActionEntity::Service) ?: ActionEntity.Unhandled
}
