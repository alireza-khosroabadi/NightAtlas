package dev.nightatlas.feature.atlas.observation

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import dev.nightatlas.ui.components.ActionRow
import dev.nightatlas.ui.components.Page

@Composable
internal fun RecordedScreen(
    onBack: () -> Unit,
    onViewJournal: () -> Unit,
    onFinish: () -> Unit,
    viewModel: RecordedViewModel = hiltViewModel(),
) {
    Page("Observation logged", onBack) {
        Text(viewModel.starName, style = MaterialTheme.typography.headlineMedium)
        ActionRow("View journal", onViewJournal)
        ActionRow("Finish and return to the atlas", onFinish)
    }
}
