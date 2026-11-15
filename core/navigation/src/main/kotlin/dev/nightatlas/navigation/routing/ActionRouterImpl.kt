package dev.nightatlas.navigation.routing

import android.net.Uri
import dev.nightatlas.navigation.deeplink.DeeplinkNavigator
import dev.nightatlas.navigation.deeplink.isAtlasDeepLink
import dev.nightatlas.navigation.model.ActionEntity
import dev.nightatlas.navigation.model.BottomBarRoute
import dev.nightatlas.navigation.model.JournalTab
import dev.nightatlas.navigation.model.NavigationResult
import dev.nightatlas.navigation.model.NavigationTarget
import dev.nightatlas.navigation.model.RegistryTarget
import dev.nightatlas.navigation.model.asNavigationResult
import dev.nightatlas.navigation.platform.PhoneDialDestination
import dev.nightatlas.navigation.platform.PhoneDialNavigator
import dev.nightatlas.navigation.platform.SystemAction
import dev.nightatlas.navigation.platform.SystemActionNavigator
import dev.nightatlas.navigation.platform.WebNavigator
import dev.nightatlas.navigation.registry.RegisteredRouteNavigator
import dev.nightatlas.navigation.service.NavigationServices
import dev.nightatlas.navigation.service.ServiceRequest
import dev.nightatlas.navigation.service.UnsupportedInput
import dev.nightatlas.navigation.stack.StackNavigator
import dev.nightatlas.navigation.tabs.BottomBarNavigator

class ActionRouterImpl(
    private val bottomBarNavigator: BottomBarNavigator,
    private val webNavigator: WebNavigator,
    private val systemActionNavigator: SystemActionNavigator,
    private val phoneDialNavigator: PhoneDialNavigator,
    private val deeplinkNavigator: DeeplinkNavigator,
    private val registeredRouteNavigator: RegisteredRouteNavigator,
    private val stackNavigator: StackNavigator,
) : ActionEntityRouter {

    override fun open(target: NavigationTarget): NavigationResult =
        when (target) {
            is ActionEntity -> openAction(target)
            is NavigationTarget.Link -> openLink(target.uri)
            is NavigationTarget.Stack -> stackNavigator.execute(target.action).asNavigationResult()
            is BottomBarRoute -> bottomBarNavigator.switch(target).asNavigationResult()
            is RegistryTarget -> registeredRouteNavigator.navigate(target).asNavigationResult()
        }

    private fun openAction(action: ActionEntity): NavigationResult =
        when (action) {
            is ActionEntity.Service -> openService(action.request)
            is ActionEntity.UnsupportedService -> {
                val input = UnsupportedInput(action.destination, action.type.name)
                val fallback = openService(NavigationServices.Update(input))
                if (fallback.isHandled) NavigationResult.FallbackOpened else NavigationResult.Unhandled
            }
            is ActionEntity.BottomBar -> {
                val route = BottomBarRoute.from(action.destination)
                val tab = if (route is JournalTab && action.tabScreen.isNotBlank()) {
                    route.copy(tabScreen = action.tabScreen)
                } else {
                    route
                }
                bottomBarNavigator.switch(tab).asNavigationResult()
            }
            is ActionEntity.WebView -> webNavigator.openWebView(action.url).asNavigationResult()
            is ActionEntity.ExternalWeb -> webNavigator.openExternal(action.url).asNavigationResult()
            is ActionEntity.Link ->
                if (action.uri.isAtlasDeepLink()) deeplinkNavigator.openLink(action) else NavigationResult.Unhandled
            is ActionEntity.System -> systemActionNavigator.open(SystemAction(action.key)).asNavigationResult()
            is ActionEntity.Dial -> PhoneDialDestination.normalize(action.number)?.let {
                phoneDialNavigator.open(it).asNavigationResult()
            } ?: NavigationResult.Unhandled
            is ActionEntity.Tab, ActionEntity.Unhandled -> NavigationResult.Unhandled
        }

    private fun openLink(uri: Uri): NavigationResult =
        if (uri.isAtlasDeepLink()) deeplinkNavigator.openLink(uri) else NavigationResult.Unhandled

    override fun openService(request: ServiceRequest<*>): NavigationResult =
        registeredRouteNavigator.resolveServiceRoute(request)?.let(::open) ?: NavigationResult.Unhandled
}
