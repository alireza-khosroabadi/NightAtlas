package dev.nightatlas.navigation.service

/**
 * Identity of a navigable app service.
 *
 * Backend, deeplinks and persisted preferences all supply service identity as a free-form string
 * with inconsistent casing (`SKY_OBJECT`, `sky_object`, `Sky_Object`). The value is therefore
 * normalized on construction, which makes [equals] — and every `Map<AppServiceId, _>` lookup built
 * on it — case-insensitive by construction.
 *
 * Consequence: never compare [value] to a raw string literal. Wrap the literal in an [AppServiceId]
 * and compare the ids.
 *
 * The original, un-normalized string is not retained here. Callers that must report the exact
 * backend spelling (analytics, logging) should keep it alongside the id.
 */
@JvmInline
value class AppServiceId private constructor(val value: String) {

    /**
     * True when the source string carried no identity at all — `null`, empty or whitespace.
     *
     * Backend payloads and persisted preferences both default a missing id to `""`, so a blank id
     * is a data condition rather than a programming error. It is a well-formed [AppServiceId] that
     * can never resolve to a handler: nothing registers the empty id. Callers that can represent
     * absence should use [parseOrNull] instead of testing this.
     */
    val isBlank: Boolean
        get() = value.isEmpty()

    override fun toString(): String = value

    companion object {
        /**
         * Kotlin's no-arg [String.uppercase] is locale-independent, so this is stable regardless of
         * the device locale.
         */
        operator fun invoke(raw: String): AppServiceId = AppServiceId(raw.trim().uppercase())

        /**
         * Total [invoke] with absence made explicit: returns `null` for a `null`, empty or
         * whitespace-only source rather than a blank id that silently never resolves.
         *
         * Prefer this at data boundaries (DTO and persisted-preference mapping) wherever the
         * surrounding type can hold a `null`.
         */
        fun parseOrNull(raw: String?): AppServiceId? =
            // `invoke(it)`, not `AppServiceId(it)`: the private constructor is in scope inside
            // the class and wins overload resolution, which would skip normalization.
            raw?.let { invoke(it) }?.takeUnless { it.isBlank }
    }
}

fun String.toAppServiceId(): AppServiceId = AppServiceId(this)

/**
 * [toAppServiceId] for sources that may legitimately carry no id.
 *
 * See [AppServiceId.parseOrNull].
 */
fun String?.toAppServiceIdOrNull(): AppServiceId? = AppServiceId.parseOrNull(this)
