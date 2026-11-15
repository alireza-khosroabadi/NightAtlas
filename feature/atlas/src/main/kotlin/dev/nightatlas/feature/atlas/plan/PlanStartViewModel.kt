package dev.nightatlas.feature.atlas.plan

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.nightatlas.domain.catalog.SkyCatalog
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
internal class PlanStartViewModel
@Inject
constructor(handle: SavedStateHandle, catalog: SkyCatalog) : ViewModel() {
    private val selectedId = handle.getMutableStateFlow("plan.targetId", "")
    val target =
        selectedId.map(catalog::find)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), catalog.find(selectedId.value))

    fun selectTarget(id: String) {
        selectedId.value = id
    }

    companion object {
        // Reuse the saved-state handle created before this ViewModel changed packages.
        const val SAVED_STATE_KEY =
            "androidx.lifecycle.ViewModelProvider.DefaultKey:dev.nightatlas.feature.atlas.PlanStartViewModel"
    }
}
