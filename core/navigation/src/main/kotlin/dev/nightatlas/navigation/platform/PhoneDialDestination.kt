package dev.nightatlas.navigation.platform

object PhoneDialDestination {
    private val PhoneDialRegex = Regex("^[0-9+*#(),.\\- ]{3,32}$")

    fun normalize(destination: String): String? {
        val normalized = destination.trim()
        return normalized.takeIf { PhoneDialRegex.matches(it) }
    }
}
