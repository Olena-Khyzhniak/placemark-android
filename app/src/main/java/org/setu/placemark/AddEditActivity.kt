package org.setu.placemark

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.setu.placemark.models.PlacemarkModel

// Activity for adding a new Placemark or editing an existing one
class AddEditActivity : AppCompatActivity() {

    private lateinit var titleInput: EditText
    private lateinit var descriptionInput: EditText

    private var editingId: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        createUserInterface()

        editingId = intent.getLongExtra("id", -1L)

        if (editingId != -1L) {
            loadExistingMark(editingId!!)
        }
    }

    private fun createUserInterface() {

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 320, 32, 32)
        }

        titleInput = EditText(this).apply {
            hint = "Title"
        }

        descriptionInput = EditText(this).apply {
            hint = "Description"
        }

        val saveButton = Button(this).apply {
            text = "Save"
            setOnClickListener { saveMark() }
        }

        val cancelButton = Button(this).apply {
            text = "Cancel"
            setOnClickListener { finish() }
        }

        root.addView(titleInput)
        root.addView(descriptionInput)
        root.addView(saveButton)
        root.addView(cancelButton)

        setContentView(root)
    }

    // Loads an existing mark into the input fields for editing
    private fun loadExistingMark(id: Long) {

        val mark = AppData.placedMarks.findOne(id)

        if (mark == null) {
            Toast.makeText(this, "Mark not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        titleInput.setText(mark.title)
        descriptionInput.setText(mark.description)
    }

    // Validates inputs and saves a new or updated PlacemarkModel
    private fun saveMark() {

        val title = titleInput.text.toString().trim()
        val description = descriptionInput.text.toString().trim()

        if (title.isEmpty()) {
            titleInput.error = "Title is required"
            return
        }

        if (editingId == null || editingId == -1L) {
            val mark = PlacemarkModel(title = title, description = description)
            AppData.placedMarks.create(mark)
            Toast.makeText(this, "Mark created", Toast.LENGTH_SHORT).show()
        } else {
            val mark = PlacemarkModel(id = editingId!!, title = title, description = description)
            AppData.placedMarks.update(mark)
            Toast.makeText(this, "Mark updated", Toast.LENGTH_SHORT).show()
        }

        finish()
    }
}
