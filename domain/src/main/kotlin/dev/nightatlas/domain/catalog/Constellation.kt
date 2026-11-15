package dev.nightatlas.domain.catalog

data class Constellation(
    val id: String,
    val name: String,
    val abbreviation: String,
    val description: String,
    val fieldNote: String,
)
