package fluffy.tigerrr.happyshapes

import java.io.OutputStream
import javax.xml.stream.XMLOutputFactory
import javax.xml.stream.XMLStreamWriter


class HappyShapesGenerator {
    companion object {
        private const val CANVAS_SIZE = 60 // 150
        private const val POSITIONAL_STEP = 12 // 30
        private const val RECT_MIN_SIZE = 12 // 30
        private const val RECT_SIZE_STEP = 4 // 10
        private const val CIRCLE_MIN_SIZE = 6 // 15
        private const val CIRCLE_SIZE_STEP = 2 // 5
        private const val SHAPES = 16
        private const val SHADOW_FILTER_ID = "s"

        private val OUTPUT = XMLOutputFactory.newInstance()

        private val BACKGROUND_COLORS = listOf(
            "EFBC9B", "FBF3D5", "D6DAC8", "9CAFAA",
            "D37676", "EBC49F", "F1EF99", "51829B",
            "F6995C", "AD88C6", "E1AFD1", "FFE6E6",
            "A5DD9B", "F6F193", "C5EBAA", "F2C18D"
        ).map { "#$it" }

        private val SHAPE_COLORS = listOf(
            "D20062", "D6589F", "D895DA", "C4E4FF",
            "D9EDBF", "FF9800", "2C7865", "90D26D",
            "FFF7FC", "8B93FF", "5755FE", "FF71CD",
            "430A5D", "5F374B", "8C6A5D", "EEE4B1"
        ).map { "#$it" }

        private val KEY_COLORS = listOf(
            "49243E", "704264", "BB8493", "DBAFA0",
            "FFC94A", "C08B5C", "795458", "453F78",
            "FFAF45", "FB6D48", "D74B76", "673F69",
            "E72929", "FF5BAE", "FFE4CF", "FFFDD7",
            "FF204E", "A0153E", "5D0E41", "00224D",
            "FDA403", "E8751A", "898121", "E5C287",
            "8E7AB5", "B784B7", "E493B3", "EEA5A6",
            "22092C", "872341", "BE3144", "F05941"
        ).map { "#$it" }
    }

    private fun generateShapes(mainComponent: Long, keyComponent: Long): HappyShapes {
        val mainGenerator = NumberGenerator(mainComponent)
        val keyGenerator = NumberGenerator(keyComponent)

        val backgroundColorIndex = mainGenerator.nextBits(4)
        val key1Color = keyGenerator.nextBits(5)
        val key2Color = keyGenerator.nextBits(5)

        val shapes = generateSequence {
            val typeIndex = mainGenerator.nextBits(2)
            val posIndex = mainGenerator.nextBits(4)
            val jiggleIndex = mainGenerator.nextBits(3)
            val shadowIndex = mainGenerator.nextBits(2)
            val sizeIndex = mainGenerator.nextBits(2)
            val colorIndex = mainGenerator.nextBits(4)
            Shape(typeIndex, posIndex, jiggleIndex, shadowIndex, sizeIndex, colorIndex)
        }.take(SHAPES)

        return HappyShapes(shapes, backgroundColorIndex, key1Color, key2Color)
    }

    fun writeAsSvg(mainComponent: String, keyComponent: String, out: OutputStream) {
        this.writeAsSvg(mainComponent.toSeed(), keyComponent.toSeed(), out)
    }

