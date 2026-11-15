package dev.nightatlas.app.fieldkit

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import dev.nightatlas.ui.components.ActionRow
import dev.nightatlas.ui.components.Page

@Composable
internal fun PendingRequestsScreen(
    onBack: () -> Unit,
    onQueueAction: () -> Unit,
    onQueueLink: () -> Unit,
) {
    Page("Pending requests", onBack) {
        Text(
            "Queue a few requests, then leave this screen to open them in order. Use Back to retrace the route."
        )
        ActionRow("Queue the Orion action", onQueueAction)
        ActionRow("Queue the Lyra link", onQueueLink)
        ActionRow("Open queued requests", onBack)
    }
}
