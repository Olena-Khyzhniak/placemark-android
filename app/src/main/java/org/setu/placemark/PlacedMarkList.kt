package org.setu.placemark

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.File
import java.util.concurrent.atomic.AtomicLong
import timber.log.Timber

// Manages the in-memory list of PlacedMark objects, persisted to a JSON file
class PlacedMarkList(private val file: File) {
    val placemarks = ArrayList<PlacedMark>()
    private val lastId = AtomicLong(0L)

    private val gson = GsonBuilder()
        .setPrettyPrinting()
        .create()

    init {
        load()
    }

    // Read placemarks from disk; does nothing if the file does not exist yet
    private fun load() {
        if (!file.exists()) return

        val type = object : TypeToken<ArrayList<PlacedMark>>() {}.type

        val loaded: ArrayList<PlacedMark> =
            gson.fromJson(file.readText(), type) ?: arrayListOf()

        placemarks.clear()
        placemarks.addAll(loaded)

        lastId.set(placemarks.maxOfOrNull { it.id } ?: 0L)
        Timber.d("load: ${placemarks.size} marks loaded from ${file.path}")
    }

    // Write the current list to disk as pretty-printed JSON
    private fun save() {
        file.parentFile?.mkdirs()
        file.writeText(gson.toJson(placemarks))
        Timber.d("save: ${placemarks.size} marks written to ${file.path}")
    }

    fun findAll(): List<PlacedMark> {
        Timber.d("findAll: returning ${placemarks.size} marks")
        return placemarks
    }

    fun create(mark: PlacedMark) {
        mark.validate()
        mark.id = lastId.incrementAndGet()
        placemarks.add(mark)
        save()
        Timber.d("create: mark added id=${mark.id}, title=${mark.title}")
    }

    fun findOne(id: Long): PlacedMark? {
        val mark = placemarks.find { it.id == id }
        if (mark == null) Timber.w("findOne: no mark found for id=$id")
        else Timber.d("findOne: found mark id=${mark.id}, title=${mark.title}")
        return mark
    }

    fun update(mark: PlacedMark): Boolean {
        mark.validate()

        val found = findOne(mark.id) ?: return false
        found.title = mark.title
        found.desc = mark.desc
        found.x = mark.x
        found.y = mark.y
        found.imageUri = mark.imageUri
        save()
        Timber.d("update: mark updated id=${mark.id}, title=${mark.title}")
        return true
    }

    fun delete(id: Long): Boolean {
        val found = findOne(id) ?: return false
        placemarks.remove(found)
        save()
        Timber.d("delete: mark removed id=$id")
        return true
    }
}
