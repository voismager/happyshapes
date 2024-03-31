package happyshapes

import fluffy.tigerrr.happyshapes.HappyShapesGenerator
import java.io.FileOutputStream

fun main() {
    val g = HappyShapesGenerator()
    g.writeAsSvg("Liza", "Liza12", FileOutputStream("out.svg"))
}