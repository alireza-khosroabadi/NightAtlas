package dev.nightatlas.navigation.serialization

import kotlinx.serialization.Serializable

@Serializable data class TypedDestination<T : Any>(val navType: String, val data: T)
