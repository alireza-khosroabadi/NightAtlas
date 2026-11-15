package dev.nightatlas.navigation.routing

import android.net.Uri
import dev.nightatlas.navigation.model.ActionEntity
import dev.nightatlas.navigation.model.NavigationResult
import dev.nightatlas.navigation.model.NavigationTarget
import dev.nightatlas.navigation.model.RouteTarget
import dev.nightatlas.navigation.service.ServiceRequest
import dev.nightatlas.navigation.stack.StackAction

/** The graph chooses the destination. The host handles unsuccessful requests. */
class GraphNavigator(
    private val container: NavigationContainer,
    private val onUnhandled: () -> Unit,
    val onServiceUnhandled: () -> Unit,
) {
    fun openRoute(route: RouteTarget) = report(container.open(route))

    fun openRegistered(input: NavigationTarget.Model<*>) = report(container.open(input))

    fun openAction(action: ActionEntity) = report(container.openAction(action))

    fun openLink(link: Uri) = report(container.openLink(link))

    fun execute(action: StackAction) = report(container.open(action))

    fun openService(request: ServiceRequest<*>) {
        if (container.openService(request) == NavigationResult.Unhandled) onServiceUnhandled()
    }

    private fun report(result: NavigationResult) {
        if (result == NavigationResult.Unhandled) onUnhandled()
    }
}
