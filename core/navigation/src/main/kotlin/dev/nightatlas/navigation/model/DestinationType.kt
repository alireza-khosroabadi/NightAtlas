package dev.nightatlas.navigation.model

import android.util.Log

enum class DestinationType {
    WEB_VIEW,
    EXTERNAL_WEB,
    DEEP_LINK,
    TEL_DEEP_LINK,
    TAB_BAR,
    SERVICE,
    SYSTEM_ACTION,
    BOTTOM_BAR;

    companion object {
        fun parse(destination: String?): DestinationType? {
            if (destination.isNullOrBlank()) return null
            return runCatching { DestinationType.valueOf(destination.trim().uppercase()) }
                .getOrNull()
        }

        fun getDestinationType(destination: String?): DestinationType {
            return parse(destination)
                ?: run {
                    Log.e(
                        DestinationType::class.simpleName,
                        "Enum NOT FOUND For $destination. Falling back to WEB_VIEW for legacy behavior.",
                    )
                    WEB_VIEW
                }
        }
    }
}
