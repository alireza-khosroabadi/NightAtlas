package dev.nightatlas.feature.journal.navigation

import dev.nightatlas.navigation.model.NavigationRoute
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("dev.nightatlas.feature.journal.JournalObservation")
internal data class JournalObservation(val id: String) : NavigationRoute
