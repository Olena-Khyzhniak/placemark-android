package org.setu.placemark.models

// Interface defining the contract for all Placemark data stores
interface PlacemarkStore {
    fun findAll(): List<PlacemarkModel>
    fun create(placemark: PlacemarkModel)
    fun update(placemark: PlacemarkModel): Boolean
    fun delete(id: Long): Boolean
    fun findOne(id: Long): PlacemarkModel?
}
