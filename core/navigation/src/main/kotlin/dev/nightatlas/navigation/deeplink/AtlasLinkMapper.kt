package dev.nightatlas.navigation.deeplink

import android.net.Uri
import dev.nightatlas.navigation.model.ActionEntity
import dev.nightatlas.navigation.model.DestinationType
import dev.nightatlas.navigation.model.decodeServiceAction
import dev.nightatlas.navigation.registry.NavigationRegistry

class AtlasLinkMapper(private val registry: NavigationRegistry) : DeepLinkActionMapper {
    override fun map(uri: Uri): ActionEntity? {
        if (!uri.isAtlasDeepLink() || !uri.isHierarchical) return null
        val keyInPath = uri.host.isNullOrBlank() || uri.isHubDeepLink()
        val key = if (keyInPath) uri.pathSegments.firstOrNull() else uri.host
        if (key.isNullOrBlank())
            return if (uri.isHubDeepLink()) null else ActionEntity.bottomBar("catalog")
        if (key.equals("legacy", ignoreCase = true)) return null
        val segments = if (keyInPath) uri.pathSegments.drop(1) else uri.pathSegments
        val path = uri.getQueryParameter("path") ?: segments.joinToString("/")
        val params = uri.extractArguments() + ("path" to path)
        return if (key.equals("catalog", ignoreCase = true)) ActionEntity.bottomBar("catalog")
        else registry.decodeServiceAction(key, DestinationType.SERVICE, params)
    }
}
