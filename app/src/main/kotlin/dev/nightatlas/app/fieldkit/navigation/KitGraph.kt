package dev.nightatlas.app.fieldkit.navigation

import android.net.Uri
import androidx.core.net.toUri
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import dev.nightatlas.app.fieldkit.FieldKitExample
import dev.nightatlas.app.fieldkit.FieldKitScreen
import dev.nightatlas.app.fieldkit.GUIDE_URL
import dev.nightatlas.app.fieldkit.GuideScreen
import dev.nightatlas.app.fieldkit.KitCards
import dev.nightatlas.app.fieldkit.LEGACY_KIT_LINK
import dev.nightatlas.app.fieldkit.PendingRequestsScreen
import dev.nightatlas.app.fieldkit.UnavailableScreen
import dev.nightatlas.app.fieldkit.WEB_URL
import dev.nightatlas.app.fieldkit.editor.routeInputDestination
import dev.nightatlas.app.fieldkit.trail.TrailChoice
import dev.nightatlas.app.fieldkit.trail.TrailEndScreen
import dev.nightatlas.app.fieldkit.trail.TrailMarkerScreen
import dev.nightatlas.app.fieldkit.trail.TrailStartScreen
import dev.nightatlas.navigation.model.ActionEntity
import dev.nightatlas.navigation.model.CatalogTab
import dev.nightatlas.navigation.model.FieldKitRoute
import dev.nightatlas.navigation.routing.GraphNavigator
import dev.nightatlas.navigation.service.AtlasServices
import dev.nightatlas.navigation.service.NavigationServices
import dev.nightatlas.navigation.service.ObjectInput
import dev.nightatlas.navigation.service.WebInput
import dev.nightatlas.navigation.service.invoke
import dev.nightatlas.navigation.stack.StackAction

internal fun NavGraphBuilder.kitGraph(
    navigation: GraphNavigator,
    cards: KitCards,
    sendObject: (ObjectInput.Detail) -> Unit,
    sendGuide: () -> Unit,
    sendLink: (Uri) -> Unit,
) {
    composable(INBOX.name) {
        PendingRequestsScreen(
            onBack = { navigation.execute(StackAction.Back) },
            onQueueAction = {
                sendObject(ObjectInput.Detail("orion"))
            },
            onQueueLink = { sendLink("nightatlas://star/lyra".toUri()) },
        )
    }
    routeInputDestination(navigation)
    composable(FieldKitRoute.name, deepLinks = listOf(navDeepLink { uriPattern = LEGACY_KIT_LINK })) {
        FieldKitScreen(
            onBack = { navigation.execute(StackAction.NavigateUp) },
            onExampleSelected = { example ->
                when (example) {
                    FieldKitExample.RouteEditor -> navigation.openRoute(INPUT)
                    FieldKitExample.Lyra ->
                        navigation.openService(AtlasServices.Object(ObjectInput.Detail("lyra")))
                    FieldKitExample.OrionCard ->
                        navigation.openAction(cards.orion)
                    FieldKitExample.CassiopeiaAlias ->
                        navigation.openAction(cards.cassiopeia)
                    FieldKitExample.GuideHub ->
                        navigation.openLink("nightatlas://hub/field/guide".toUri())
                    FieldKitExample.LogObservation ->
                        navigation.openAction(cards.record)
                    FieldKitExample.LegacyStar -> navigation.openAction(cards.legacyStar)
                    FieldKitExample.Tonight -> navigation.openService(KitServices.Tonight())
                    FieldKitExample.GuideTab -> navigation.openAction(journalGuide)
                    FieldKitExample.UnknownService ->
                        navigation.openAction(cards.unknown)
                    FieldKitExample.OfflineGuide ->
                        navigation.openService(KitServices.FieldManual())
                    FieldKitExample.NasaWebsite ->
                        navigation.openAction(ActionEntity.externalWeb(WEB_URL))
                    FieldKitExample.BrowserService ->
                        navigation.openService(NavigationServices.ExternalWeb(WebInput(WEB_URL)))
                    FieldKitExample.GuideService ->
                        navigation.openService(NavigationServices.WebView(WebInput(GUIDE_URL)))
                    FieldKitExample.Dialer ->
                        navigation.openAction(ActionEntity.dialPhone("+1 202-555-0143"))
                    FieldKitExample.AppSettings ->
                        navigation.openAction(ActionEntity.openAppDetailsSettings())
                    FieldKitExample.BiometricSettings ->
                        navigation.openAction(ActionEntity.openBiometricSettings())
                    FieldKitExample.PendingRequests -> navigation.openRoute(INBOX)
                    FieldKitExample.GuideIntent -> sendGuide()
                    FieldKitExample.StartTrail -> navigation.openRoute(TRAIL_START)
                }
            },
        )
    }
    composable(TRAIL_START.name) {
        TrailStartScreen(
            onBack = { navigation.execute(StackAction.Back) },
            onNext = { navigation.openRoute(TrailMarker()) },
        )
    }
    composable<TrailMarker> { entry ->
        TrailMarkerScreen(
            visit = entry.toRoute<TrailMarker>().visit,
            onBack = { navigation.execute(StackAction.NavigateUp) },
            onNext = { navigation.openRoute(TRAIL_END) },
            onVisitAgain = { navigation.openRoute(TrailMarker(2)) },
            onReturnToFirst = { navigation.execute(StackAction.popTo(TrailMarker(1))) },
            onReplaceFromFirst = {
                navigation.execute(
                    StackAction.popUpToAndNavigate(TrailMarker(1), TrailMarker(3), inclusive = true)
                )
            },
        )
    }
    composable(TRAIL_END.name) {
        TrailEndScreen(
            onBack = { navigation.execute(StackAction.Back) },
            onChoiceSelected = { choice ->
                when (choice) {
                    TrailChoice.ReturnToFirst -> navigation.execute(StackAction.popTo(TRAIL_START))
                    TrailChoice.LeaveFirst ->
                        navigation.execute(StackAction.popUpTo(TRAIL_START, inclusive = true))
                    TrailChoice.ReturnToMarker ->
                        navigation.execute(StackAction.popTo<TrailMarker>())
                    TrailChoice.LeaveMarker ->
                        navigation.execute(StackAction.popUpTo(TrailMarker(), inclusive = true))
                    TrailChoice.ReplaceFirst ->
                        navigation.execute(
                            StackAction.popUpToAndNavigate(TRAIL_START, FieldKitRoute, inclusive = true)
                        )
                    TrailChoice.BackTwo -> navigation.execute(StackAction.PopCount(2))
                    TrailChoice.RepeatStop -> navigation.execute(StackAction.ReplaceCurrent)
                    TrailChoice.EndWalk -> navigation.openService(KitServices.EndWalk())
                    TrailChoice.Restart ->
                        navigation.execute(StackAction.ClearStackAndNavigate(CatalogTab))
                }
            },
        )
    }
    composable<Unavailable> { entry ->
        val route = entry.toRoute<Unavailable>()
        UnavailableScreen(
            destination = route.destination,
            type = route.type,
            onBack = { navigation.execute(StackAction.Back) },
            onReturnToAtlas = { navigation.execute(StackAction.PopToRoot) },
        )
    }
    composable<GuidePage> { entry ->
        val url = entry.toRoute<GuidePage>().url
        GuideScreen(
            url = url,
            isOfflineGuide = url == GUIDE_URL,
            onBack = { navigation.execute(StackAction.Back) },
        )
    }
}

private val journalGuide = ActionEntity.bottomBar("journal", "guide")
