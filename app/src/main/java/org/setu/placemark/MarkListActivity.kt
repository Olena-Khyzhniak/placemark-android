package org.setu.placemark

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import timber.log.Timber

class MarkListActivity : AppCompatActivity(), OnMarkListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PlacedMarkAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_mark_list)

        recyclerView = findViewById(R.id.marksRecyclerView)

        adapter = PlacedMarkAdapter(
            marks = AppData.placedMarks.findAll(),
            listener = this
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        val returnButton = findViewById<Button>(R.id.returnButton)

        returnButton.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()

        if (::adapter.isInitialized) {
            adapter.updateMarks(AppData.placedMarks.findAll())
        }
    }

    // Short click → open edit screen for the selected mark
    override fun onMarkClick(mark: PlacedMark) {
        Timber.d("Editing mark id=${mark.id}, title=${mark.title}")
        val intent = Intent(this, AddEditActivity::class.java)
        intent.putExtra("id", mark.id)
        startActivity(intent)
    }

    // Long click → show confirmation dialog before deleting the mark
    override fun onMarkLongClick(mark: PlacedMark): Boolean {
        Timber.d("Delete requested for mark id=${mark.id}, title=${mark.title}")
        AlertDialog.Builder(this)
            .setTitle("Delete this mark?")
            .setMessage("\"${mark.title}\" will be permanently removed.")
            .setPositiveButton("Delete") { _, _ ->
                Timber.d("Delete confirmed for mark id=${mark.id}")
                AppData.placedMarks.delete(mark.id)
                adapter.updateMarks(AppData.placedMarks.findAll())
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                Timber.d("Delete cancelled for mark id=${mark.id}")
                dialog.dismiss()
            }
            .show()
        return true
    }
}
