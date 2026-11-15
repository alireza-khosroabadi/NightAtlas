package dev.nightatlas.navigation.routing

import android.net.Uri
import androidx.navigation.NavHostController
import dev.nightatlas.navigation.logging.NavigationLogger
import dev.nightatlas.navigation.model.ActionEntity
import dev.nightatlas.navigation.model.NavigationResult
import dev.nightatlas.navigation.model.NavigationTarget
import dev.nightatlas.navigation.service.ServiceRequest
import dev.nightatlas.navigation.stack.StackAction

class NavigationContainer(internal val navController: NavHostController) {
    private var _router: ActionEntityRouter? = null
    private val router: ActionEntityRouter
        get() =
            checkNotNull(_router) {
                "ActionEntityRouter is not attached to NavigationContainer. Call attachRouter() before routing."
            }

    fun attachRouter(router: ActionEntityRouter) {
        if (_router != null && _router !== router) {
            NavigationLogger.w(TAG, "Replacing attached ActionEntityRouter")
        }
        _router = router
    }

    fun open(target: NavigationTarget): NavigationResult = router.open(target)

    fun open(action: StackAction): NavigationResult = open(NavigationTarget.Stack(action))

    fun openAction(action: ActionEntity): NavigationResult = open(action)

    fun openLink(uri: Uri): NavigationResult = open(NavigationTarget.Link(uri))

    fun openService(request: ServiceRequest<*>): NavigationResult =
        router.openService(request)

    fun tryNavigate(target: NavigationTarget): Boolean = open(target).isHandled

    fun tryNavigate(action: StackAction): Boolean = open(action).isHandled

    fun tryNavigate(request: ServiceRequest<*>): Boolean =
        openService(request).isHandled

    fun navigate(target: NavigationTarget) {
        open(target)
    }

    fun navigateUp() {
        open(StackAction.NavigateUp)
    }

    private companion object {
        const val TAG = "NavigationContainer"
    }
}
