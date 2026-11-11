package org.setu.placemark

import android.content.Intent
import android.net.Uri
import androidx.core.net.toUri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import timber.log.Timber

class AddEditActivity : AppCompatActivity() {

    private lateinit var titleInput: EditText
    private lateinit var descriptionInput: EditText
    private lateinit var xInput: EditText
    private lateinit var yInput: EditText

    var imagePreview: ImageView? = null
    var selectedImageUri: Uri? = null

    private var editingId: Long? = null
    private var selectedLatitude: Double? = null
    private var selectedLongitude: Double? = null

    // Must be registered at class level, before onCreate, so the callback survives rotation/return
    val mapPicker = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data ?: return@registerForActivityResult
            selectedLatitude = data.getDoubleExtra("latitude", 0.0)
            selectedLongitude = data.getDoubleExtra("longitude", 0.0)

            selectedLatitude?.let { xInput.setText(it.toString()) }
            selectedLongitude?.let { yInput.setText(it.toString()) }
        }
    }

    val imagePicker =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
            if (uri != null) {
                // Keep access to the document URI for later use, when supported.
                try {
                    contentResolver.takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                } catch (_: SecurityException) {
                    // Some providers may not grant persistable access.
                }

                selectedImageUri = uri
                imagePreview!!.setImageURI(uri)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_add_edit)

        titleInput = findViewById(R.id.titleInput)
        descriptionInput = findViewById(R.id.descriptionInput)
        xInput = findViewById(R.id.xInput)
        yInput = findViewById(R.id.yInput)

        imagePreview = findViewById(R.id.imageViewAdd)
        val saveButton = findViewById<Button>(R.id.saveButton)
        val cancelButton = findViewById<Button>(R.id.cancelButton)

        editingId = intent.getLongExtra("id", -1L)
            .takeIf { it != -1L }

        if (editingId != null) {
            Timber.d("Opening AddEditActivity to edit mark id=$editingId")
            findViewById<TextView>(R.id.formTitle).text = getString(R.string.label_edit_mark)
            loadExistingMark(editingId!!)
        } else {
            Timber.d("Opening AddEditActivity to create a new mark")
        }

        saveButton.setOnClickListener { saveMark() }
        cancelButton.setOnClickListener { finish() }

        findViewById<Button>(R.id.imagePickButton).setOnClickListener {
            imagePicker.launch(arrayOf("image/*"))
        }

        findViewById<Button>(R.id.buttonLoc).setOnClickListener {
            val intent = Intent(this, MapPickerActivity::class.java)
            mapPicker.launch(intent)
        }
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
        descriptionInput.setText(mark.desc)
        xInput.setText(mark.x.toString())
        yInput.setText(mark.y.toString())
        // Restore the saved image, or show a placeholder when none was set
        selectedImageUri = mark.imageUri?.toUri()
        if (selectedImageUri != null) {
            imagePreview?.setImageURI(selectedImageUri)
        } else {
            imagePreview?.setImageResource(android.R.drawable.ic_menu_gallery)
        }
    }

    private fun saveMark() {

        val title = titleInput.text.toString().trim()
        val desc = descriptionInput.text.toString().trim()

        val x = xInput.text.toString().toDoubleOrNull() ?: 0.0
        val y = yInput.text.toString().toDoubleOrNull() ?: 0.0

        val mark = if (editingId == null) {
            PlacedMark(
                title = title,
                desc = desc,
                x = x,
                y = y,
                imageUri = selectedImageUri?.toString()
            )
        } else {
            PlacedMark(
                id = editingId!!,
                title = title,
                desc = desc,
                x = x,
                y = y,
                imageUri = selectedImageUri?.toString()
            )
        }

        // Validate before saving — throws IllegalArgumentException with a user-friendly message
        try {
            mark.validate()
        } catch (e: IllegalArgumentException) {
            Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
            return
        }

        if (editingId == null) {
            AppData.placedMarks.create(mark)
            Timber.d("Mark created with title=$title")
            Toast.makeText(this, "Mark created", Toast.LENGTH_SHORT).show()
        } else {
            AppData.placedMarks.update(mark)
            Timber.d("Mark updated id=$editingId, title=$title")
            Toast.makeText(this, "Mark updated", Toast.LENGTH_SHORT).show()
        }

        finish()
    }
}
