package io.github.voismager

import io.github.voismager.HappyShapesGenerator
import java.io.FileOutputStream

fun main() {
    val g = HappyShapesGenerator()
    val mainComponent = "Walton"
    val keyComponent = "MqTryK1S1ub6SWgASFUc7"
    g.writeAsSvg(mainComponent, keyComponent, FileOutputStream("$mainComponent-$keyComponent.svg"))
}