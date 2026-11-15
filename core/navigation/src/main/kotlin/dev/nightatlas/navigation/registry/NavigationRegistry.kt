package dev.nightatlas.navigation.registry

import androidx.navigation.NavController
import dev.nightatlas.navigation.logging.NavigationLogger
import dev.nightatlas.navigation.model.NamedRoute
import dev.nightatlas.navigation.model.NavigationRoute
import dev.nightatlas.navigation.model.NavigationTarget
import dev.nightatlas.navigation.model.RegistryTarget
import dev.nightatlas.navigation.registry.handler.ModelRouteHandler
import dev.nightatlas.navigation.registry.handler.RouteHandler
import dev.nightatlas.navigation.service.AppServiceId
import dev.nightatlas.navigation.service.AppServiceIdResolver
import dev.nightatlas.navigation.service.NavigationServices
import dev.nightatlas.navigation.service.ServiceContract
import dev.nightatlas.navigation.service.ServiceRegistration
import dev.nightatlas.navigation.service.ServiceRequest
import kotlin.reflect.KClass

class NavigationRegistry : AppServiceIdResolver {

    private val typedHandlers = linkedMapOf<KClass<out NavigationRoute>, RouteHandler>()
    private val broadTypedHandlers = mutableListOf<RouteHandler>()
    private val stringHandlers = linkedMapOf<NamedRoute, (NavController, NamedRoute) -> Unit>()
    private val modelHandlers = mutableSetOf<ModelRouteHandler<*>>()
    private val serviceHandlers = linkedMapOf<AppServiceId, ServiceRegistration<*>>()
    private val serviceAliases = linkedMapOf<String, ServiceAliasResolver>()
    private val normalizedServiceAliases = mutableListOf<NormalizedServiceAlias>()
    private val staticAliasTargets = mutableListOf<AliasTarget>()
    private val externallyHandledServices = linkedMapOf<AppServiceId, String>()

    fun register(handler: RouteHandler) {
        val routeClass = handler.routeClass
        if (routeClass == null) {
            broadTypedHandlers.add(handler)
        } else {
            putUnique(typedHandlers, routeClass, handler, "typed route")
        }
    }

    fun register(handler: ModelRouteHandler<*>) {
        check(modelHandlers.add(handler)) { "Duplicate model route handler registration" }
    }

    fun registerString(
        route: NamedRoute,
        navigate: (NavController, NamedRoute) -> Unit = { controller, target ->
            controller.navigate(target.name)
        },
    ) {
        putUnique(stringHandlers, route, navigate, "string route")
    }

    fun <I : Any> registerService(
        contract: ServiceContract<I>,
        aliases: Set<String> = emptySet(),
        decode: (Map<String, String>) -> I?,
        resolve: (I) -> NavigationTarget?,
    ) {
        putUnique(serviceHandlers, contract.id, ServiceRegistration(contract, decode, resolve), "service route")
        aliases.forEach { alias -> registerServiceAlias(alias, contract.id) }
    }

    fun registerService(contract: ServiceContract<Unit>, resolve: () -> NavigationTarget?) {
        registerService(contract, decode = { Unit }) { resolve() }
    }

    /** Direct service IDs bypass aliases. Action and link ingress resolve aliases first. */
    fun decodeService(id: AppServiceId, fields: Map<String, String>): ServiceRequest<*>? =
        try {
            handlerFor(id)?.decode(fields)
        } catch (e: Exception) {
            NavigationLogger.e(TAG, "Failed to decode parameters for service '$id'", e)
            null
        }

    private fun handlerFor(id: AppServiceId): ServiceRegistration<*>? =
        NavigationServices.platformHandlers[id] ?: serviceHandlers[id]

    fun registerServiceAlias(alias: String, id: AppServiceId) {
        registerServiceAlias(alias, ServiceAliasResolver { id })
        staticAliasTargets += AliasTarget(alias = alias, id = id)
    }

    fun registerPathAwareServiceAlias(alias: String, resolver: (String?) -> AppServiceId?) {
        registerServiceAlias(alias, ServiceAliasResolver(resolver))
    }

    fun registerNormalizedServiceAlias(alias: String, id: AppServiceId) {
        val normalizedAlias = alias.normalizedLookupKey()
        require(normalizedAlias.isNotBlank()) { "A normalized service alias must not be blank" }
        check(normalizedServiceAliases.none { it.alias == normalizedAlias }) {
            "Duplicate normalized service alias registration for $alias"
        }
        normalizedServiceAliases += NormalizedServiceAlias(alias = normalizedAlias, id = id)
        staticAliasTargets += AliasTarget(alias = alias, id = id)
    }

    /** Declares an alias target handled outside the registry for assembly-time validation. */
    fun registerExternallyHandledService(id: AppServiceId, handledBy: String) {
        putUnique(
            target = externallyHandledServices,
            key = id,
            value = handledBy,
            label = "externally handled service",
        )
    }

    fun navigate(navController: NavController, route: RegistryTarget): Boolean =
        when (route) {
            is NamedRoute -> navigateString(navController, route)
            is NavigationRoute -> navigateTyped(navController, route)
            is NavigationTarget.Model<*> -> navigateModel(navController, route)
        }

    private fun navigateModel(navController: NavController, input: NavigationTarget.Model<*>): Boolean {
        if (input.handler !in modelHandlers) return false
        return try {
            navController.navigate(input.destination())
            true
        } catch (e: Exception) {
            NavigationLogger.e(TAG, "Failed to navigate mapped model", e)
            false
        }
    }

