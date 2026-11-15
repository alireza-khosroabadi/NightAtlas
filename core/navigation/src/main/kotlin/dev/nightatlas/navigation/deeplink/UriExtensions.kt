package dev.nightatlas.navigation.deeplink

import android.net.Uri
import androidx.core.net.toUri
import dev.nightatlas.navigation.logging.NavigationLogger

fun Uri.extractArguments(): Map<String, String> {
    return buildMap {
        queryParameterNames.forEach { key -> put(key, getQueryParameter(key).orEmpty()) }
        if (pathSegments.isNotEmpty()) {
            put("path", pathSegments.joinToString("/"))
        }
    }
}

fun Uri.isAtlasDeepLink(): Boolean =
    scheme.equals(DEEPLINK_PREFIX.removeSuffix("://"), ignoreCase = true)

fun Uri.isHubDeepLink(): Boolean = host.equals(HUB_DEEP_LINK_HOST, ignoreCase = true)

fun String.isADeeplink(): Boolean =
    try {
        val uri = this.toUri()
        uri.isAtlasDeepLink()
    } catch (e: Exception) {
        NavigationLogger.e("UriExtensions", "Failed to parse deeplink: $this", e)
        false
    }
