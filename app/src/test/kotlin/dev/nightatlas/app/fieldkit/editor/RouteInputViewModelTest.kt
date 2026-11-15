package dev.nightatlas.app.fieldkit.editor

import androidx.lifecycle.SavedStateHandle
import dev.nightatlas.app.InputKind
import dev.nightatlas.app.RouteInputState
import dev.nightatlas.feature.atlas.navigation.AtlasEntry
import dev.nightatlas.navigation.model.ActionEntity
import dev.nightatlas.navigation.registry.NavigationRegistryFactory
import dev.nightatlas.navigation.service.AtlasServices
import dev.nightatlas.navigation.service.ObjectInput
import org.junit.Assert.*
import org.junit.Test

class RouteInputViewModelTest {
    private val handle = SavedStateHandle()
    private val factory = NavigationRegistryFactory(setOf(AtlasEntry()))
    private val model = RouteInputViewModel(handle, factory)

    @Test
    fun invalidActionIsEditableAndAValidActionProducesTypedInput() {
        model.changeValue("{?")
        assertNull(model.submit())
        assertTrue(model.state.value.invalidJson)

        model.changeValue("""{"destination":"star","destinationType":"SERVICE","parameters":{"id":"lyra","note":null}}""")
        assertFalse(model.state.value.invalidJson)
        assertEquals(
            SubmittedInput.Action(ActionEntity.Service(AtlasServices.Object(ObjectInput.Detail("lyra")))),
            model.submit(),
        )
    }

    @Test
    fun serviceParametersMustBeAnObjectAndSurviveKindChanges() {
        model.selectKind(InputKind.Service)
        model.changeValue("  custom  ")
        model.changeParameters("[]")
        assertNull(model.submit())
        assertTrue(model.state.value.invalidJson)

        model.selectKind(InputKind.Route)
        assertFalse(model.state.value.invalidJson)
        model.changeValue("  trail/start  ")
        assertEquals(SubmittedInput.Route("trail/start"), model.submit())

        model.selectKind(InputKind.Service)
        assertEquals("[]", model.state.value.parameters)
        assertEquals(InputKind.Service.example, model.state.value.value)
        model.changeValue("  sky_object  ")
        model.changeParameters("""{"id":"orion"}""")
        assertEquals(
            SubmittedInput.Service(AtlasServices.Object(ObjectInput.Detail("orion"))),
            model.submit(),
        )
    }

    @Test
    fun aNewViewModelRestoresTheDraftAndItsValidationErrorFromSavedState() {
        model.selectKind(InputKind.Service)
        model.changeValue("custom")
        model.changeParameters("{?")
        assertNull(model.submit())

        val savedValues = handle.keys().associateWith { key -> handle.get<Any?>(key) }
        val restored = RouteInputViewModel(SavedStateHandle(savedValues), factory)
        assertEquals(
            RouteInputState(InputKind.Service, "custom", "{?", invalidJson = true),
            restored.state.value,
        )
        restored.changeParameters("{}")
        assertEquals(SubmittedInput.UnavailableService, restored.submit())
    }

    @Test
    fun selectingTheActiveKindKeepsTheDraftAndBlankInputCannotBeSubmitted() {
        model.selectKind(InputKind.Route)
        model.changeValue("  custom-route  ")
        model.selectKind(InputKind.Route)
        assertEquals(SubmittedInput.Route("custom-route"), model.submit())

        model.changeValue("  ")
        assertFalse(model.state.value.canSubmit)
        assertNull(model.submit())
        assertFalse(model.state.value.invalidJson)
    }
}
