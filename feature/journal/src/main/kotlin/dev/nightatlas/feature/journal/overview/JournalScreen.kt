package dev.nightatlas.feature.journal.overview

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.nightatlas.feature.journal.Section
import dev.nightatlas.ui.components.ActionRow
import dev.nightatlas.ui.components.Page

internal enum class JournalShortcut(val label: String) {
    Service("Via service"),
    DeepLink("Via deep link"),
    BottomBar("Via bottom bar action"),
}

@Composable
internal fun JournalScreen(
    onSectionSelected: (Section) -> Section?,
    onShortcutSelected: (JournalShortcut, Section) -> Section?,
    onOpenObject: (String) -> Unit,
    onOpenObservation: (String) -> Unit,
    viewModel: JournalViewModel = hiltViewModel(key = JournalViewModel.SAVED_STATE_KEY),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val shortcutSection = if (state.section == Section.Saved) Section.Guide else Section.Saved
    Page("Journal") {
        PrimaryTabRow(selectedTabIndex = state.section.ordinal) {
            Section.entries.forEach { tab ->
                Tab(
                    selected = tab == state.section,
                    onClick = { onSectionSelected(tab)?.let(viewModel::selectSection) },
                    text = { Text(tab.title) },
                )
            }
        }
        when (state.section) {
            Section.Guide -> {
                Text("Before you observe", style = MaterialTheme.typography.headlineSmall)
                Text(
                    "Find an open view of the sky. Dim your screen and give your eyes time to adjust to the dark."
                )
                ActionRow("Practice with Lyra", { onOpenObject("lyra") })
            }
            Section.Saved -> {
                if (!state.hasObservations) {
                    Text(
                        "Your journal is empty. Choose a constellation in the atlas and tap “Log observation” when you see it."
                    )
                }
                state.sightings.forEach { star ->
                    ActionRow(star.name, { onOpenObject(star.id) }, star.abbreviation)
                    ActionRow("Observation details for ${star.name}", { onOpenObservation(star.id) })
                    TextButton(onClick = { viewModel.remove(star.id) }) {
                        Text("Remove ${star.name} from the journal")
                    }
                }
            }
        }
        SectionShortcut(shortcutSection.title) { shortcut ->
            onShortcutSelected(shortcut, shortcutSection)?.let(viewModel::selectSection)
        }
    }
}

@Composable
private fun SectionShortcut(title: String, onSelect: (JournalShortcut) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        TextButton(onClick = { expanded = true }) { Text("Shortcut to $title") }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            JournalShortcut.entries.forEach { shortcut ->
                DropdownMenuItem(
                    text = { Text(shortcut.label) },
                    onClick = {
                        expanded = false
                        onSelect(shortcut)
                    },
                )
            }
        }
    }
}
