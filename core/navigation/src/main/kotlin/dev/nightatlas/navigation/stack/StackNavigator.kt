package dev.nightatlas.navigation.stack

interface StackNavigator {
    fun execute(action: StackAction): Boolean
}
