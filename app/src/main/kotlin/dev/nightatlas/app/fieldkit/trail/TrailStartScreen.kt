package dev.nightatlas.app.fieldkit.trail

import androidx.compose.runtime.Composable
import dev.nightatlas.ui.components.ActionRow
import dev.nightatlas.ui.components.Page

@Composable
internal fun TrailStartScreen(onBack: () -> Unit, onNext: () -> Unit) {
    Page("Stop 1", onBack) { ActionRow("Go to stop 2", onNext) }
}
