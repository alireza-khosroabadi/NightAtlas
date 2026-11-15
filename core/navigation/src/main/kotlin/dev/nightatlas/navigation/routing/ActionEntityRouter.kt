package dev.nightatlas.navigation.routing

import dev.nightatlas.navigation.model.NavigationResult
import dev.nightatlas.navigation.model.NavigationTarget
import dev.nightatlas.navigation.service.ServiceRequest

interface ActionEntityRouter {
    fun open(target: NavigationTarget): NavigationResult

    fun openService(request: ServiceRequest<*>): NavigationResult
}
