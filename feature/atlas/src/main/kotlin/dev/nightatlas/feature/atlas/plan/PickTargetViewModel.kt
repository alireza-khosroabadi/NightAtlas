package dev.nightatlas.feature.atlas.plan

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.nightatlas.domain.catalog.SkyCatalog
import javax.inject.Inject

@HiltViewModel
internal class PickTargetViewModel @Inject constructor(catalog: SkyCatalog) : ViewModel() {
    val constellations = catalog.constellations
}
