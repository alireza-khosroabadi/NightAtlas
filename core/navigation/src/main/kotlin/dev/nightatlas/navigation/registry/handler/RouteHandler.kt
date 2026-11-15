package dev.nightatlas.navigation.registry.handler

import androidx.navigation.NavController
import dev.nightatlas.navigation.model.NavigationRoute
import kotlin.reflect.KClass

interface RouteHandler {
    /** An exact registration takes priority over handlers selected by [canHandle]. */
    val routeClass: KClass<out NavigationRoute>?
        get() = null

    fun canHandle(route: NavigationRoute): Boolean

    fun navigate(navController: NavController, route: NavigationRoute)
}
