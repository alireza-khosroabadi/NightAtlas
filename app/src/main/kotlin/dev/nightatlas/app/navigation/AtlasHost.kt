package dev.nightatlas.app.navigation

import android.app.Activity
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material.navigation.ModalBottomSheetLayout
import androidx.compose.material.navigation.rememberBottomSheetNavigator
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.*
import androidx.navigation.navigation
import dev.nightatlas.app.fieldkit.KitCards
import dev.nightatlas.app.fieldkit.navigation.INBOX
import dev.nightatlas.app.fieldkit.navigation.kitGraph
import dev.nightatlas.feature.atlas.navigation.atlasGraph
import dev.nightatlas.feature.journal.navigation.journalGraph
import dev.nightatlas.navigation.deeplink.AtlasLinkMapper
import dev.nightatlas.navigation.model.AtlasTabs
import dev.nightatlas.navigation.model.CatalogTab
import dev.nightatlas.navigation.model.JournalTab
import dev.nightatlas.navigation.pending.PendingNavigationViewModel
import dev.nightatlas.navigation.registry.NavigationRegistry
import dev.nightatlas.navigation.routing.GraphNavigator
import dev.nightatlas.navigation.service.ObjectInput
import dev.nightatlas.ui.theme.NightTheme
import kotlinx.coroutines.launch

@Composable
internal fun AtlasHost(
    activity: Activity,
    registry: NavigationRegistry,
    pending: PendingNavigationViewModel,
    sendObject: (ObjectInput.Detail) -> Unit,
    sendGuide: () -> Unit,
    sendLink: (Uri) -> Unit,
) {
    val sheet = rememberBottomSheetNavigator()
    val controller = rememberNavController(sheet)
    val container =
        remember(activity, controller, registry) { createContainer(activity, controller, registry) }
    val links = remember(registry) { AtlasLinkMapper(registry) }
    val cards = remember(registry) { KitCards(registry) }
    val entry by controller.currentBackStackEntryAsState()
    val snack = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val navigation =
        remember(container, scope, snack) {
            GraphNavigator(
                container,
                onUnhandled = {
                    scope.launch { snack.showSnackbar("This destination is unavailable.") }
                },
                onServiceUnhandled = {
                    scope.launch { snack.showSnackbar("This service is unavailable.") }
                },
            )
        }
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    // The first entry signals that the NavHost has installed the graph.
    val canDeliver = entry != null && entry?.destination?.route != INBOX.name
    LaunchedEffect(canDeliver, lifecycle, pending) {
        if (canDeliver)
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                pending.pendingNavigation.collect(navigation::openAction)
            }
    }
    NightTheme {
        val onCatalog = entry?.destination?.hasRoute<CatalogTab>() == true
        val journal = entry?.destination?.hasRoute<JournalTab>() == true
        Scaffold(
            snackbarHost = { SnackbarHost(snack) },
            bottomBar = {
                if (onCatalog || journal)
                    AtlasBottomBar(
                        catalogSelected = onCatalog,
                        onCatalogSelected = { if (!onCatalog) navigation.openRoute(CatalogTab) },
                        onJournalSelected = { if (!journal) navigation.openRoute(JournalTab()) },
                    )
            },
        ) { inset ->
            ModalBottomSheetLayout(
                bottomSheetNavigator = sheet,
                modifier = Modifier.padding(inset),
                sheetBackgroundColor = MaterialTheme.colorScheme.surfaceContainer,
            ) {
                NavHost(controller, startDestination = AtlasTabs) {
                    navigation<AtlasTabs>(startDestination = CatalogTab) {
                        atlasGraph(
                            navigation,
                            previousEntry = { controller.previousBackStackEntry },
                        )
                        journalGraph(navigation, links::decode)
                        kitGraph(navigation, cards, sendObject, sendGuide, sendLink)
                    }
                }
            }
        }
    }
}

@Composable
private fun AtlasBottomBar(
    catalogSelected: Boolean,
    onCatalogSelected: () -> Unit,
    onJournalSelected: () -> Unit,
) {
    NavigationBar {
        NavigationBarItem(
            selected = catalogSelected,
            onClick = onCatalogSelected,
            icon = { Text("✧") },
            label = { Text("Atlas") },
        )
        NavigationBarItem(
            selected = !catalogSelected,
            onClick = onJournalSelected,
            icon = { Text("☾") },
            label = { Text("Journal") },
        )
    }
}
