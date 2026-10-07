package org.setu.placemark

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

// Main screen: lists all Placemarks and provides Add/Edit/Delete actions
class MainActivity : AppCompatActivity() {

    private lateinit var listLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createUserInterface()
    }

    // Refresh the list every time the user returns to this screen
    override fun onResume() {
        super.onResume()
        if (::listLayout.isInitialized) {
            displayMarks()
        }
    }

    private fun createUserInterface() {

        // Outer wrapper that fills the screen
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 32, 32, 32)
        }

        // Apply system window insets so content sits below the status bar
        ViewCompat.setOnApplyWindowInsetsListener(root) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(32, bars.top + 16, 32, bars.bottom + 16)
            insets
        }

        val title = TextView(this).apply {
            text = "Placed Marks"
            textSize = 28f
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 24)
        }

        val addButton = Button(this).apply {
            text = "Add Mark"
            setOnClickListener {
                startActivity(Intent(this@MainActivity, AddEditActivity::class.java))
            }
        }

        listLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        // Wrap the list in a ScrollView so it scrolls when there are many marks
        val scrollView = ScrollView(this)
        scrollView.addView(listLayout)

        root.addView(title, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ))
        root.addView(addButton, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ))
        root.addView(scrollView, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        ))

        setContentView(root)
        displayMarks()
    }

    // Rebuilds the list view from the current store contents
    private fun displayMarks() {

        listLayout.removeAllViews()

        val marks = AppData.placedMarks.findAll()

        if (marks.isEmpty()) {
            val emptyText = TextView(this).apply {
                text = "No placed marks yet."
                textSize = 18f
                setPadding(0, 40, 0, 40)
            }
            listLayout.addView(emptyText)
            return
        }

        for (mark in marks) {

            // Card-like container for each mark entry
            val markLayout = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(0, 24, 0, 24)
            }

            val markTitle = TextView(this).apply {
                text = "${mark.id}: ${mark.title}"
                textSize = 20f
            }

            val markDescription = TextView(this).apply {
                text = mark.description
                textSize = 16f
            }

            // Row holding Edit and Delete side by side
            val buttonRow = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                setPadding(0, 8, 0, 0)
            }

            val editButton = Button(this).apply {
                text = "Edit"
                setOnClickListener {
                    val intent = Intent(this@MainActivity, AddEditActivity::class.java)
                    intent.putExtra("id", mark.id)
                    startActivity(intent)
                }
            }

            val deleteButton = Button(this).apply {
                text = "Delete"
                setOnClickListener {
                    AppData.placedMarks.delete(mark.id)
                    displayMarks()
                }
            }

            buttonRow.addView(editButton)
            buttonRow.addView(deleteButton)

            markLayout.addView(markTitle)
            markLayout.addView(markDescription)
            markLayout.addView(buttonRow)

            listLayout.addView(markLayout)
        }
    }
}
