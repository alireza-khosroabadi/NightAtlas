package dev.nightatlas.navigation.registry.handler

import androidx.navigation.NavController
import dev.nightatlas.navigation.model.NavigationRoute
import kotlin.reflect.KClass

class TypedRouteHandler(override val routeClass: KClass<out NavigationRoute>) : RouteHandler {
    override fun canHandle(route: NavigationRoute): Boolean = routeClass.isInstance(route)

    override fun navigate(navController: NavController, route: NavigationRoute) {
        require(canHandle(route)) {
            "Route ${route::class.qualifiedName} is not handled by ${routeClass.qualifiedName}"
        }
        navController.navigate(route)
    }
}
