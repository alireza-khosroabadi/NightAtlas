package dev.nightatlas.feature.atlas.plan.navigation

import dev.nightatlas.navigation.model.NavigationRoute
import dev.nightatlas.navigation.service.serviceContract

internal sealed interface PlanInput {
    data object Start : PlanInput
    data object Pick : PlanInput
    data class Review(val id: String) : PlanInput
}

internal object ObservationPlan {
    val service = serviceContract<PlanInput>("OBSERVATION_PLAN")

    // An explicit step wins over the path. Without either, a supplied target opens its review.
    fun decode(params: Map<String, String>): PlanInput? {
        val id = params["id"]?.takeIf { it.isNotBlank() }
        val path = params["path"].orEmpty().trim('/').lowercase()
        val step =
            params["step"]?.toIntOrNull()?.takeIf { it in 1..3 }
                ?: when {
                    path == "start" -> 1
                    path == "pick" -> 2
                    path == "review" -> 3
                    id != null -> 3
                    else -> 1
                }
        return when (step) {
            1 -> PlanInput.Start
            2 -> PlanInput.Pick
            else -> id?.let(PlanInput::Review)
        }
    }

    fun resolve(input: PlanInput): NavigationRoute = when (input) {
        PlanInput.Start -> PlanStart
        PlanInput.Pick -> PickTarget()
        is PlanInput.Review -> ReviewPlan(input.id)
    }
}
