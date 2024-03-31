package fluffy.tigerrr.happyshapes

data class HappyShapes (
    val shapes: Sequence<Shape>,
    val backgroundColor: Int,
    val key1Color: Int,
    val key2Color: Int
)

data class Shape(
    val type: Int,
    val position: Int,
    val jiggle: Int,
    val shadow: Int,
    val size: Int,
    val color: Int
)
