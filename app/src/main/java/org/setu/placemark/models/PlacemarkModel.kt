package org.setu.placemark.models

/**
 * Data class representing a single Placemark item.
 * Kotlin automatically generates toString(), equals(), hashCode(), and copy().
 */
data class PlacedMark(
    var id:  Long = 0L,
    var title: String = "",
    var desc: String = "",
    var x: Double = 0.0,
    var y: Double = 0.0
)
