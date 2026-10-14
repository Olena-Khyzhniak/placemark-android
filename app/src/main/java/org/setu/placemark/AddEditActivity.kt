package org.setu.placemark

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import timber.log.Timber

class AddEditActivity : AppCompatActivity() {

    private lateinit var titleInput: EditText
    private lateinit var descriptionInput: EditText
    private lateinit var xInput: EditText
    private lateinit var yInput: EditText

    private var editingId: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_add_edit)

        titleInput = findViewById(R.id.titleInput)
        descriptionInput = findViewById(R.id.descriptionInput)
        xInput = findViewById(R.id.xInput)
        yInput = findViewById(R.id.yInput)

        val saveButton = findViewById<Button>(R.id.saveButton)
        val cancelButton = findViewById<Button>(R.id.cancelButton)

        editingId = intent.getLongExtra("id", -1L)
            .takeIf { it != -1L }

        if (editingId != null) {
            Timber.d("Opening AddEditActivity to edit mark id=$editingId")
            findViewById<TextView>(R.id.formTitle).text = "Edit Mark"
            loadExistingMark(editingId!!)
        } else {
            Timber.d("Opening AddEditActivity to create a new mark")
        }

        saveButton.setOnClickListener { saveMark() }
        cancelButton.setOnClickListener { finish() }
    }

    private fun loadExistingMark(id: Long) {

        val mark = AppData.placedMarks.findOne(id)

        if (mark == null) {
            Timber.w("Mark not found for id=$id")
            Toast.makeText(this, "Mark not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        Timber.d("Loaded mark id=${mark.id}, title=${mark.title}")

        titleInput.setText(mark.title)
        descriptionInput.setText(mark.description)
        xInput.setText(mark.x.toString())
        yInput.setText(mark.y.toString())
    }

    private fun saveMark() {

        val title = titleInput.text.toString().trim()
        val description = descriptionInput.text.toString().trim()

        if (title.isEmpty()) {
            titleInput.error = "Title is required"
            return
        }

        val x = xInput.text.toString().toDoubleOrNull()
        if (x == null) {
            xInput.error = "Enter a valid number"
            return
        }

        val y = yInput.text.toString().toDoubleOrNull()
        if (y == null) {
            yInput.error = "Enter a valid number"
            return
        }

        if (editingId == null) {
            val mark = PlacedMark(
                title = title,
                description = description,
                x = x,
                y = y
            )
            AppData.placedMarks.create(mark)
            Timber.d("Mark created with title=$title")
            Toast.makeText(this, "Mark created", Toast.LENGTH_SHORT).show()
        } else {
            val mark = PlacedMark(
                id = editingId!!,
                title = title,
                description = description,
                x = x,
                y = y
            )
            AppData.placedMarks.update(mark)
            Timber.d("Mark updated id=$editingId, title=$title")
            Toast.makeText(this, "Mark updated", Toast.LENGTH_SHORT).show()
        }

        finish()
    }
}
