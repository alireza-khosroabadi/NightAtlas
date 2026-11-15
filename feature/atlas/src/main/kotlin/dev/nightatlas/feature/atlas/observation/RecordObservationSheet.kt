package dev.nightatlas.feature.atlas.observation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
internal fun RecordObservationSheet(
    onBack: () -> Unit,
    onRecorded: (String) -> Unit,
    viewModel: RecordObservationViewModel = hiltViewModel(),
) {
    val star = viewModel.star
    Surface(shape = MaterialTheme.shapes.large, color = MaterialTheme.colorScheme.surfaceContainer) {
        Column(
            Modifier.fillMaxWidth().padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(star?.name ?: "Unknown object", style = MaterialTheme.typography.headlineSmall)
            Text(
                if (star != null) "Add this constellation to your journal?"
                else "This object is missing from the atlas."
            )
            if (star != null) {
                Button(onClick = { viewModel.record()?.let(onRecorded) }) {
                    Text("Add to journal")
                }
            }
            TextButton(onClick = onBack) { Text("Back") }
        }
    }
}
