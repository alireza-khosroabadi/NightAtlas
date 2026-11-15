package dev.nightatlas.feature.atlas.observation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.nightatlas.domain.catalog.SkyCatalog
import dev.nightatlas.domain.journal.ObservationBook
import dev.nightatlas.feature.atlas.navigation.FieldSlip
import dev.nightatlas.feature.atlas.navigation.RecordObservation
import dev.nightatlas.navigation.serialization.decodeTypedDestination
import javax.inject.Inject

@HiltViewModel
internal class RecordObservationViewModel
@Inject
constructor(handle: SavedStateHandle, catalog: SkyCatalog, private val book: ObservationBook) :
    ViewModel() {
    val star =
        runCatching {
                handle.toRoute<RecordObservation>().encodedSlip.decodeTypedDestination<FieldSlip>().id
            }
            .getOrNull()
            ?.let(catalog::find)

    fun record(): String? {
        val id = star?.id ?: return null
        book.record(id)
        return id
    }
}
