package dev.nightatlas.feature.journal.observation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.nightatlas.ui.components.ActionRow
import dev.nightatlas.ui.components.Page

@Composable
internal fun JournalObservationScreen(
    onBack: () -> Unit,
    onViewInAtlas: (String) -> Unit,
    viewModel: JournalObservationViewModel = hiltViewModel(),
) {
    val star = viewModel.star
    val isObserved by viewModel.isObserved.collectAsStateWithLifecycle()
    Page("Observation details", onBack) {
        Text(star?.name ?: "Unknown object")
        Text(
            if (isObserved) "This observation is in your journal."
            else "You haven’t logged this constellation yet."
        )
        if (star != null) ActionRow("View in atlas", { onViewInAtlas(viewModel.id) })
    }
}
