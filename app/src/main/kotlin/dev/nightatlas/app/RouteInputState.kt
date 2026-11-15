package dev.nightatlas.app

import dev.nightatlas.app.fieldkit.LEGACY_KIT_LINK
import dev.nightatlas.app.fieldkit.navigation.TRAIL_START
import java.io.Serializable

internal enum class InputKind(val title: String, val example: String) {
    Action(
        "JSON action",
        """{"destination":"plan","destinationType":"SERVICE","parameters":{"step":"3","id":"lyra"}}""",
    ),
    Service("Service ID", "OBSERVATION_PLAN"),
    Link("Link", LEGACY_KIT_LINK),
    Route("Route name", TRAIL_START.name),
}

internal data class RouteInputState(
    val kind: InputKind = InputKind.Action,
    val value: String = kind.example,
    val parameters: String = """{"step":"3","id":"lyra"}""",
    val invalidJson: Boolean = false,
) : Serializable {
    val canSubmit: Boolean
        get() = value.isNotBlank()
}
