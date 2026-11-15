package dev.nightatlas.app.fieldkit.navigation

import androidx.core.net.toUri
import dev.nightatlas.app.fieldkit.GUIDE_URL
import dev.nightatlas.navigation.feature.FeatureEntry
import dev.nightatlas.navigation.model.ActionEntity
import dev.nightatlas.navigation.model.FieldKitRoute
import dev.nightatlas.navigation.model.NavigationTarget
import dev.nightatlas.navigation.registry.NavigationRegistry
import dev.nightatlas.navigation.registry.handler.TypedRouteHandler
import dev.nightatlas.navigation.service.AtlasServices
import dev.nightatlas.navigation.service.NavigationServices
import dev.nightatlas.navigation.service.UnsupportedInput
import dev.nightatlas.navigation.stack.StackAction
import javax.inject.Inject

internal class KitEntry @Inject constructor() : FeatureEntry {
    override fun registerNavigation(registry: NavigationRegistry) {
        registry.registerString(FieldKitRoute)
        registry.registerString(TRAIL_START)
        // TRAIL_END uses the registry's direct NavController fallback for string routes.
        registry.register(TypedRouteHandler(TrailMarker::class))
        registry.register(TypedRouteHandler(GuidePage::class))
        registry.register(TypedRouteHandler(Unavailable::class))
        registry.registerService(NavigationServices.Update, decode = { fields ->
            UnsupportedInput(fields["unsupportedDestination"].orEmpty(), fields["unsupportedDestinationType"].orEmpty())
        }) { Unavailable(it.destination, it.type) }
        for (platform in listOf(NavigationServices.WebView, NavigationServices.ExternalWeb)) {
            registry.registerExternallyHandledService(platform.id, "PlatformNavigation")
            registry.registerServiceAlias(platform.id.value, platform.id)
        }
        registry.registerPathAwareServiceAlias("field") { path ->
            if (path == "guide" || path == "saved") AtlasServices.Journal.id else AtlasServices.Object.id
        }
        registry.registerService(KitServices.FieldManual) { ActionEntity.webView(GUIDE_URL) }
        registry.registerService(KitServices.EndWalk) { NavigationTarget.Stack(StackAction.PopToRoot) }
        registry.registerService(KitServices.Tonight) { NavigationTarget.Link("nightatlas://star/orion".toUri()) }
    }
}
