package dev.nightatlas.navigation.platform

interface SystemActionNavigator {
    fun open(action: SystemAction): Boolean
}
