package dev.nightatlas.feature.journal.observation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.nightatlas.domain.catalog.SkyCatalog
import dev.nightatlas.domain.journal.ObservationBook
import dev.nightatlas.feature.journal.navigation.JournalObservation
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
internal class JournalObservationViewModel
@Inject
constructor(handle: SavedStateHandle, catalog: SkyCatalog, book: ObservationBook) : ViewModel() {
    val id = handle.toRoute<JournalObservation>().id
    val star = catalog.find(id)
    val isObserved =
        book.observed.map { id in it }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), id in book.observed.value)
}
