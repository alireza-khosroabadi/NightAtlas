package dev.nightatlas.app.navigation

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.net.toUri
import androidx.navigation.NavHostController
import dev.nightatlas.app.fieldkit.navigation.GuidePage
import dev.nightatlas.navigation.deeplink.AtlasLinkMapper
import dev.nightatlas.navigation.deeplink.DeeplinkNavigatorImpl
import dev.nightatlas.navigation.platform.PhoneDialNavigator
import dev.nightatlas.navigation.platform.SystemAction
import dev.nightatlas.navigation.platform.SystemActionKey
import dev.nightatlas.navigation.platform.SystemActionNavigator
import dev.nightatlas.navigation.platform.WebNavigator
import dev.nightatlas.navigation.registry.NavigationRegistry
import dev.nightatlas.navigation.registry.RegisteredRouteNavigator
import dev.nightatlas.navigation.routing.ActionRouterImpl
import dev.nightatlas.navigation.routing.NavigationContainer
import dev.nightatlas.navigation.stack.StackNavigatorImpl
import dev.nightatlas.navigation.tabs.BottomBarNavigatorImpl

internal fun createContainer(
    activity: Activity,
    controller: NavHostController,
    registry: NavigationRegistry,
): NavigationContainer {
    val container = NavigationContainer(controller)
    val platform = PlatformNavigation(activity, container)
    container.attachRouter(
        ActionRouterImpl(
            bottomBarNavigator = BottomBarNavigatorImpl(container),
            webNavigator = platform,
            systemActionNavigator = platform,
            phoneDialNavigator = platform,
            deeplinkNavigator =
                DeeplinkNavigatorImpl(container, AtlasLinkMapper(registry)) { uri ->
                    if (controller.graph.hasDeepLink(uri)) {
                        controller.navigate(uri)
                        true
                    } else false
                },
            registeredRouteNavigator = RegisteredRouteNavigator(controller, registry),
            stackNavigator = StackNavigatorImpl(controller),
        )
    )
    return container
}

private class PlatformNavigation(
    private val activity: Activity,
    private val container: NavigationContainer,
) : WebNavigator, SystemActionNavigator, PhoneDialNavigator {
    override fun openWebView(url: String): Boolean =
        isHttps(url) && container.tryNavigate(GuidePage(url))

    override fun openExternal(url: String): Boolean =
        isHttps(url) &&
            launch(Intent(Intent.ACTION_VIEW, url.toUri()).addCategory(Intent.CATEGORY_BROWSABLE))

    override fun open(destination: String): Boolean =
        launch(Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", destination, null)))

    override fun open(action: SystemAction): Boolean =
        when (action.key) {
            SystemActionKey.OPEN_APP_DETAILS_SETTINGS ->
                launch(
                    Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", activity.packageName, null),
                    )
                )
            SystemActionKey.OPEN_BIOMETRIC_SETTINGS -> {
                val enrolled =
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.R &&
                        launch(Intent(Settings.ACTION_BIOMETRIC_ENROLL))
                enrolled || launch(Intent(Settings.ACTION_SECURITY_SETTINGS))
            }
        }

    private fun launch(intent: Intent): Boolean =
        try {
            activity.startActivity(intent)
            true
        } catch (_: ActivityNotFoundException) {
            false
        } catch (_: SecurityException) {
            false
        }
}

internal fun isHttps(url: String): Boolean {
    val uri = url.toUri()
    return uri.scheme.equals("https", ignoreCase = true) && !uri.host.isNullOrBlank()
}
