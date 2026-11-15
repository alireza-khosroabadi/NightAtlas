package dev.nightatlas.feature.atlas.navigation

import dev.nightatlas.navigation.model.NavigationRoute
import dev.nightatlas.navigation.serialization.asTypedDestination
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("dev.nightatlas.feature.atlas.ObjectDetail")
internal data class ObjectDetail(val id: String, val title: String? = null) : NavigationRoute

@Serializable
@SerialName("dev.nightatlas.feature.atlas.FieldSlip")
internal data class FieldSlip(val id: String) {
    fun encode(): String = asTypedDestination(serializer().descriptor.serialName)
}

@Serializable
@SerialName("dev.nightatlas.feature.atlas.RecordObservation")
internal data class RecordObservation(val encodedSlip: String) : NavigationRoute

@Serializable
@SerialName("dev.nightatlas.feature.atlas.Recorded")
internal data class Recorded(val id: String) : NavigationRoute
