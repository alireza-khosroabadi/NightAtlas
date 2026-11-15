package dev.nightatlas.feature.atlas.catalog

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.nightatlas.domain.catalog.SkyCatalog
import dev.nightatlas.feature.atlas.navigation.ObjectDetail
import javax.inject.Inject

@HiltViewModel
internal class ObjectDetailViewModel
@Inject
constructor(handle: SavedStateHandle, catalog: SkyCatalog) : ViewModel() {
    private val route = handle.toRoute<ObjectDetail>()
    val star = catalog.find(route.id)
    val title = route.title
}
