package dev.nightatlas.app.navigation

import android.content.Intent
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.nightatlas.app.MainActivity
import dev.nightatlas.app.fieldkit.navigation.Unavailable
import dev.nightatlas.navigation.intent.consumeNavigationAction
import dev.nightatlas.navigation.model.ActionEntity
import dev.nightatlas.navigation.model.JournalTab
import dev.nightatlas.navigation.service.AtlasServices
import dev.nightatlas.navigation.service.JournalInput
import dev.nightatlas.navigation.service.NavigationServices
import dev.nightatlas.navigation.service.UnsupportedInput
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RegistrationTest {
    @get:Rule val activity = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun registrationsAreCompleteAndAliasesKeepTheirPrecedence() {
        activity.scenario.onActivity { main ->
            val registry = main.registryFactory.create()
            assertEquals(emptyList<String>(), registry.validate())
            assertEquals(AtlasServices.Object.id, registry.resolveServiceId(" star "))
            assertEquals(AtlasServices.Object.id, registry.resolveServiceId("SKY/OBJECT"))
            assertEquals(AtlasServices.Object.id, registry.resolveServiceId("field", "lyra"))
            assertEquals(AtlasServices.Journal.id, registry.resolveServiceId("field", "guide"))
            assertEquals(NavigationServices.WebView.id, registry.resolveServiceId("web_view"))
            assertEquals(
                JournalTab("guide"),
                registry.resolveServiceRoute(AtlasServices.Journal(JournalInput.Guide)),
            )
        }
    }

    @Test
    fun legacyIntentPayloadDecodesToTypedRequestAndIsConsumedOnlyOnce() {
        activity.scenario.onActivity { main ->
            val registry = main.registryFactory.create()
            val intent = Intent()
            val extra = "dev.nightatlas.navigation.extra.ACTION_ENTITY"
            intent.putExtra(extra, """{"title":"Card","destination":"book","destinationType":"SERVICE","parameters":{"path":"observation/lyra"}}""")
            assertEquals(
                ActionEntity.Service(AtlasServices.Journal(JournalInput.Observation("lyra"))),
                intent.consumeNavigationAction(registry),
            )
            assertNull(intent.consumeNavigationAction(registry))
            intent.putExtra(extra, "{}")
            assertNull(intent.consumeNavigationAction(registry))
        }
    }

    @Test
    fun unknownServicePreservesItsIdentityOnTheFallbackScreen() {
        activity.scenario.onActivity { main ->
            val registry = main.registryFactory.create()
            assertNull(registry.resolveServiceId("MOON_PHASES"))
            assertEquals(
                Unavailable("MOON_PHASES", "SERVICE"),
                registry.resolveServiceRoute(
                    NavigationServices.Update(UnsupportedInput("MOON_PHASES", "SERVICE")),
                ),
            )
        }
    }
}
