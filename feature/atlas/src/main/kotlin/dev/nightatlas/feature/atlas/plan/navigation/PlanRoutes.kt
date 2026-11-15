package dev.nightatlas.feature.atlas.plan.navigation

import dev.nightatlas.navigation.model.NavigationRoute
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("dev.nightatlas.feature.atlas.PlanStart")
internal data object PlanStart : NavigationRoute

@Serializable
@SerialName("dev.nightatlas.feature.atlas.PickTarget")
internal data class PickTarget(val returnToCaller: Boolean = false) : NavigationRoute

@Serializable
@SerialName("dev.nightatlas.feature.atlas.ReviewPlan")
internal data class ReviewPlan(val id: String) : NavigationRoute
