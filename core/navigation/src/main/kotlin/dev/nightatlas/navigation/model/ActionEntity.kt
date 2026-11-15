package dev.nightatlas.navigation.model

import android.net.Uri
import dev.nightatlas.navigation.platform.SystemActionKey
import dev.nightatlas.navigation.service.ServiceRequest

sealed interface ActionEntity : NavigationTarget {
    data class Service(val request: ServiceRequest<*>) : ActionEntity
    data class UnsupportedService(val destination: String, val type: DestinationType) : ActionEntity
    data object Unhandled : ActionEntity
    data class BottomBar(val destination: String, val tabScreen: String = "") : ActionEntity
    data class Tab(val destination: String) : ActionEntity
    data class Link internal constructor(val uri: Uri, val mapped: ActionEntity?) : ActionEntity
    data class WebView(val url: String) : ActionEntity
    data class ExternalWeb(val url: String) : ActionEntity
    data class Dial(val number: String) : ActionEntity
    data class System(val key: SystemActionKey) : ActionEntity

    companion object {
        fun bottomBar(destination: String, tabScreen: String = "") = BottomBar(destination, tabScreen)
        fun dialPhone(number: String) = Dial(number)
        fun openAppDetailsSettings() = System(SystemActionKey.OPEN_APP_DETAILS_SETTINGS)
        fun openBiometricSettings() = System(SystemActionKey.OPEN_BIOMETRIC_SETTINGS)
        fun externalWeb(url: String) = ExternalWeb(url)
        fun webView(url: String) = WebView(url)
    }
}
