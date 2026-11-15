package dev.nightatlas.feature.atlas.catalog

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

// Schematic line figures, drawn in a fixed coordinate system so rotation preserves proportions.
private data class Figure(val stars: List<Offset>, val strokes: List<List<Int>>)

private val figures =
    mapOf(
        "orion" to
            Figure(
                listOf(
                    Offset(-42f, -55f),
                    Offset(38f, -44f),
                    Offset(15f, 0f),
                    Offset(0f, 5f),
                    Offset(-15f, 11f),
                    Offset(-36f, 61f),
                    Offset(46f, 54f),
                ),
                listOf(listOf(0, 1, 2, 3, 4, 0), listOf(4, 5, 6, 2)),
            ),
        "lyra" to
            Figure(
                listOf(
                    Offset(-48f, -56f),
                    Offset(-12f, -19f),
                    Offset(32f, -9f),
                    Offset(48f, 48f),
                    Offset(4f, 38f),
                ),
                listOf(listOf(0, 1, 2, 3, 4, 1)),
            ),
        "cassiopeia" to
            Figure(
                listOf(
                    Offset(-75f, -21f),
                    Offset(-37f, 24f),
                    Offset(0f, -17f),
                    Offset(34f, 31f),
                    Offset(73f, -32f),
                ),
                listOf(listOf(0, 1, 2, 3, 4)),
            ),
    )

@Composable
internal fun StarChart(id: String, name: String) {
    val figure = figures[id] ?: return
    Canvas(
        Modifier.fillMaxWidth()
            .height(176.dp)
            .background(Color(0xFF182C36), RoundedCornerShape(16.dp))
            .semantics { contentDescription = "Star chart for " + name }
    ) {
        val unit = size.minDimension / 170f
        fun point(index: Int) = center + figure.stars[index] * unit
        drawCircle(Color(0xFF324953), 70f * unit, center, style = Stroke(unit * .45f))
        figure.strokes.forEach { stroke ->
            stroke.zipWithNext().forEach { (a, b) ->
                drawLine(Color(0xFF79A7AA), point(a), point(b), unit * 1.4f)
            }
        }
        figure.stars.indices.forEach { i ->
            val radius = if (i == 0) 4f else 2.8f
            drawCircle(Color(0xFF395351), (radius + 2) * unit, point(i))
            drawCircle(Color(0xFFFFD99C), radius * unit, point(i))
        }
    }
}
