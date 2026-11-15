package dev.nightatlas.feature.atlas.catalog

import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import dev.nightatlas.ui.components.Page

@Composable
internal fun ObjectDetailScreen(
    onBack: () -> Unit,
    onLog: (String) -> Unit,
    viewModel: ObjectDetailViewModel = hiltViewModel(),
) {
    val star = viewModel.star
    Page(star?.name ?: "Unknown object", onBack) {
        if (star == null) {
            Text("No constellation in the atlas matches this ID.")
        } else {
            viewModel.title?.let { Text(it, color = MaterialTheme.colorScheme.secondary) }
            StarChart(star.id, star.name)
            Text(star.description, style = MaterialTheme.typography.bodyLarge)
            Text(star.fieldNote, color = MaterialTheme.colorScheme.secondary)
            Button(onClick = { onLog(star.id) }) { Text("Log observation") }
        }
    }
}
