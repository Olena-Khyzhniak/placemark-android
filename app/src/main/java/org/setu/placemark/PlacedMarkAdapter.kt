package org.setu.placemark

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import timber.log.Timber

// Adapter for the marks list. Item click triggers edit; long-click triggers delete.
class PlacedMarkAdapter(
    private var marks: List<PlacedMark>,
    private val listener: OnMarkListener
) : RecyclerView.Adapter<PlacedMarkAdapter.MarkViewHolder>() {

    class MarkViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val titleText: TextView =
            itemView.findViewById(R.id.titleText)

        val descriptionText: TextView =
            itemView.findViewById(R.id.descriptionText)

        val coordinatesText: TextView =
            itemView.findViewById(R.id.coordinatesText)

        val markImage: ImageView =
            itemView.findViewById(R.id.markImage)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MarkViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.item_placed_mark,
                parent,
                false
            )

        return MarkViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: MarkViewHolder,
        position: Int
    ) {

        val mark = marks[position]

        holder.titleText.text =
            "${mark.id}: ${mark.title}"

        holder.descriptionText.text =
            mark.desc

        holder.coordinatesText.text =
            "X: ${mark.x}, Y: ${mark.y}"

        // Show the saved photo, or a placeholder gallery icon if none was set
        val imageUri = mark.imageUri
        if (imageUri != null) {
            holder.markImage.setImageURI(Uri.parse(imageUri))
        } else {
            holder.markImage.setImageResource(android.R.drawable.ic_menu_gallery)
        }

        // Short click → edit the selected mark
        holder.itemView.setOnClickListener {
            Timber.d("Item clicked: id=${mark.id}")
            listener.onMarkClick(mark)
        }

        // Long click → request deletion of the selected mark
        holder.itemView.setOnLongClickListener {
            Timber.d("Item long-clicked: id=${mark.id}")
            listener.onMarkLongClick(mark)
        }
    }

    override fun getItemCount(): Int {
        return marks.size
    }

    fun updateMarks(newMarks: List<PlacedMark>) {
        marks = newMarks
        notifyDataSetChanged()
    }
}
