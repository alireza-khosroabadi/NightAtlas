package dev.nightatlas.navigation.deeplink

import android.net.Uri
import dev.nightatlas.navigation.logging.NavigationLogger
import dev.nightatlas.navigation.model.ActionEntity

interface DeepLinkActionMapper {
    fun map(uri: Uri): ActionEntity?

    fun decode(uri: Uri): ActionEntity.Link {
        val mapped = try {
            map(uri)
        } catch (e: RuntimeException) {
            NavigationLogger.e("DeepLinkActionMapper", "Failed to map deep link", e)
            null
        }
        return ActionEntity.Link(uri, mapped)
    }
}