    fun writeAsSvg(mainComponent: Long, keyComponent: Long, out: OutputStream) {
        val writer = OUTPUT.createXMLStreamWriter(out)

        writer.writeStartElement("svg")
        this.writeHeader(writer)
        this.writeFilters(writer)

        val shapes = generateShapes(mainComponent, keyComponent)

        this.writeBackground(BACKGROUND_COLORS[shapes.backgroundColor], writer)

        for ((i, shape) in shapes.shapes.withIndex()) {
            val jiggleX = when (shape.jiggle) {
                0, 1 -> -1
                2, 3 -> 1
                else -> 0
            }

            val jiggleY = when (shape.jiggle) {
                0, 3 -> -1
                1, 2 -> 1
                else -> 0
            }

            val shadow = shape.shadow == 3

            val x = jiggleX + POSITIONAL_STEP + (shape.position % 4) * POSITIONAL_STEP
            val y = jiggleY + POSITIONAL_STEP + (shape.position / 4) * POSITIONAL_STEP

            val color = when (i) {
                SHAPES - 1 -> KEY_COLORS[shapes.key1Color]
                SHAPES - 2 -> KEY_COLORS[shapes.key2Color]
                else -> SHAPE_COLORS[shape.color]
            }

            if (shape.type % 2 == 0) {
                val size = RECT_MIN_SIZE + shape.size * RECT_SIZE_STEP
                this.writeRect(color, size, size, (x - size / 2), (y - size / 2), shadow, writer)
            } else {
                val size = CIRCLE_MIN_SIZE + shape.size * CIRCLE_SIZE_STEP
                this.writeCircle(color, size, x, y, shadow, writer)
            }
        }

        writer.writeEndElement()

        writer.flush()
        writer.close()
    }

    private fun writeHeader(writer: XMLStreamWriter) {
        writer.writeAttribute("contentScriptType", "text/ecmascript")
        writer.writeAttribute("contentStyleType", "text/css")
        writer.writeAttribute("preserveAspectRatio", "xMidYMid meet")
        writer.writeAttribute("width", CANVAS_SIZE.toString())
        writer.writeAttribute("height", CANVAS_SIZE.toString())
        writer.writeAttribute("zoomAndPan", "magnify")
        writer.writeAttribute("xmlns", "http://www.w3.org/2000/svg")
        writer.writeAttribute("version", "1.0")
    }

    private fun writeFilters(writer: XMLStreamWriter) {
        writer.writeStartElement("defs")

        writer.writeStartElement("filter")
        writer.writeAttribute("id", SHADOW_FILTER_ID)

        writer.writeEmptyElement("feDropShadow")
        writer.writeAttribute("dx", "1")
        writer.writeAttribute("dy", "1")
        writer.writeAttribute("stdDeviation", "0.3")
        writer.writeAttribute("flood-opacity", "0.7")

        writer.writeEndElement()

        writer.writeEndElement()
    }

    private fun writeBackground(color: String, writer: XMLStreamWriter) {
        writer.writeEmptyElement("rect")
        writer.writeAttribute("fill", color)
        writer.writeAttribute("height", CANVAS_SIZE.toString())
        writer.writeAttribute("width", CANVAS_SIZE.toString())
    }

    private fun writeRect(color: String, h: Int, w: Int, x: Int, y: Int, shadow: Boolean, writer: XMLStreamWriter) {
        writer.writeEmptyElement("rect")
        writer.writeAttribute("fill", color)
        writer.writeAttribute("height", h.toString())
        writer.writeAttribute("width", w.toString())
        writer.writeAttribute("x", x.toString())
        writer.writeAttribute("y", y.toString())
        writer.writeAttribute("stroke", "black")

        if (shadow) {
            writer.writeAttribute("filter", "url(#$SHADOW_FILTER_ID)")
        }
    }

    private fun writeCircle(color: String, r: Int, x: Int, y: Int, shadow: Boolean, writer: XMLStreamWriter) {
        writer.writeEmptyElement("circle")
        writer.writeAttribute("fill", color)
        writer.writeAttribute("r", r.toString())
        writer.writeAttribute("cx", x.toString())
        writer.writeAttribute("cy", y.toString())
        writer.writeAttribute("stroke", "black")

        if (shadow) {
            writer.writeAttribute("filter", "url(#$SHADOW_FILTER_ID)")
        }
    }

    private fun String.toSeed(): Long {
        var seed: Long = 7

        for (ch in this) {
            seed = seed * 31 + ch.code
        }

        return seed
    }
}