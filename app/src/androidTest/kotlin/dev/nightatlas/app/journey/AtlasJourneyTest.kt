package dev.nightatlas.app.journey

import android.content.Intent
import android.net.Uri
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.nightatlas.app.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AtlasJourneyTest {
    @get:Rule val ui = createAndroidComposeRule<MainActivity>()

    private fun click(label: String) = ui.onNodeWithText(label).performScrollTo().performClick()

    private fun sendLink(link: String) {
        ui.runOnUiThread {
            ui.activity.startActivity(
                Intent(Intent.ACTION_VIEW, Uri.parse(link), ui.activity, MainActivity::class.java)
            )
        }
    }

    @Test
    fun recordedObjectSurvivesRecreationAndOpensAcrossFeatures() {
        click("Lyra")
        click("Log observation")
        ui.onNodeWithText("Add to journal").performClick()
        ui.onNodeWithText("Observation logged").assertExists()
        ui.activityRule.scenario.recreate()
        ui.onNodeWithText("Observation logged").assertExists()
        ui.onNodeWithText("Lyra").assertExists()
        click("View journal")
        ui.activityRule.scenario.recreate()
        click("Lyra")
        ui.onNodeWithText("Log observation").assertExists()
        click("Back")
        click("Remove Lyra from the journal")
        ui.activityRule.scenario.recreate()
        ui.onNodeWithText("Remove Lyra from the journal").assertDoesNotExist()
        ui.onNodeWithText("Sightings").assertIsSelected()
    }

    @Test
    fun detailResolvesEachEntryIndependentlyWhenAnObjectIsMissing() {
        sendLink("nightatlas://star/orion?title=First%20target")
        ui.onNodeWithText("Orion").assertExists()
        ui.onNodeWithText("First target").assertExists()

        sendLink("nightatlas://star/not-in-catalog")
        ui.onNodeWithText("Unknown object").assertExists()
        ui.activityRule.scenario.recreate()
        ui.onNodeWithText("Unknown object").assertExists()
        ui.onNodeWithText("Log observation").assertDoesNotExist()
        click("Back")
        ui.onNodeWithText("Orion").assertExists()
        ui.onNodeWithText("First target").assertExists()
        ui.onNodeWithText("Log observation").assertExists()
    }

    @Test
    fun incomingLinkAndSerializedIntentSelectTheRequestedJournalTab() {
        sendLink("nightatlas://hub/field/guide")
        ui.onNodeWithText("Before you observe").assertExists()
        ui.onNodeWithText("Atlas").performClick()
        click("Field kit")
        ui.onNodeWithText("Android").performClick()
        click("Open the guide via intent")
        ui.onNodeWithText("Before you observe").assertExists()
    }

    @Test
    fun missingServiceShowsTheRequestedDestination() {
        sendLink("nightatlas://moon-phases")
        ui.onNodeWithText("moon-phases · SERVICE").assertExists()
    }

    @Test
    fun typedPopRemovesMarkerAndLeavesItsStringParent() {
        click("Field kit")
        ui.onNodeWithText("Back stack").performClick()
        click("Start at stop 1")
        click("Go to stop 2")
        click("Go to stop 3")
        click("Pop through stop 2")
        ui.onNodeWithText("Stop 1").assertExists()
    }

    @Test
    fun eachStackCommandReachesItsAdvertisedScreen() {
        val examples =
            listOf(
                "Pop to stop 1 by route name" to "Stop 1",
                "Pop through stop 1" to "Field kit",
                "Pop to stop 2 by route type" to "Stop 2",
                "Replace stop 1 with the field kit" to "Field kit",
                "Pop two screens" to "Stop 1",
                "Replace this screen" to "Stop 3",
                "Return to the atlas via service" to "Sky atlas",
                "Clear the stack and start again" to "Sky atlas",
            )
        for ((command, expected) in examples) {
            sendLink("nightatlas://catalog")
            click("Field kit")
            ui.onNodeWithText("Back stack").performClick()
            click("Start at stop 1")
            click("Go to stop 2")
            click("Go to stop 3")
            click(command)
            ui.onNodeWithText(expected).assertExists()
        }
    }
}
