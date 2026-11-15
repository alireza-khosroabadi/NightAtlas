package dev.nightatlas.app.fieldkit.navigation

import dev.nightatlas.navigation.model.NamedRoute
import dev.nightatlas.navigation.model.NavigationRoute
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

internal val INBOX = NamedRoute("pending-inbox")
internal val INPUT = NamedRoute("route-input")
internal val TRAIL_START = NamedRoute("trail/start")
internal val TRAIL_END = NamedRoute("trail/end")

@Serializable
@SerialName("dev.nightatlas.app.TrailMarker")
internal data class TrailMarker(val visit: Int = 1) : NavigationRoute

@Serializable
@SerialName("dev.nightatlas.app.GuidePage")
internal data class GuidePage(val url: String) : NavigationRoute

@Serializable
@SerialName("dev.nightatlas.app.Unavailable")
internal data class Unavailable(val destination: String, val type: String) : NavigationRoute
