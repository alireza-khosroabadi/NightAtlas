package dev.nightatlas.app.fieldkit.editor

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import dev.nightatlas.app.fieldkit.navigation.INPUT
import dev.nightatlas.navigation.model.NamedRoute
import dev.nightatlas.navigation.routing.GraphNavigator
import dev.nightatlas.navigation.stack.StackAction

internal fun NavGraphBuilder.routeInputDestination(navigation: GraphNavigator) {
    composable(INPUT.name) {
        RouteInputScreen(
            onSubmit = { input ->
                when (input) {
                    is SubmittedInput.Action -> navigation.openAction(input.action)
                    is SubmittedInput.Service -> navigation.openService(input.request)
                    SubmittedInput.UnavailableService -> navigation.onServiceUnhandled()
                    is SubmittedInput.Route -> navigation.openRoute(NamedRoute(input.name))
                }
            },
            onBack = { navigation.execute(StackAction.Back) },
        )
    }
}
