package org.setu.placemark.models

import org.junit.Assert.assertEquals
import org.junit.AssertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PlacemarkMemStoreTest {
    @Test
    fun createUpdateAndDeletePlacemark() {
        val store = PlacemarkMemStore()
        val placemark = PlacedMark(title = "Harbour", desc = "South pier")

        store.create(placemark)

        assertEquals(1L, placemark.id)
        assertEquals(placemark, store.findOne(placemark.id))

        val updatedPlacemark = placemark.copy(title = "Updated harbour", desc = "North pier")
        assertTrue(store.update(updatedPlacemark))
        assertEquals(updatedPlacemark, store.findOne(placemark.id))

        assertTrue(store.delete(placemark.id))
        assertNull(store.findOne(placemark.id))
    }
}