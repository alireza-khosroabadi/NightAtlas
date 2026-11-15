package dev.nightatlas.navigation.registry.handler

import dev.nightatlas.navigation.model.NavigationRoute
import dev.nightatlas.navigation.model.NavigationTarget

/** Binds a model to its mapper before the request enters the registry. */
class ModelRouteHandler<T>(internal val destination: (T) -> NavigationRoute) {
    fun input(model: T): NavigationTarget.Model<T> = NavigationTarget.Model(this, model)
}
