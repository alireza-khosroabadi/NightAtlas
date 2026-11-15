package dev.nightatlas.navigation.model

import kotlin.reflect.KClass

/** A stack operation can match a specific destination or any entry of a route type. */
sealed interface BackStackTarget {
    data class ByType(val type: KClass<out NavigationRoute>) : BackStackTarget
}

sealed interface RouteTarget : BackStackTarget, RegistryTarget

data class NamedRoute(val name: String) : RouteTarget

/** Implemented by the serializable destination types declared by each graph. */
interface NavigationRoute : RouteTarget
