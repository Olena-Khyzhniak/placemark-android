package org.setu.placemark


data class PlacedMark(
    var id: Long = 0,
    var title: String = "",
    var description: String = "",
    var x: Double = 0.0,
    var y: Double = 0.0
)
