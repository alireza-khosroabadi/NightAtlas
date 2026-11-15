package dev.nightatlas.feature.atlas.plan

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.nightatlas.ui.components.ActionRow
import dev.nightatlas.ui.components.Page

@Composable
internal fun PlanStartScreen(
    consumePickedTarget: () -> String?,
    onBack: () -> Unit,
    onChoose: () -> Unit,
    onReview: (String) -> Unit,
    viewModel: PlanStartViewModel = hiltViewModel(key = PlanStartViewModel.SAVED_STATE_KEY),
) {
    val target by viewModel.target.collectAsStateWithLifecycle()
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        consumePickedTarget()?.let(viewModel::selectTarget)
    }
    Page("Observing plan", onBack) {
        Text(target?.let { "Selected target: ${it.name}" } ?: "Choose a constellation to observe.")
        ActionRow("Choose a target", onChoose)
        target?.let { selected ->
            ActionRow("Review plan", { onReview(selected.id) })
        }
    }
}
