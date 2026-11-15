package dev.nightatlas.feature.atlas.navigation

import dev.nightatlas.navigation.model.CatalogTab
import dev.nightatlas.navigation.serialization.decodeTypedDestination
import org.junit.Assert.assertEquals
import org.junit.Test

class ObjectServiceTest {
    @Test
    fun missingObjectReturnsTheCatalog() {
        assertEquals(CatalogTab, objectRoute(emptyMap()))
    }

    @Test
    fun explicitIdAndTitleOverridePathDefaults() {
        assertEquals(
            ObjectDetail("lyra", "Tonight"),
            objectRoute(
                mapOf("id" to "lyra", "path" to "orion", "title" to "Tonight")
            ),
        )
    }

    @Test
    fun bothStepAndPathOpenTheSameConfirmation() {
        val fromStep = objectRoute(mapOf("id" to "lyra", "step" to "2"))
        val fromPath = objectRoute(mapOf("path" to "/lyra/record/"))
        assertEquals(fromStep, fromPath)
        assertEquals(
            FieldSlip("lyra"),
            (fromPath as RecordObservation).encodedSlip.decodeTypedDestination<FieldSlip>(),
        )
    }

    @Test
    fun twoStepObjectServiceAlsoHonorsExplicitFirstStep() {
        val params = mapOf("path" to "lyra/record")
        assertEquals(ObjectDetail("lyra"), objectRoute(params + ("step" to "1")))
        val confirm = objectRoute(params) as RecordObservation
        assertEquals(FieldSlip("lyra"), confirm.encodedSlip.decodeTypedDestination<FieldSlip>())
    }

    @Test
    fun fieldSlipKeepsItsEncodedIdentityAfterMovingPackages() {
        val payload = """{"navType":"dev.nightatlas.feature.atlas.FieldSlip","data":{"id":"lyra"}}"""
        val legacy = java.net.URLEncoder.encode(payload, "UTF-8")
        assertEquals(legacy, FieldSlip("lyra").encode())
        assertEquals(FieldSlip("lyra"), legacy.decodeTypedDestination<FieldSlip>())
    }
}

private fun objectRoute(fields: Map<String, String>) = ObjectService.resolveRoute(ObjectService.decode(fields))
