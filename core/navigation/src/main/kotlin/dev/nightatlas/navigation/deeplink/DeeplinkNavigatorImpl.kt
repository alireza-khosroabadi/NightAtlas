package dev.nightatlas.navigation.deeplink

import android.net.Uri
import androidx.core.net.toUri
import dev.nightatlas.navigation.logging.NavigationLogger
import dev.nightatlas.navigation.model.ActionEntity
import dev.nightatlas.navigation.model.NavigationResult
import dev.nightatlas.navigation.model.asNavigationResult
import dev.nightatlas.navigation.routing.NavigationContainer

class DeeplinkNavigatorImpl(
    private val container: NavigationContainer,
    private val deepLinkActionMapper: DeepLinkActionMapper,
    private val openGraphLink: (Uri) -> Boolean = { false },
) : DeeplinkNavigator {

    override fun openDeeplink(uri: Uri) {
        if (!tryOpenDeeplink(uri)) {
            NavigationLogger.e(TAG, "Deeplink was not handled: $uri")
        }
    }

    override fun tryOpenDeeplink(uri: Uri): Boolean = openLink(uri).isHandled

    override fun openLink(uri: Uri): NavigationResult = openLink(deepLinkActionMapper.decode(uri))

    override fun openLink(link: ActionEntity.Link): NavigationResult =
        try {
            val mapped = link.mapped?.let(container::openAction)
            if (mapped != null && mapped.isHandled) mapped
            else openGraphLink(link.uri).asNavigationResult()
        } catch (e: Exception) {
            NavigationLogger.e(TAG, "Failed to open deeplink: ${link.uri}", e)
            NavigationResult.Unhandled
        }

    override fun openDeeplink(uri: String) {
        openDeeplink(uri.toUri())
    }

    override fun tryOpenDeeplink(uri: String): Boolean = tryOpenDeeplink(uri.toUri())

    private companion object {
        const val TAG = "DeeplinkNavigator"
    }
}
