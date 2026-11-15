package dev.nightatlas.navigation.intent

import android.content.Intent
import dev.nightatlas.navigation.logging.NavigationLogger
import dev.nightatlas.navigation.model.ActionDto
import dev.nightatlas.navigation.model.ActionEntity
import dev.nightatlas.navigation.model.DestinationType
import dev.nightatlas.navigation.registry.NavigationRegistry
import kotlinx.serialization.json.Json

fun Intent.putNavigationAction(action: ActionDto): Intent = apply {
    putExtra(EXTRA_NAVIGATION_ACTION, json.encodeToString(action.copy(title = action.title.orEmpty())))
}

fun Intent.consumeNavigationAction(registry: NavigationRegistry): ActionEntity? =
    try {
        getStringExtra(EXTRA_NAVIGATION_ACTION)?.let {
            val dto = json.decodeFromString<ActionDto>(it)
            // An invalid action extra leaves the intent's data URI eligible for delivery.
            dto.takeIf { it.title != null && it.destination != null && DestinationType.parse(it.destinationType) != null }
                ?.toDomainModel(registry)
        }
    } catch (exception: RuntimeException) {
        NavigationLogger.e(TAG, "Unable to read navigation action from intent", exception)
        null
    } finally {
        removeExtra(EXTRA_NAVIGATION_ACTION)
    }

private val json = Json { ignoreUnknownKeys = true }

private const val EXTRA_NAVIGATION_ACTION = "dev.nightatlas.navigation.extra.ACTION_ENTITY"
private const val TAG = "NavigationIntent"
