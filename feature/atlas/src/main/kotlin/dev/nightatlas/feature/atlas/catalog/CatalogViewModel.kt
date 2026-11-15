package dev.nightatlas.feature.atlas.catalog

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.nightatlas.domain.catalog.SkyCatalog
import javax.inject.Inject

@HiltViewModel
internal class CatalogViewModel @Inject constructor(catalog: SkyCatalog) : ViewModel() {
    val constellations = catalog.constellations
}
