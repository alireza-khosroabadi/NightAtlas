package dev.nightatlas.data.journal

import android.content.Context
import dev.nightatlas.domain.journal.ObservationBook
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class LocalObservationBook(context: Context) : ObservationBook {
    private val prefs = context.getSharedPreferences("field_book", Context.MODE_PRIVATE)
    private val ids = MutableStateFlow(prefs.getStringSet("observed", emptySet()).orEmpty().toSet())
    override val observed = ids.asStateFlow()

    override fun record(id: String) = edit { it + id }

    override fun remove(id: String) = edit { it - id }

    @Synchronized
    private fun edit(transform: (Set<String>) -> Set<String>) {
        val next = transform(ids.value)
        prefs.edit().putStringSet("observed", next).apply()
        ids.value = next
    }
}
