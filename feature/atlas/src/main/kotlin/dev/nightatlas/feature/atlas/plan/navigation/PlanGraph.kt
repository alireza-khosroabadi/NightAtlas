package dev.nightatlas.feature.atlas.plan.navigation

import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import dev.nightatlas.feature.atlas.navigation.FieldSlip
import dev.nightatlas.feature.atlas.navigation.RecordObservation
import dev.nightatlas.feature.atlas.plan.PickTargetScreen
import dev.nightatlas.feature.atlas.plan.PlanStartScreen
import dev.nightatlas.feature.atlas.plan.ReviewPlanScreen
import dev.nightatlas.navigation.routing.GraphNavigator
import dev.nightatlas.navigation.stack.StackAction

// Only a committed selection is delivered to the caller's entry.
private const val SELECTED_TARGET = "plan.selectedTarget"

internal fun NavGraphBuilder.planGraph(
    navigation: GraphNavigator,
    previousEntry: () -> NavBackStackEntry?,
) {
    composable<PlanStart> { entry ->
        PlanStartScreen(
            consumePickedTarget = { entry.savedStateHandle.remove<String>(SELECTED_TARGET) },
            onBack = { navigation.execute(StackAction.NavigateUp) },
            onChoose = { navigation.openRoute(PickTarget(returnToCaller = true)) },
            onReview = { id -> navigation.openRoute(ReviewPlan(id)) },
        )
    }
    composable<PickTarget> { entry ->
        val returnToCaller = entry.toRoute<PickTarget>().returnToCaller
        PickTargetScreen(
            onBack = { navigation.execute(StackAction.Back) },
            onSelect = { id ->
                val caller = previousEntry()
                if (returnToCaller && caller != null && caller.destination.hasRoute<PlanStart>()) {
                    caller.savedStateHandle[SELECTED_TARGET] = id
                    navigation.execute(StackAction.Back)
                } else {
                    navigation.openRoute(ReviewPlan(id))
                }
            },
        )
    }
    composable<ReviewPlan> {
        ReviewPlanScreen(
            onBack = { navigation.execute(StackAction.Back) },
            onLog = { id ->
                navigation.openRoute(RecordObservation(FieldSlip(id).encode()))
            },
        )
    }
}
