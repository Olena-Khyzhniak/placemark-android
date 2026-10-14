package org.setu.placemark

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import timber.log.Timber

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialise Timber once for the whole app — debug builds only
        Timber.plant(Timber.DebugTree())

        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        Timber.d("MainActivity started")

        val viewMarksButton = findViewById<Button>(R.id.viewMarksButton)
        val addMarkButton = findViewById<Button>(R.id.addMarkButton)

        viewMarksButton.setOnClickListener {
            Timber.d("Navigating to MarkListActivity")
            startActivity(Intent(this, MarkListActivity::class.java))
        }

        addMarkButton.setOnClickListener {
            Timber.d("Navigating to AddEditActivity to create a new mark")
            startActivity(Intent(this, AddEditActivity::class.java))
        }
    }
}
