package dev.nightatlas.navigation.model

import android.net.Uri
import dev.nightatlas.navigation.registry.handler.ModelRouteHandler
import dev.nightatlas.navigation.stack.StackAction

/** The targets that a graph can open or a service resolver can return. */
sealed interface NavigationTarget {
    data class Link(val uri: Uri) : NavigationTarget

    data class Stack(val action: StackAction) : NavigationTarget

    class Model<T> internal constructor(
        internal val handler: ModelRouteHandler<T>,
        private val value: T,
    ) : RegistryTarget {
        internal fun destination(): NavigationRoute = handler.destination(value)
    }
}

/** Destinations the route registry can open on a NavController. */
sealed interface RegistryTarget : NavigationTarget
