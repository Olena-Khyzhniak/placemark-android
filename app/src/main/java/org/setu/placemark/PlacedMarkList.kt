package org.setu.placemark

import java.util.concurrent.atomic.AtomicLong

// Manages the in-memory list of PlacedMark objects
class PlacedMarkList {
    val placemarks = ArrayList<PlacedMark>()
    private val lastId = AtomicLong(0L)

    fun findAll(): List<PlacedMark> = placemarks

    fun create(mark: PlacedMark) {
        mark.id = lastId.incrementAndGet()
        placemarks.add(mark)
    }

    fun findOne(id: Long): PlacedMark? =
        placemarks.find { it.id == id }

    fun update(mark: PlacedMark): Boolean {
        val found = findOne(mark.id) ?: return false
        found.title = mark.title
        found.description = mark.description
        found.x = mark.x
        found.y = mark.y
        return true
    }

    fun delete(id: Long): Boolean {
        val found = findOne(id) ?: return false
        placemarks.remove(found)
        return true
    }
}
