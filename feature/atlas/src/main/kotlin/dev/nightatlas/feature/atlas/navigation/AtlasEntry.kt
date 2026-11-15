package dev.nightatlas.feature.atlas.navigation

import dev.nightatlas.feature.atlas.plan.navigation.ObservationPlan
import dev.nightatlas.feature.atlas.plan.navigation.PickTarget
import dev.nightatlas.feature.atlas.plan.navigation.PlanStart
import dev.nightatlas.feature.atlas.plan.navigation.ReviewPlan
import dev.nightatlas.navigation.feature.FeatureEntry
import dev.nightatlas.navigation.registry.NavigationRegistry
import dev.nightatlas.navigation.registry.handler.TypedRouteHandler
import dev.nightatlas.navigation.service.AtlasServices
import javax.inject.Inject

class AtlasEntry @Inject constructor() : FeatureEntry {
    override fun registerNavigation(registry: NavigationRegistry) {
        registry.register(TypedRouteHandler(PlanStart::class))
        registry.register(TypedRouteHandler(PickTarget::class))
        registry.register(TypedRouteHandler(ReviewPlan::class))
        registry.registerService(
            ObservationPlan.service,
            setOf("plan", "observing-list"),
            ObservationPlan::decode,
            ObservationPlan::resolve,
        )
        registry.register(catalogSelectionHandler)
        registry.register(TypedRouteHandler(ObjectDetail::class))
        registry.register(TypedRouteHandler(RecordObservation::class))
        registry.register(TypedRouteHandler(Recorded::class))
        registry.registerService(
            AtlasServices.Object,
            setOf("star", "object"),
            ObjectService::decode,
            ObjectService::resolveRoute,
        )
        registry.registerNormalizedServiceAlias("sky-object", AtlasServices.Object.id)
    }
}
