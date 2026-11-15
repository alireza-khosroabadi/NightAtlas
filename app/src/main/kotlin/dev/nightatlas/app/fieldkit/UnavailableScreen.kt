package dev.nightatlas.app.fieldkit

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import dev.nightatlas.ui.components.ActionRow
import dev.nightatlas.ui.components.Page

@Composable
internal fun UnavailableScreen(
    destination: String,
    type: String,
    onBack: () -> Unit,
    onReturnToAtlas: () -> Unit,
) {
    Page("Service unavailable", onBack) {
        Text("This version of Night Atlas does not recognize that destination.")
        Text("$destination · $type", color = MaterialTheme.colorScheme.secondary)
        ActionRow("Return to the atlas", onReturnToAtlas)
    }
}
