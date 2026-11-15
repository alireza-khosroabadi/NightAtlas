package dev.nightatlas.navigation.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

sealed interface BottomBarRoute : NavigationRoute {
    companion object {
        fun from(path: String): BottomBarRoute =
            when (path) {
                "journal" -> JournalTab()
                else -> CatalogTab
            }
    }
}

@Serializable
@SerialName("dev.nightatlas.navigation.core.AtlasTabs")
data object AtlasTabs : BottomBarRoute

@Serializable
@SerialName("dev.nightatlas.navigation.core.CatalogTab")
data object CatalogTab : BottomBarRoute

@Serializable
@SerialName("dev.nightatlas.navigation.core.JournalTab")
data class JournalTab(val tabScreen: String = "") : BottomBarRoute
