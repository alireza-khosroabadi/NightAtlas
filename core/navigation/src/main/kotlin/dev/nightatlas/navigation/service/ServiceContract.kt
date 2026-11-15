package dev.nightatlas.navigation.service

import dev.nightatlas.navigation.model.NavigationTarget

class ServiceContract<I : Any>(val id: AppServiceId, private val inputClass: Class<I>) {
    operator fun invoke(input: I): ServiceRequest<I> = ServiceRequest(this, input)

    /** A different contract can use the same input type or the same wire ID. */
    fun inputOf(request: ServiceRequest<*>): I? {
        val type = inputClass
        return if (request.contract === this) type.cast(request.input) else null
    }
}

data class ServiceRequest<I : Any> internal constructor(val contract: ServiceContract<I>, val input: I)

inline fun <reified I : Any> serviceContract(id: String): ServiceContract<I> =
    ServiceContract(AppServiceId(id), I::class.javaObjectType)

operator fun ServiceContract<Unit>.invoke(): ServiceRequest<Unit> = invoke(Unit)

internal class ServiceRegistration<I : Any>(
    val contract: ServiceContract<I>,
    private val decoder: (Map<String, String>) -> I?,
    private val resolver: (I) -> NavigationTarget?,
) {
    fun decode(fields: Map<String, String>): ServiceRequest<I>? = decoder(fields)?.let(contract::invoke)

    fun resolve(request: ServiceRequest<*>): NavigationTarget? =
        contract.inputOf(request)?.let(resolver)
}
