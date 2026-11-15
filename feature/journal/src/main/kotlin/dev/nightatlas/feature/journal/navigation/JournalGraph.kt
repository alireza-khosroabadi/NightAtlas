package dev.nightatlas.feature.journal.navigation

import android.net.Uri
import androidx.core.net.toUri
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import dev.nightatlas.feature.journal.observation.JournalObservationScreen
import dev.nightatlas.feature.journal.overview.JournalScreen
import dev.nightatlas.feature.journal.overview.JournalShortcut
import dev.nightatlas.navigation.model.ActionEntity
import dev.nightatlas.navigation.model.JournalTab
import dev.nightatlas.navigation.routing.GraphNavigator
import dev.nightatlas.navigation.service.AtlasServices
import dev.nightatlas.navigation.service.JournalInput
import dev.nightatlas.navigation.service.ObjectInput
import dev.nightatlas.navigation.stack.StackAction

fun NavGraphBuilder.journalGraph(
    navigation: GraphNavigator,
    decodeLink: (Uri) -> ActionEntity.Link,
) {
    val navigator = JournalNavigator(navigation::openAction)
    composable<JournalTab> {
        JournalScreen(
            onSectionSelected = { navigator.handleAction(it.selectAction()) },
            onShortcutSelected = { shortcut, shortcutSection ->
                val action =
                    when (shortcut) {
                        JournalShortcut.Service ->
                            ActionEntity.Service(AtlasServices.Journal(shortcutSection.serviceInput))
                        JournalShortcut.DeepLink ->
                            decodeLink("nightatlas://journal/${shortcutSection.key}".toUri())
                        JournalShortcut.BottomBar -> ActionEntity.bottomBar("journal", shortcutSection.key)
                    }
                navigator.handleAction(action)
            },
            onOpenObject = { id -> navigation.openService(AtlasServices.Object(ObjectInput.Detail(id))) },
            onOpenObservation = { id ->
                navigator.openFromService(JournalInput.Observation(id))
            },
        )
    }
    composable<JournalObservation> {
        JournalObservationScreen(
            onBack = { navigation.execute(StackAction.Back) },
            onViewInAtlas = { id ->
                navigation.openService(AtlasServices.Object(ObjectInput.Detail(id)))
            },
        )
    }
}
