package dev.nightatlas.data.catalog

import dev.nightatlas.domain.catalog.Constellation
import dev.nightatlas.domain.catalog.SkyCatalog

object PocketCatalog : SkyCatalog {
    override val constellations =
        listOf(
            Constellation(
                "orion",
                "Orion",
                "Ori",
                "Look for the three stars in a row that form Orion’s Belt. Bright stars above and below the belt outline the hunter.",
                "Compare the orange glow of Betelgeuse with the blue-white light of Rigel at Orion’s foot.",
            ),
            Constellation(
                "lyra",
                "Lyra",
                "Lyr",
                "Start with Vega, the brightest star in Lyra. Four fainter stars nearby form a small parallelogram.",
                "Give your eyes a few minutes to adjust, then look for the fainter stars near Vega.",
            ),
            Constellation(
                "cassiopeia",
                "Cassiopeia",
                "Cas",
                "Cassiopeia’s five main stars form a zigzag. Depending on when you look, it can resemble a W or an M.",
                "Find both ends of the zigzag, then trace the five stars from one end to the other.",
            ),
        )
}
