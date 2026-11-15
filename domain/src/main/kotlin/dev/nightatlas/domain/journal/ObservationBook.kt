package dev.nightatlas.domain.journal

import kotlinx.coroutines.flow.StateFlow

interface ObservationBook {
    val observed: StateFlow<Set<String>>

    fun record(id: String)

    fun remove(id: String)
}
