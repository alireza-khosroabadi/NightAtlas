package dev.nightatlas.navigation.deeplink

import android.net.Uri
import dev.nightatlas.navigation.model.ActionEntity
import dev.nightatlas.navigation.model.NavigationResult
import dev.nightatlas.navigation.model.asNavigationResult

interface DeeplinkNavigator {
    fun openLink(uri: Uri): NavigationResult = tryOpenDeeplink(uri).asNavigationResult()

    fun openLink(link: ActionEntity.Link): NavigationResult

    fun openDeeplink(uri: Uri)

    fun openDeeplink(uri: String)

    fun tryOpenDeeplink(uri: Uri): Boolean

    fun tryOpenDeeplink(uri: String): Boolean
}
