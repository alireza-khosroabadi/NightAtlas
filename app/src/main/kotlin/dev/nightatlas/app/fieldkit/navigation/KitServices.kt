package dev.nightatlas.app.fieldkit.navigation

import dev.nightatlas.navigation.service.serviceContract

internal object KitServices {
    val FieldManual = serviceContract<Unit>("FIELD_MANUAL")
    val EndWalk = serviceContract<Unit>("END_WALK")
    val Tonight = serviceContract<Unit>("TONIGHT")
}
