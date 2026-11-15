package dev.nightatlas.navigation.tabs

import dev.nightatlas.navigation.model.BottomBarRoute

interface BottomBarNavigator {

    fun switch(tab: BottomBarRoute): Boolean
}
