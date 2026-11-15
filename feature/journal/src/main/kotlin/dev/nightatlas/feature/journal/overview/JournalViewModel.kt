package dev.nightatlas.feature.journal.overview

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.nightatlas.domain.catalog.Constellation
import dev.nightatlas.domain.catalog.SkyCatalog
import dev.nightatlas.domain.journal.ObservationBook
import dev.nightatlas.feature.journal.Section
import dev.nightatlas.navigation.model.JournalTab
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

internal data class JournalState(
    val section: Section,
    val sightings: List<Constellation>,
    val hasObservations: Boolean,
)

@HiltViewModel
internal class JournalViewModel
@Inject
constructor(
    handle: SavedStateHandle,
    private val catalog: SkyCatalog,
    private val book: ObservationBook,
) : ViewModel() {
    private val section =
        handle.getMutableStateFlow(
            "journal.section",
            Section.match(handle.toRoute<JournalTab>().tabScreen) ?: Section.Saved,
        )

    val state =
        combine(section, book.observed, ::journalState)
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                journalState(section.value, book.observed.value),
            )

    fun selectSection(value: Section) {
        section.value = value
    }

    fun remove(id: String) = book.remove(id)

    private fun journalState(section: Section, observed: Set<String>) =
        JournalState(
            section = section,
            sightings = catalog.constellations.filter { it.id in observed },
            hasObservations = observed.isNotEmpty(),
        )

    companion object {
        // Reuse the saved-state handle created before this ViewModel changed packages.
        const val SAVED_STATE_KEY =
            "androidx.lifecycle.ViewModelProvider.DefaultKey:dev.nightatlas.feature.journal.JournalViewModel"
    }
}
