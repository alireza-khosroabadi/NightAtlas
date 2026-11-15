package dev.nightatlas.feature.atlas.plan.navigation

import org.junit.Assert.*
import org.junit.Test

class ObservationPlanTest {
    @Test
    fun explicitStepWinsOverPathAndTarget() {
        val conflicting = mapOf("path" to "review", "id" to "lyra")
        assertEquals(PlanStart, planRoute(conflicting + ("step" to "1")))
        assertEquals(PickTarget(), planRoute(conflicting + ("step" to "2")))
        assertEquals(
            ReviewPlan("lyra"),
            planRoute(mapOf("step" to "3", "path" to "pick", "id" to "lyra")),
        )
    }

    @Test
    fun pathWinsOverInferredStepAndMissingReviewTargetIsUnhandled() {
        assertEquals(
            PickTarget(),
            planRoute(mapOf("path" to "/PICK/", "id" to "lyra", "step" to "bad")),
        )
        assertEquals(ReviewPlan("orion"), planRoute(mapOf("id" to "orion")))
        assertNull(planRoute(mapOf("path" to "review")))
        assertEquals(PlanStart, planRoute(emptyMap()))
    }
}

private fun planRoute(fields: Map<String, String>) = ObservationPlan.decode(fields)?.let(ObservationPlan::resolve)
