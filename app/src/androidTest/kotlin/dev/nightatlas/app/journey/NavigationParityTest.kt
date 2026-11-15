package dev.nightatlas.app.journey

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import android.provider.Settings
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.core.net.toUri
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import dev.nightatlas.app.MainActivity
import dev.nightatlas.app.fieldkit.GUIDE_URL
import dev.nightatlas.app.fieldkit.LEGACY_KIT_LINK
import dev.nightatlas.app.fieldkit.WEB_URL
import dev.nightatlas.navigation.intent.putNavigationAction
import dev.nightatlas.navigation.model.ActionDto
import dev.nightatlas.navigation.service.AtlasServices
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NavigationParityTest {
    @get:Rule val ui = createAndroidComposeRule<MainActivity>()

    private fun click(label: String) = ui.onNodeWithText(label).performScrollTo().performClick()

    private fun shown(label: String) = ui.onNodeWithText(label).assertExists()

    private fun back() = ui.runOnUiThread { ui.activity.onBackPressedDispatcher.onBackPressed() }

    private fun link(value: String) {
        ui.runOnUiThread {
            val activity = ui.activity
            activity.startActivity(
                Intent(Intent.ACTION_VIEW, value.toUri(), activity, MainActivity::class.java)
            )
        }
    }

    @Test
    fun pickerReturnsOnlyCommittedSelectionToItsOwnCaller() {
        click("Observing plan")
        click("Choose a target")
        click("Back")
        ui.onNodeWithText("Review plan").assertDoesNotExist()
        click("Choose a target")
        ui.activityRule.scenario.recreate()
        click("Lyra")
        shown("Selected target: Lyra")
        ui.activityRule.scenario.recreate()
        click("Choose a target")
        back()
        shown("Selected target: Lyra")
        click("Review plan")
        ui.activityRule.scenario.recreate()
        shown("Lyra")
        click("Log this observation")
        ui.onNodeWithText("Add to journal").performClick()
        shown("Observation logged")
        click("Finish and return to the atlas")
        shown("Sky atlas")
    }

    @Test
    fun directPickerWithNoCallerOpensReviewAndQueryPathOverridesUrlPath() {
        link("nightatlas:///plan/review?step=2&id=orion")
        shown("Choose a constellation")
        click("Cassiopeia")
        shown("Plan review")
        shown("Cassiopeia")
        link("nightatlas://hub/plan/pick?path=review&id=lyra")
        shown("Plan review")
        shown("Lyra")
        link("nightatlas://plan/review")
        shown("This destination is unavailable.")
        shown("Lyra")
    }

    @Test
    fun explicitIntentPrefersItsActionAndAcceptsAnActionlessUri() {
        ui.runOnUiThread {
            val activity = ui.activity
            activity.startActivity(
                Intent(activity, MainActivity::class.java).apply {
                    data = LEGACY_KIT_LINK.toUri()
                    putNavigationAction(
                        ActionDto("", AtlasServices.Object.id.value, "SERVICE", mapOf("id" to "lyra"))
                    )
                }
            )
        }
        shown("Lyra")
        ui.activityRule.scenario.recreate()
        shown("Lyra")
        back()
        shown("Sky atlas")
        ui.runOnUiThread {
            val activity = ui.activity
            activity.startActivity(
                Intent(activity, MainActivity::class.java).setData(LEGACY_KIT_LINK.toUri())
            )
        }
        shown("Field kit")
        back()
        shown("Sky atlas")
    }

    @Test
    fun plainTabSwitchRestoresTheLocalTabButAnExplicitRequestWins() {
        ui.onNodeWithText("Journal").performClick()
        ui.onNodeWithText("Field guide").performClick()
        ui.activityRule.scenario.recreate()
        shown("Before you observe")
        ui.onNodeWithText("Atlas").performClick()
        ui.onNodeWithText("Journal").performClick()
        shown("Before you observe")
        link("nightatlas://journal/saved")
        shown("Shortcut to Field guide")
    }

    @Test
    fun journalSwitchesSectionsLocallyAndOpensNonTabServiceDestinations() {
        link("nightatlas://book/field-guide")
        shown("Before you observe")
        for ((label, destination) in
            listOf(
                "Via service" to "Sightings",
                "Via deep link" to "Field guide",
                "Via bottom bar action" to "Sightings",
            )) {
            click("Shortcut to " + destination)
            ui.onNodeWithText(label).performClick()
            shown("Shortcut to " + if (destination == "Sightings") "Field guide" else "Sightings")
        }
        back()
        shown("Sky atlas")
        link("nightatlas://journal/observation/orion")
        shown("Observation details")
        shown("Orion")
        click("View in atlas")
        shown("Log observation")
    }

    @Test
    fun fieldKitExamplesOpenTheirSelectedDestination() {
        click("Field kit")
        for ((label, expected) in
            listOf(
                "Open Lyra by service ID" to "Lyra",
                "Open Orion from a JSON card" to "Tonight’s observation",
                "Open Cassiopeia by its legacy alias" to "Cassiopeia",
                "Try a legacy service link" to "Orion",
                "Open tonight’s target by service" to "Orion",
                "Try an unknown service" to "MOON_PHASES · SERVICE",
            )) {
            click(label)
            shown(expected)
            back()
            shown("Field kit")
        }
        click("Log an observation via deep link")
        shown("Add to journal")
        back()
        click("Open the guide through the hub")
        shown("Before you observe")
    }

    @Test
    fun routeEditorAcceptsTypedRouteNamesFromBeforeThePackageMove() {
        click("Field kit")
        click("Try a route")
        ui.onNodeWithText("Route name").performClick()
        val routes = listOf(
            "dev.nightatlas.feature.atlas.ObjectDetail/lyra" to "Log observation",
            "dev.nightatlas.feature.atlas.ReviewPlan/orion" to "Plan review",
            "dev.nightatlas.feature.journal.JournalObservation/orion" to "Observation details",
            "dev.nightatlas.app.TrailMarker?visit=7" to "Visit 7",
        )
        for ((route, expected) in routes) {
            ui.onNode(hasSetTextAction()).performTextReplacement(route)
            click("Open destination")
            shown(expected)
            back()
            shown("Try a route")
        }
    }

    @Test
    fun failedInputsLeaveTheEditorUsableAndTheGraphResolvesALegacyLinkOnce() {
        click("Field kit")
        click("Try a route")
        val input = ui.onNode(hasSetTextAction())
        input.performTextReplacement("{?")
        click("Open destination")
        shown("Enter valid JSON for this request.")
        input.performTextReplacement(
            """{"destination":"letters","destinationType":"TEL_DEEP_LINK"}"""
        )
        click("Open destination")
        shown("This destination is unavailable.")
        input.performTextReplacement("""{"destination":"WEB_VIEW","destinationType":"SERVICE"}""")
        click("Open destination")
        shown("Try a route")
        input.performTextReplacement(
            """{"destination":"star","destinationType":"SERVICE","parameters":{"id":"lyra","title":"From the route editor"}}"""
        )
        click("Open destination")
        shown("From the route editor")
        back()
        ui.activityRule.scenario.recreate()
        shown("Try a route")
        ui.onNode(hasSetTextAction()).assertTextContains("From the route editor", substring = true)
        ui.onNodeWithText("Link").performClick()
        click("Open destination")
        shown("Field kit")
        back()
        shown("Try a route")
        ui.onNodeWithText("Route name").performClick()
        click("Open destination")
        shown("Stop 1")
    }

    @Test
    fun directServiceAndUnknownDtoTypeExerciseDifferentFallbacks() {
        click("Field kit")
        click("Try a route")
        ui.onNodeWithText("Service ID").performClick()
        ui.onNodeWithText("Input").performTextReplacement("unknown-service")
        click("Open destination")
        shown("This service is unavailable.")
        ui.onNodeWithText("Input").performTextReplacement("observation_plan")
        ui.onNodeWithText("Service parameters (JSON)").performTextReplacement("{?")
        click("Open destination")
        shown("Enter valid JSON for this request.")
        ui.activityRule.scenario.recreate()
        shown("Enter valid JSON for this request.")
        ui.onNodeWithText("Service parameters (JSON)")
            .performTextReplacement("""{"step":"3","id":"lyra"}""")
        ui.onNodeWithText("Enter valid JSON for this request.").assertDoesNotExist()
        click("Open destination")
        shown("Plan review")
        shown("Lyra")
        back()
        ui.onNodeWithText("JSON action").performClick()
        ui.onNode(hasSetTextAction())
            .performTextReplacement("""{"destination":"$GUIDE_URL","destinationType":"old-type"}""")
        click("Open destination")
        ui.runOnIdle {
            fun containsWebView(view: View): Boolean =
                view is WebView ||
                    (view is ViewGroup &&
                        (0 until view.childCount).any { containsWebView(view.getChildAt(it)) })
            assertTrue(containsWebView(ui.activity.window.decorView))
        }
    }

    @Test
    fun phoneBrowserAndSettingsHandOffValidatedAndroidIntents() {
        val sent = mutableListOf<Intent>()
        val monitor =
            object : Instrumentation.ActivityMonitor() {
                override fun onStartActivity(intent: Intent): Instrumentation.ActivityResult? {
                    if (
                        intent.action !in
                            setOf(
                                Intent.ACTION_DIAL,
                                Intent.ACTION_VIEW,
                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Settings.ACTION_BIOMETRIC_ENROLL,
                                Settings.ACTION_SECURITY_SETTINGS,
                            )
                    )
                        return null
                    sent.add(Intent(intent))
                    return Instrumentation.ActivityResult(Activity.RESULT_CANCELED, null)
                }
            }
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        instrumentation.addMonitor(monitor)
        try {
            click("Field kit")
            click("Try a route")
            ui.onNode(hasSetTextAction())
                .performTextReplacement(
                    """{"destination":" *123# ","destinationType":"TEL_DEEP_LINK"}"""
                )
            click("Open destination")
            assertEquals(Intent.ACTION_DIAL, sent.last().action)
            assertEquals("*123#", sent.last().data?.schemeSpecificPart)
            back()
            ui.onNodeWithText("Android").performClick()
            click("Read about stars on NASA’s website")
            assertEquals(Intent.ACTION_VIEW, sent.last().action)
            assertEquals(WEB_URL, sent.last().dataString)
            click("App settings")
            assertEquals(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, sent.last().action)
            assertEquals("package:dev.nightatlas", sent.last().dataString)
            click("Biometric settings")
            assertTrue(
                sent.last().action in
                    setOf(Settings.ACTION_BIOMETRIC_ENROLL, Settings.ACTION_SECURITY_SETTINGS)
            )
        } finally {
            instrumentation.removeMonitor(monitor)
        }
    }

    @Test
    fun deferredIntentAndUriAreDeliveredInOrderOnce() {
        click("Field kit")
        ui.onNodeWithText("Android").performClick()
        click("Pending requests")
        click("Queue the Orion action")
        click("Queue the Lyra link")
        shown("Open queued requests")
        ui.activityRule.scenario.recreate()
        click("Open queued requests")
        shown("Lyra")
        back()
        shown("Orion")
        back()
        shown("Field kit")
    }

    @Test
    fun stackTargetsDistinguishTwoInstancesOfTheSameTypedDestination() {
        click("Field kit")
        ui.onNodeWithText("Back stack").performClick()
        click("Start at stop 1")
        click("Go to stop 2")
        click("Visit this stop again")
        shown("Visit 2")
        click("Return to the first visit")
        shown("Visit 1")
        click("Visit this stop again")
        click("Replace the stack from the first visit")
        shown("Visit 3")
        back()
        shown("Stop 1")
    }
}
