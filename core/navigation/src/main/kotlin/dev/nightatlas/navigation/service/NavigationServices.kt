package dev.nightatlas.navigation.service

import dev.nightatlas.navigation.model.ActionEntity

data class WebInput(val url: String)

data class UnsupportedInput(val destination: String, val type: String)

object NavigationServices {
    val WebView = serviceContract<WebInput>("WEB_VIEW")
    val ExternalWeb = serviceContract<WebInput>("EXTERNAL_WEB")
    val Update = serviceContract<UnsupportedInput>("UPDATE")

    private fun decodeUrl(fields: Map<String, String>): WebInput? = fields["url"]?.let(::WebInput)

    internal val platformHandlers = listOf(
        ServiceRegistration(WebView, ::decodeUrl) { ActionEntity.WebView(it.url) },
        ServiceRegistration(ExternalWeb, ::decodeUrl) { ActionEntity.ExternalWeb(it.url) },
    ).associateBy { it.contract.id }
}
