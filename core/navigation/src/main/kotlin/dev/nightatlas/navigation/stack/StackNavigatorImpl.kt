package dev.nightatlas.navigation.stack

import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.navOptions
import dev.nightatlas.navigation.logging.NavigationLogger
import dev.nightatlas.navigation.model.BackStackTarget
import dev.nightatlas.navigation.model.CatalogTab
import dev.nightatlas.navigation.model.NamedRoute
import dev.nightatlas.navigation.model.NavigationRoute
import dev.nightatlas.navigation.model.RouteTarget

class StackNavigatorImpl(private val controller: NavController) : StackNavigator {
    override fun execute(action: StackAction): Boolean =
        try {
            when (action) {
                StackAction.Back ->
                    controller.previousBackStackEntry != null && controller.popBackStack()
                StackAction.NavigateUp -> controller.navigateUp()
                StackAction.PopToRoot -> controller.popBackStack(CatalogTab, false)
                is StackAction.PopUpTo -> pop(action.target, action.inclusive)
                is StackAction.PopCount -> popCount(action.count)
                StackAction.ReplaceCurrent -> replaceCurrent()
                is StackAction.ClearStackAndNavigate ->
                    navigate(
                        action.route,
                        navOptions { popUpTo(controller.graph.id) { inclusive = true } },
                    )
                is StackAction.PopUpToAndNavigate ->
                    navigate(
                        action.route,
                        navOptions {
                            val target = action.popUpTo
                            when (target) {
                                is NamedRoute ->
                                    popUpTo(target.name) { inclusive = action.inclusive }
                                is BackStackTarget.ByType ->
                                    popUpTo(target.type) { inclusive = action.inclusive }
                                is NavigationRoute ->
                                    popUpTo(target) { inclusive = action.inclusive }
                            }
                        },
                    )
            }
        } catch (e: IllegalArgumentException) {
            NavigationLogger.e("StackNavigator", "Invalid back stack target for $action", e)
            false
        } catch (e: IllegalStateException) {
            NavigationLogger.e("StackNavigator", "Back stack unavailable for $action", e)
            false
        }

    private fun pop(target: BackStackTarget, inclusive: Boolean) =
        when (target) {
            is NamedRoute -> controller.popBackStack(target.name, inclusive)
            is BackStackTarget.ByType -> controller.popBackStack(target.type, inclusive)
            is NavigationRoute -> controller.popBackStack(target, inclusive)
        }

    private fun popCount(count: Int): Boolean {
        if (count < 1) return false
        var popped = 0
        while (
            popped < count && controller.previousBackStackEntry != null && controller.popBackStack()
        ) {
            popped++
        }
        return popped == count
    }

    private fun replaceCurrent(): Boolean {
        val entry = controller.currentBackStackEntry ?: return false
        val id = entry.destination.id
        controller.navigate(id, entry.arguments, navOptions { popUpTo(id) { inclusive = true } })
        val replacement = controller.currentBackStackEntry
        return replacement != null && replacement !== entry && replacement.destination.id == id
    }

    private fun navigate(target: RouteTarget, options: NavOptions): Boolean {
        when (target) {
            is NamedRoute -> controller.navigate(target.name, options)
            is NavigationRoute -> controller.navigate(target, options)
        }
        return true
    }
}
