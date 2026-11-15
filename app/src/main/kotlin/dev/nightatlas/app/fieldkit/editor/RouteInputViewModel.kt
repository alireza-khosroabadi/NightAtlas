package dev.nightatlas.app.fieldkit.editor

import androidx.core.net.toUri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.nightatlas.app.InputKind
import dev.nightatlas.app.RouteInputState
import dev.nightatlas.navigation.deeplink.AtlasLinkMapper
import dev.nightatlas.navigation.model.ActionDto
import dev.nightatlas.navigation.model.ActionEntity
import dev.nightatlas.navigation.registry.NavigationRegistryFactory
import dev.nightatlas.navigation.service.AppServiceId
import dev.nightatlas.navigation.service.ServiceRequest
import javax.inject.Inject
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

internal sealed interface SubmittedInput {
    data class Action(val action: ActionEntity) : SubmittedInput
    data class Service(val request: ServiceRequest<*>) : SubmittedInput
    data object UnavailableService : SubmittedInput
    data class Route(val name: String) : SubmittedInput
}

@HiltViewModel
internal class RouteInputViewModel @Inject constructor(
    handle: SavedStateHandle,
    registryFactory: NavigationRegistryFactory,
) : ViewModel() {
    private val registry = registryFactory.create()
    private val links = AtlasLinkMapper(registry)
    private val draft = handle.getMutableStateFlow("routeInput.draft", RouteInputState())
    val state = draft.asStateFlow()

    fun selectKind(kind: InputKind) {
        if (kind != draft.value.kind) {
            draft.value = draft.value.copy(kind = kind, value = kind.example, invalidJson = false)
        }
    }

    fun changeValue(value: String) {
        draft.value = draft.value.copy(value = value, invalidJson = false)
    }

    fun changeParameters(parameters: String) {
        draft.value = draft.value.copy(parameters = parameters, invalidJson = false)
    }

    fun submit(): SubmittedInput? {
        val input = draft.value
        if (!input.canSubmit) return null
        return try {
            when (input.kind) {
                InputKind.Action ->
                    SubmittedInput.Action(Json.decodeFromString<ActionDto>(input.value).toDomainModel(registry))
                InputKind.Service -> {
                    val fields = Json.decodeFromString<Map<String, String>>(input.parameters)
                    registry.decodeService(AppServiceId(input.value), fields)?.let(SubmittedInput::Service)
                        ?: SubmittedInput.UnavailableService
                }
                InputKind.Link -> SubmittedInput.Action(links.decode(input.value.trim().toUri()))
                InputKind.Route -> SubmittedInput.Route(input.value.trim())
            }
        } catch (_: SerializationException) {
            draft.value = input.copy(invalidJson = true)
            null
        }
    }

    companion object {
        // Reuse the saved-state handle created before this ViewModel changed packages.
        const val SAVED_STATE_KEY =
            "androidx.lifecycle.ViewModelProvider.DefaultKey:dev.nightatlas.app.RouteInputViewModel"
    }
}
