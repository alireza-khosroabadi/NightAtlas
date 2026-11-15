package dev.nightatlas.app

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import dev.nightatlas.app.navigation.AtlasHost
import dev.nightatlas.navigation.deeplink.AtlasLinkMapper
import dev.nightatlas.navigation.deeplink.isAtlasDeepLink
import dev.nightatlas.navigation.intent.consumeNavigationAction
import dev.nightatlas.navigation.intent.putNavigationAction
import dev.nightatlas.navigation.model.ActionDto
import dev.nightatlas.navigation.pending.PendingNavigationViewModel
import dev.nightatlas.navigation.registry.NavigationRegistryFactory
import dev.nightatlas.navigation.service.AtlasServices
import dev.nightatlas.navigation.service.ObjectInput
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject internal lateinit var registryFactory: NavigationRegistryFactory

    private val pending by viewModels<PendingNavigationViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val systemBar = SystemBarStyle.dark(Color.TRANSPARENT)
        enableEdgeToEdge(statusBarStyle = systemBar, navigationBarStyle = systemBar)
        if (savedInstanceState == null) accept(intent)
        val registry = registryFactory.create()
        setContent { AtlasHost(this, registry, pending, ::sendObject, ::sendGuide, ::sendLink) }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        accept(intent)
    }

    private fun accept(incoming: Intent) {
        val registry = registryFactory.create()
        val action = incoming.consumeNavigationAction(registry)
        val uri = incoming.data
        // NavHost also inspects the launch intent for graph links. The pending queue owns delivery.
        incoming.data = null
        if (action != null) pending.setPendingAction(action)
        else if (uri != null && uri.isAtlasDeepLink()) pending.setPendingAction(AtlasLinkMapper(registry).decode(uri))
    }

    private fun sendLink(uri: Uri) {
        startActivity(Intent(Intent.ACTION_VIEW, uri, this, MainActivity::class.java))
    }

    private fun sendObject(input: ObjectInput.Detail) {
        val fields = buildMap {
            put("id", input.id)
            input.title?.let { put("title", it) }
        }
        sendAction(ActionDto("", AtlasServices.Object.id.value, "SERVICE", fields))
    }

    private fun sendGuide() {
        sendAction(ActionDto("", "journal", "BOTTOM_BAR", mapOf("tabScreen" to "guide")))
    }

    private fun sendAction(action: ActionDto) {
        startActivity(Intent(this, MainActivity::class.java).putNavigationAction(action))
    }
}
