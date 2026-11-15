package dev.nightatlas.navigation.service

sealed interface ObjectInput {
    data object Catalog : ObjectInput
    data class Detail(val id: String, val title: String? = null) : ObjectInput
    data class Record(val id: String) : ObjectInput
}

sealed interface JournalInput {
    data object Default : JournalInput
    data object Saved : JournalInput
    data object Guide : JournalInput
    data class Observation(val id: String) : JournalInput
}

object AtlasServices {
    val Object = serviceContract<ObjectInput>("SKY_OBJECT")
    val Journal = serviceContract<JournalInput>("OBSERVATION_BOOK")
}
