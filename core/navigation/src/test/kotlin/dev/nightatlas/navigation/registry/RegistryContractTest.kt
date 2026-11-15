package dev.nightatlas.navigation.registry

import dev.nightatlas.navigation.model.CatalogTab
import dev.nightatlas.navigation.model.NamedRoute
import dev.nightatlas.navigation.registry.handler.ModelRouteHandler
import dev.nightatlas.navigation.registry.handler.TypedRouteHandler
import dev.nightatlas.navigation.service.AppServiceId
import dev.nightatlas.navigation.service.invoke
import dev.nightatlas.navigation.service.serviceContract
import org.junit.Assert.*
import org.junit.Test

class RegistryContractTest {
    private val registry = NavigationRegistry()
    private val service = serviceContract<Unit>("PLAN")
    private val id = service.id

    @Test
    fun conflictingRegistrationsFailDuringAssembly() {
        val modelHandler = ModelRouteHandler<String> { CatalogTab }
        val registrations: List<() -> Unit> =
            listOf(
                { registry.register(TypedRouteHandler(CatalogTab::class)) },
                { registry.registerString(NamedRoute("legacy")) },
                { registry.register(modelHandler) },
                { registry.registerService(service) { NamedRoute("legacy") } },
                { registry.registerServiceAlias("plan-list", id) },
                { registry.registerNormalizedServiceAlias("Plan_List", id) },
            )
        for (register in registrations) {
            register()
            assertThrows(IllegalStateException::class.java) { register() }
        }
    }

    @Test
    fun validationReportsDanglingAliasesAndDuplicateInternalExternalOwnership() {
        registry.registerServiceAlias("old-plan", id)
        assertEquals(1, registry.validate().size)
        registry.registerExternallyHandledService(id, "test")
        assertTrue(registry.validate().isEmpty())
        registry.registerService(service) { null }
        assertEquals(1, registry.validate().size)
        assertNull(registry.resolveServiceRoute(service()))
    }

    @Test
    fun typedCallsSkipDecodingAndAnotherContractCannotClaimTheSameRegistration() {
        val visits = serviceContract<Int>("visits")
        val other = serviceContract<String>("VISITS")
        var decoded = 0
        registry.registerService(visits, decode = {
            decoded++
            it["count"]?.toIntOrNull()
        }) { NamedRoute("visit/$it") }
        assertEquals(NamedRoute("visit/2"), registry.resolveServiceRoute(visits(2)))
        assertEquals(0, decoded)
        val parsed = registry.decodeService(visits.id, mapOf("count" to "3"))
        assertEquals(visits(3), parsed)
        assertEquals(NamedRoute("visit/3"), registry.resolveServiceRoute(parsed!!))
        assertEquals(1, decoded)
        assertNull(registry.resolveServiceRoute(other("3")))
        assertNull(registry.decodeService(visits.id, mapOf("count" to "?")))
    }

    @Test
    fun mostSpecificNormalizedAliasWinsInEitherRegistrationOrder() {
        val other = AppServiceId("OTHER")
        val aliases = listOf("sky" to id, "sky-object" to other)
        for (order in listOf(aliases, aliases.reversed())) {
            val routes = NavigationRegistry()
            order.forEach { (alias, service) ->
                routes.registerNormalizedServiceAlias(alias, service)
            }
            assertEquals(other, routes.resolveServiceId("old/SKY_object/detail"))
            assertEquals(id, routes.resolveServiceId("old/SKY/detail"))
            routes.registerServiceAlias("OLD/SKY_OBJECT/DETAIL", id)
            assertEquals(id, routes.resolveServiceId("old/sky_object/detail"))
        }
    }

    @Test
    fun equallySpecificAliasesMustAgreeOnTheirTarget() {
        val other = AppServiceId("OTHER")
        for (target in listOf(id, other)) {
            val aliases = listOf("sky" to id, "map" to target)
            for (order in listOf(aliases, aliases.reversed())) {
                val routes = NavigationRegistry()
                order.forEach { (alias, service) ->
                    routes.registerNormalizedServiceAlias(alias, service)
                }
                assertEquals(if (target == id) id else null, routes.resolveServiceId("sky/map"))
            }
        }
    }

    @Test
    fun emptyNormalizedAliasCannotMatchEveryService() {
        assertThrows(IllegalArgumentException::class.java) {
            registry.registerNormalizedServiceAlias("/-_", id)
        }
    }
}