    private fun navigateTyped(navController: NavController, route: NavigationRoute): Boolean {
        return try {
            val handler = findTypedHandler(route)
            if (handler == null) {
                NavigationLogger.e(
                    TAG,
                    "No typed handler found for ${route::class.qualifiedName}. Registered routes: ${registeredTypedRoutes()}",
                )
                return false
            }
            handler.navigate(navController, route)
            true
        } catch (e: Exception) {
            NavigationLogger.e(
                TAG,
                "Failed to navigate typed route ${route::class.qualifiedName}",
                e,
            )
            false
        }
    }

    private fun navigateString(navController: NavController, route: NamedRoute): Boolean {
        return try {
            stringHandlers[route]?.invoke(navController, route) ?: navController.navigate(route.name)
            true
        } catch (e: Exception) {
            NavigationLogger.e(TAG, "Failed to navigate string route $route", e)
            false
        }
    }

    fun resolveServiceRoute(request: ServiceRequest<*>): NavigationTarget? =
        try {
            handlerFor(request.contract.id)?.resolve(request)
        } catch (e: Exception) {
            NavigationLogger.e(TAG, "Failed to resolve service '${request.contract.id}'", e)
            null
        }

    /**
     * Reports registrations that can never resolve. Cheap enough to run at assembly time; see
     * `NavigationRegistryFactory`.
     *
     * Covers what is statically knowable: an alias whose target id has no handler and is not
     * declared via [registerExternallyHandledService]. Path-aware aliases pick their target at
     * resolve time and are therefore out of scope.
     */
    fun validate(): List<String> {
        val handled = serviceHandlers.keys + externallyHandledServices.keys

        val danglingAliases =
            staticAliasTargets
                .filterNot { it.id in handled }
                .map { (alias, id) ->
                    "Alias '$alias' targets service id '$id', which no feature registered a handler for. " +
                        "Register it in the owning feature, or declare it with " +
                        "registerExternallyHandledService() if it is resolved before the registry."
                }

        val blankServiceIds =
            serviceHandlers.keys
                .filter { it.isBlank }
                .map {
                    "A service handler is registered under a blank id, which no lookup can produce."
                }

        val unusedExternalDeclarations =
            externallyHandledServices
                .filterKeys { it in serviceHandlers.keys }
                .map { (id, handledBy) ->
                    "Service id '$id' is declared as externally handled by $handledBy but also has a " +
                        "registered handler. One of the two is dead."
                }

        return danglingAliases + blankServiceIds + unusedExternalDeclarations
    }

    override fun resolveServiceId(appServiceKey: String, path: String?): AppServiceId? {
        // Backend payloads default a missing id to "". It is well-formed but unregistrable,
        // so short-circuit rather than run it through alias matching.
        if (appServiceKey.isBlank()) return null

        val exactAlias = serviceAliases[appServiceKey.aliasKey()]?.resolve(path)
        if (exactAlias != null) return exactAlias

        val normalizedKey = appServiceKey.normalizedLookupKey()
        val matches = normalizedServiceAliases.filter { normalizedKey.contains(it.alias) }
        val longest = matches.maxOfOrNull { it.alias.length }
        if (longest != null) {
            val best = matches.filter { it.alias.length == longest }
            val id = best.first().id
            if (best.all { it.id == id }) return id
            NavigationLogger.e(
                TAG,
                "Ambiguous normalized service aliases for '$appServiceKey': " +
                    best.map { it.alias }.sorted().joinToString(),
            )
            return null
        }

        return AppServiceId(appServiceKey).takeIf(serviceHandlers::containsKey)
    }

    private fun findTypedHandler(route: NavigationRoute): RouteHandler? {
        typedHandlers[route::class]?.let {
            return it
        }

        return typedHandlers.values
            .asSequence()
            .plus(broadTypedHandlers.asSequence())
            .filter { it.canHandle(route) }
            .minByOrNull { (it.routeClass ?: it::class).qualifiedName.orEmpty() }
    }

    private fun registeredTypedRoutes(): String {
        return typedHandlers.keys
            .map { it.qualifiedName.orEmpty() }
            .plus(broadTypedHandlers.map { it::class.qualifiedName.orEmpty() })
            .sorted()
            .joinToString()
    }

    private fun <K, V> putUnique(target: MutableMap<K, V>, key: K, value: V, label: String) {
        val existing = target[key]
        check(existing == null) {
            "Duplicate $label registration for $key. Existing=${existing!!::class.qualifiedName}, new=${value!!::class.qualifiedName}"
        }
        target[key] = value
    }

    private fun registerServiceAlias(alias: String, resolver: ServiceAliasResolver) {
        putUnique(
            target = serviceAliases,
            key = alias.aliasKey(),
            value = resolver,
            label = "service alias",
        )
    }

    /**
     * Aliases are arbitrary backend/deeplink spellings (`old-sky`, `skyObject`) rather than service
     * ids, so they get their own case folding.
     */
    private fun String.aliasKey(): String = trim().lowercase()

    private fun String.normalizedLookupKey(): String =
        replace("/", "").replace("-", "").replace("_", "").lowercase()

    private fun interface ServiceAliasResolver {
        fun resolve(path: String?): AppServiceId?
    }

    private data class NormalizedServiceAlias(val alias: String, val id: AppServiceId)

    /** An alias with a statically known target, recorded for [validate]. */
    private data class AliasTarget(val alias: String, val id: AppServiceId)

    private companion object {
        const val TAG = "NavigationRegistry"
    }
}
