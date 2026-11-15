package dev.nightatlas.app.fieldkit.editor

import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.nightatlas.app.InputKind
import dev.nightatlas.app.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RouteInputScreenLifecycleTest {
    @get:Rule val ui = createAndroidComposeRule<MainActivity>()

    @Test
    fun stoppedScreenShowsTheLatestDraftWhenItsLifecycleStartsAgain() {
        lateinit var viewModel: RouteInputViewModel
        lateinit var owner: ScreenLifecycle
        ui.activityRule.scenario.onActivity { activity ->
            owner = ScreenLifecycle()
            owner.lifecycle.currentState = Lifecycle.State.STARTED
            viewModel = RouteInputViewModel(SavedStateHandle(), activity.registryFactory)
            viewModel.selectKind(InputKind.Route)
            viewModel.changeValue("first draft")
            activity.setContent {
                CompositionLocalProvider(LocalLifecycleOwner provides owner) {
                    RouteInputScreen(onSubmit = {}, onBack = {}, viewModel = viewModel)
                }
            }
        }

        val input = ui.onNode(hasSetTextAction())
        input.assertTextContains("first draft")
        ui.runOnIdle { owner.lifecycle.currentState = Lifecycle.State.CREATED }
        ui.runOnIdle { viewModel.changeValue("updated draft") }
        input.assertTextContains("first draft")
        ui.runOnIdle { owner.lifecycle.currentState = Lifecycle.State.STARTED }
        input.assertTextContains("updated draft")
    }

    private class ScreenLifecycle : LifecycleOwner {
        override val lifecycle = LifecycleRegistry(this)
    }
}
