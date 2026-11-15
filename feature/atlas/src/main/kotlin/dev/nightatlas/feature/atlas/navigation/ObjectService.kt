package dev.nightatlas.feature.atlas.navigation

import dev.nightatlas.navigation.model.CatalogTab
import dev.nightatlas.navigation.model.NavigationRoute
import dev.nightatlas.navigation.service.ObjectInput

internal object ObjectService {
    fun decode(params: Map<String, String>): ObjectInput {
        val path = params["path"].orEmpty().trim('/').split('/')
        val id = params["id"] ?: path.firstOrNull().orEmpty()
        if (id.isBlank()) return ObjectInput.Catalog
        val confirm =
            when (params["step"]?.toIntOrNull()) {
                1 -> false
                2 -> true
                else -> path.getOrNull(1).equals("record", ignoreCase = true)
            }
        return if (confirm) ObjectInput.Record(id) else ObjectInput.Detail(id, params["title"])
    }

    fun resolveRoute(input: ObjectInput): NavigationRoute = when (input) {
        ObjectInput.Catalog -> CatalogTab
        is ObjectInput.Detail -> ObjectDetail(input.id, input.title)
        is ObjectInput.Record -> RecordObservation(FieldSlip(input.id).encode())
    }
}
