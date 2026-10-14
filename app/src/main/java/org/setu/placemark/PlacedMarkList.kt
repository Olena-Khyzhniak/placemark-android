package org.setu.placemark

import java.util.concurrent.atomic.AtomicLong
import timber.log.Timber

// Manages the in-memory list of PlacedMark objects
class PlacedMarkList {
    val placemarks = ArrayList<PlacedMark>()
    private val lastId = AtomicLong(0L)

    fun findAll(): List<PlacedMark> {
        Timber.d("findAll: returning ${placemarks.size} marks")
        return placemarks
    }

    fun create(mark: PlacedMark) {
        mark.id = lastId.incrementAndGet()
        placemarks.add(mark)
        Timber.d("create: mark added id=${mark.id}, title=${mark.title}")
    }

    fun findOne(id: Long): PlacedMark? {
        val mark = placemarks.find { it.id == id }
        if (mark == null) Timber.w("findOne: no mark found for id=$id")
        else Timber.d("findOne: found mark id=${mark.id}, title=${mark.title}")
        return mark
    }

    fun update(mark: PlacedMark): Boolean {
        val found = findOne(mark.id) ?: return false
        found.title = mark.title
        found.description = mark.description
        found.x = mark.x
        found.y = mark.y
        Timber.d("update: mark updated id=${mark.id}, title=${mark.title}")
        return true
    }

    fun delete(id: Long): Boolean {
        val found = findOne(id) ?: return false
        placemarks.remove(found)
        Timber.d("delete: mark removed id=$id")
        return true
    }
}
