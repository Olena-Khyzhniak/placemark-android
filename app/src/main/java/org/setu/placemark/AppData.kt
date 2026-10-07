package org.setu.placemark

import org.setu.placemark.models.PlacemarkMemStore
import org.setu.placemark.models.PlacemarkStore

// Singleton holding the app-wide Placemark data store
object AppData {
    val placedMarks: PlacemarkStore = PlacemarkMemStore()
}
