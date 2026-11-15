package dev.nightatlas.navigation.platform

enum class SystemActionKey {
    OPEN_APP_DETAILS_SETTINGS,
    OPEN_BIOMETRIC_SETTINGS;

    companion object {
        fun parse(value: String?): SystemActionKey? {
            if (value.isNullOrBlank()) return null
            return runCatching { valueOf(value.uppercase()) }.getOrNull()
        }
    }
}
