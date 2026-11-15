package dev.nightatlas.feature.atlas.catalog

import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import dev.nightatlas.domain.catalog.Constellation
import dev.nightatlas.ui.components.ActionRow
import dev.nightatlas.ui.components.Page

@Composable
internal fun CatalogScreen(
    onSelect: (Constellation) -> Unit,
    onOpenPlan: () -> Unit,
    onOpenTools: () -> Unit,
    viewModel: CatalogViewModel = hiltViewModel(),
) {
    val constellations = viewModel.constellations
    Page("Sky atlas") {
        constellations.firstOrNull()?.let { StarChart(it.id, it.name) }
        constellations.forEach { star ->
            ActionRow(star.name, { onSelect(star) }, star.abbreviation)
        }
        ActionRow("Observing plan", onOpenPlan)
        ActionRow("Field kit", onOpenTools)
    }
}
