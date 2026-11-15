package dev.nightatlas.navigation.registry

import androidx.navigation.NavController
import dev.nightatlas.navigation.model.NavigationTarget
import dev.nightatlas.navigation.model.RegistryTarget
import dev.nightatlas.navigation.service.ServiceRequest

class RegisteredRouteNavigator(
    private val navController: NavController,
    private val registry: NavigationRegistry,
) {
    fun resolveServiceRoute(request: ServiceRequest<*>): NavigationTarget? =
        registry.resolveServiceRoute(request)

    fun navigate(route: RegistryTarget): Boolean = registry.navigate(navController, route)
}
