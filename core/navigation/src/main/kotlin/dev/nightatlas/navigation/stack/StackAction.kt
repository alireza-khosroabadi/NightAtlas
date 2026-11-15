package dev.nightatlas.navigation.stack

import dev.nightatlas.navigation.model.BackStackTarget
import dev.nightatlas.navigation.model.NamedRoute
import dev.nightatlas.navigation.model.NavigationRoute
import dev.nightatlas.navigation.model.RouteTarget

sealed class StackAction {
    companion object {
        fun popTo(target: BackStackTarget): StackAction = PopUpTo(target, inclusive = false)

        fun popTo(route: String): StackAction = popTo(NamedRoute(route))

        inline fun <reified T : NavigationRoute> popTo(): StackAction =
            popTo(BackStackTarget.ByType(T::class))

        fun popUpTo(target: BackStackTarget, inclusive: Boolean): StackAction =
            PopUpTo(target, inclusive)

        fun popUpTo(route: String, inclusive: Boolean): StackAction =
            popUpTo(NamedRoute(route), inclusive)

        inline fun <reified T : NavigationRoute> popUpTo(inclusive: Boolean): StackAction =
            popUpTo(BackStackTarget.ByType(T::class), inclusive)

        fun popUpToAndNavigate(
            popUpTo: BackStackTarget,
            route: RouteTarget,
            inclusive: Boolean,
        ): StackAction = PopUpToAndNavigate(popUpTo, route, inclusive)

        fun popUpToAndNavigate(popUpTo: String, route: String, inclusive: Boolean): StackAction =
            popUpToAndNavigate(NamedRoute(popUpTo), NamedRoute(route), inclusive)

        inline fun <reified T : NavigationRoute> popUpToAndNavigate(
            route: RouteTarget,
            inclusive: Boolean,
        ): StackAction = popUpToAndNavigate(BackStackTarget.ByType(T::class), route, inclusive)
    }

    object Back : StackAction()

    object NavigateUp : StackAction()

    object PopToRoot : StackAction()

    data class PopUpTo(val target: BackStackTarget, val inclusive: Boolean) : StackAction()

    data class PopUpToAndNavigate(
        val popUpTo: BackStackTarget,
        val route: RouteTarget,
        val inclusive: Boolean,
    ) : StackAction()

    object ReplaceCurrent : StackAction()

    data class ClearStackAndNavigate(val route: RouteTarget) : StackAction()

    data class PopCount(val count: Int) : StackAction()
}
