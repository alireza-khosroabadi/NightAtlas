package dev.nightatlas.feature.atlas.plan

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.nightatlas.domain.catalog.SkyCatalog
import dev.nightatlas.feature.atlas.plan.navigation.ReviewPlan
import javax.inject.Inject

@HiltViewModel
internal class ReviewPlanViewModel
@Inject
constructor(handle: SavedStateHandle, catalog: SkyCatalog) : ViewModel() {
    val target = catalog.find(handle.toRoute<ReviewPlan>().id)
}
