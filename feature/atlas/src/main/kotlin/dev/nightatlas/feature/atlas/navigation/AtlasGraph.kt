package dev.nightatlas.feature.atlas.navigation

import androidx.compose.material.navigation.bottomSheet
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import dev.nightatlas.domain.catalog.Constellation
import dev.nightatlas.feature.atlas.catalog.CatalogScreen
import dev.nightatlas.feature.atlas.catalog.ObjectDetailScreen
import dev.nightatlas.feature.atlas.observation.RecordObservationSheet
import dev.nightatlas.feature.atlas.observation.RecordedScreen
import dev.nightatlas.feature.atlas.plan.navigation.ObservationPlan
import dev.nightatlas.feature.atlas.plan.navigation.PlanInput
import dev.nightatlas.feature.atlas.plan.navigation.planGraph
import dev.nightatlas.navigation.model.AtlasTabs
import dev.nightatlas.navigation.model.CatalogTab
import dev.nightatlas.navigation.model.FieldKitRoute
import dev.nightatlas.navigation.registry.handler.ModelRouteHandler
import dev.nightatlas.navigation.routing.GraphNavigator
import dev.nightatlas.navigation.service.AtlasServices
import dev.nightatlas.navigation.service.JournalInput
import dev.nightatlas.navigation.stack.StackAction

// The selection handler receives the domain object and puts its stable ID on the stack.
internal val catalogSelectionHandler = ModelRouteHandler<Constellation> { ObjectDetail(it.id) }

fun NavGraphBuilder.atlasGraph(
    navigation: GraphNavigator,
    previousEntry: () -> NavBackStackEntry?,
) {
    planGraph(navigation, previousEntry)
    composable<CatalogTab> {
        CatalogScreen(
            onSelect = { star -> navigation.openRegistered(catalogSelectionHandler.input(star)) },
            onOpenPlan = { navigation.openService(ObservationPlan.service(PlanInput.Start)) },
            onOpenTools = { navigation.openRoute(FieldKitRoute) },
        )
    }
    composable<ObjectDetail> {
        ObjectDetailScreen(
            onBack = { navigation.execute(StackAction.NavigateUp) },
            onLog = { id ->
                navigation.openRoute(RecordObservation(FieldSlip(id).encode()))
            },
        )
    }
    bottomSheet<RecordObservation> {
        RecordObservationSheet(
            onBack = { navigation.execute(StackAction.Back) },
            onRecorded = { id ->
                navigation.execute(
                    StackAction.popUpToAndNavigate<RecordObservation>(Recorded(id), inclusive = true)
                )
            },
        )
    }
    composable<Recorded> {
        RecordedScreen(
            onBack = { navigation.execute(StackAction.Back) },
            onViewJournal = {
                navigation.openService(AtlasServices.Journal(JournalInput.Saved))
            },
            onFinish = { navigation.execute(StackAction.ClearStackAndNavigate(AtlasTabs)) },
        )
    }
}
