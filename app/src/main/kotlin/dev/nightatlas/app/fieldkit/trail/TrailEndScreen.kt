package dev.nightatlas.app.fieldkit.trail

import androidx.compose.runtime.Composable
import dev.nightatlas.ui.components.ActionRow
import dev.nightatlas.ui.components.Page

internal enum class TrailChoice(val label: String) {
    ReturnToFirst("Pop to stop 1 by route name"),
    LeaveFirst("Pop through stop 1"),
    ReturnToMarker("Pop to stop 2 by route type"),
    LeaveMarker("Pop through stop 2"),
    ReplaceFirst("Replace stop 1 with the field kit"),
    BackTwo("Pop two screens"),
    RepeatStop("Replace this screen"),
    EndWalk("Return to the atlas via service"),
    Restart("Clear the stack and start again"),
}

@Composable
internal fun TrailEndScreen(onBack: () -> Unit, onChoiceSelected: (TrailChoice) -> Unit) {
    Page("Stop 3", onBack) {
        TrailChoice.entries.forEach { choice ->
            ActionRow(choice.label, { onChoiceSelected(choice) })
        }
    }
}
