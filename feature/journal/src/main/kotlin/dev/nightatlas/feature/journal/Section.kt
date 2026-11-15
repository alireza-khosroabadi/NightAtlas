package dev.nightatlas.feature.journal

import dev.nightatlas.navigation.service.JournalInput

internal enum class Section(val key: String, val title: String, private val alias: String, val serviceInput: JournalInput) {
    Saved("saved", "Sightings", "logbook", JournalInput.Saved),
    Guide("guide", "Field guide", "fieldguide", JournalInput.Guide);

    companion object {
        fun match(value: String?): Section? {
            val key = value?.trim()?.trim('/')?.lowercase()?.replace("-", "")?.replace("_", "")
            return entries.find { it.key == key || it.alias == key }
        }
    }
}
