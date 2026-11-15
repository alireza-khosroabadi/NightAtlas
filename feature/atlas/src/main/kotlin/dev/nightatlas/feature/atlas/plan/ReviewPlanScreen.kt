package dev.nightatlas.feature.atlas.plan

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import dev.nightatlas.ui.components.ActionRow
import dev.nightatlas.ui.components.Page

@Composable
internal fun ReviewPlanScreen(
    onBack: () -> Unit,
    onLog: (String) -> Unit,
    viewModel: ReviewPlanViewModel = hiltViewModel(),
) {
    val target = viewModel.target
    Page("Plan review", onBack) {
        if (target == null) {
            Text("This target could not be found in the atlas.")
        } else {
            Text(target.name)
            Text(target.fieldNote)
            ActionRow("Log this observation", { onLog(target.id) })
        }
    }
}
