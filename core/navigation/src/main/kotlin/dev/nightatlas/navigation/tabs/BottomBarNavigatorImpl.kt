package dev.nightatlas.navigation.tabs

import dev.nightatlas.navigation.logging.NavigationLogger
import dev.nightatlas.navigation.model.AtlasTabs
import dev.nightatlas.navigation.model.BottomBarRoute
import dev.nightatlas.navigation.model.CatalogTab
import dev.nightatlas.navigation.model.JournalTab
import dev.nightatlas.navigation.routing.NavigationContainer

class BottomBarNavigatorImpl(private val container: NavigationContainer) : BottomBarNavigator {
    override fun switch(tab: BottomBarRoute): Boolean {
        val controller = container.navController
        // Catalog is the root itself. Restoring its popped children would reopen a detail screen.
        val restoreTab = tab is JournalTab && tab.tabScreen.isEmpty()
        return runCatching {
                controller.navigate(tab) {
                    launchSingleTop = true
                    restoreState = restoreTab
                    popUpTo<CatalogTab> { saveState = true }
                }
                true
            }
            .getOrElse { throwable ->
                NavigationLogger.e(
                    TAG,
                    "Failed to switch to $tab. Opening bottom bar graph first.",
                    throwable,
                )
                runCatching {
                        controller.navigate(AtlasTabs) {
                            launchSingleTop = true
                            restoreState = restoreTab
                        }
                        if (tab != CatalogTab) {
                            controller.navigate(tab) {
                                launchSingleTop = true
                                restoreState = restoreTab
                                popUpTo<CatalogTab> { saveState = true }
                            }
                        }
                        true
                    }
                    .getOrElse { fallbackThrowable ->
                        NavigationLogger.e(TAG, "Failed to switch to $tab", fallbackThrowable)
                        false
                    }
            }
    }

    private companion object {
        const val TAG = "BottomBarNavigator"
    }
}
