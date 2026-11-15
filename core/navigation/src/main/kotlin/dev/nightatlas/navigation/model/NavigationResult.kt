package dev.nightatlas.navigation.model

enum class NavigationResult {
    Handled,
    FallbackOpened,
    Unhandled;

    val isHandled: Boolean
        get() = this != Unhandled
}

internal fun Boolean.asNavigationResult(): NavigationResult =
    if (this) NavigationResult.Handled else NavigationResult.Unhandled
