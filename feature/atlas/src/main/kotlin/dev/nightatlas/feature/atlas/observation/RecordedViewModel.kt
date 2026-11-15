package dev.nightatlas.feature.atlas.observation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.nightatlas.domain.catalog.SkyCatalog
import dev.nightatlas.feature.atlas.navigation.Recorded
import javax.inject.Inject

@HiltViewModel
internal class RecordedViewModel
@Inject
constructor(handle: SavedStateHandle, catalog: SkyCatalog) : ViewModel() {
    val starName = catalog.find(handle.toRoute<Recorded>().id)?.name.orEmpty()
}
