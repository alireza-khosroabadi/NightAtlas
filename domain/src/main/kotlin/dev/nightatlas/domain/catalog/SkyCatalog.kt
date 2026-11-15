package dev.nightatlas.domain.catalog

interface SkyCatalog {
    val constellations: List<Constellation>

    fun find(id: String): Constellation? = constellations.firstOrNull { it.id == id }
}
