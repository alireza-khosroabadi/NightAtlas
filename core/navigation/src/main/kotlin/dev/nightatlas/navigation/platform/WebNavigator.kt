package dev.nightatlas.navigation.platform

interface WebNavigator {

    fun openWebView(url: String): Boolean

    fun openExternal(url: String): Boolean
}
