package org.setu.placemark

// Listener interface for RecyclerView item interactions in MarkListActivity.
// Click opens edit screen; long-click deletes the mark.
interface OnMarkListener {

    fun onMarkClick(mark: PlacedMark)

    fun onMarkLongClick(mark: PlacedMark): Boolean
}
