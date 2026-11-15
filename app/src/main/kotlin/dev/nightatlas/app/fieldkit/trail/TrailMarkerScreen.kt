package dev.nightatlas.app.fieldkit.trail

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import dev.nightatlas.ui.components.ActionRow
import dev.nightatlas.ui.components.Page

@Composable
internal fun TrailMarkerScreen(
    visit: Int,
    onBack: () -> Unit,
    onNext: () -> Unit,
    onVisitAgain: () -> Unit,
    onReturnToFirst: () -> Unit,
    onReplaceFromFirst: () -> Unit,
) {
    Page("Stop 2", onBack) {
        Text("Visit $visit")
        ActionRow("Go to stop 3", onNext)
        if (visit == 1) ActionRow("Visit this stop again", onVisitAgain)
        if (visit == 2) {
            ActionRow("Return to the first visit", onReturnToFirst)
            ActionRow("Replace the stack from the first visit", onReplaceFromFirst)
        }
    }
}
