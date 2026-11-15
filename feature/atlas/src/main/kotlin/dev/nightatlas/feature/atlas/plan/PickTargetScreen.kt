package dev.nightatlas.feature.atlas.plan

import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import dev.nightatlas.ui.components.ActionRow
import dev.nightatlas.ui.components.Page

@Composable
internal fun PickTargetScreen(
    onBack: () -> Unit,
    onSelect: (String) -> Unit,
    viewModel: PickTargetViewModel = hiltViewModel(),
) {
    Page("Choose a constellation", onBack) {
        viewModel.constellations.forEach { star ->
            ActionRow(star.name, { onSelect(star.id) }, star.abbreviation)
        }
    }
}
