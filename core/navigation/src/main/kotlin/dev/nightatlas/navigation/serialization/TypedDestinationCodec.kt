package dev.nightatlas.navigation.serialization

import java.net.URLDecoder
import java.net.URLEncoder
import kotlinx.serialization.json.Json

inline fun <reified T : Any> T.asTypedDestination(navType: String = T::class.qualifiedName.orEmpty()): String {
    val wrapper = TypedDestination(navType = navType, data = this)
    return URLEncoder.encode(Json.encodeToString(wrapper), "UTF-8")
}

inline fun <reified T : Any> String.decodeTypedDestination(): T {
    val json = URLDecoder.decode(this, "UTF-8")
    val wrapper = Json.decodeFromString<TypedDestination<T>>(json)
    return wrapper.data
}
