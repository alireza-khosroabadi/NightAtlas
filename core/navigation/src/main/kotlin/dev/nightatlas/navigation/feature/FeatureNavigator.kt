package dev.nightatlas.navigation.feature

import dev.nightatlas.navigation.model.ActionEntity

interface FeatureNavigator<I, out R> {
    fun handleAction(action: ActionEntity): R

    fun openFromService(input: I): R
}
