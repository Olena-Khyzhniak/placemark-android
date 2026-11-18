package org.setu.placemark

import android.content.Context
import java.io.File

object AppData {

    lateinit var placedMarkList: PlacedMarkList
        private set

    fun initialize(context: Context) {
        val file = File(
            context.filesDir,
            "place_list.json"
        )

        placedMarkList = PlacedMarkList(file)
    }
}
