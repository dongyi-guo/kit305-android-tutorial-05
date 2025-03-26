package au.edu.utas.kit305.tutorial05

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import au.edu.utas.kit305.tutorial05.databinding.ActivityAddMovieBinding
import au.edu.utas.kit305.tutorial05.databinding.ActivitySearchMovieBinding

class SearchMovie : AppCompatActivity() {

    private lateinit var ui : ActivitySearchMovieBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        ui = ActivitySearchMovieBinding.inflate(layoutInflater)
        setContentView(ui.root)
        ViewCompat.setOnApplyWindowInsetsListener(ui.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        ui.cancelFilterBtn.setOnClickListener {
            finish()
        }

        ui.clearAllBtn.setOnClickListener {
            ui.keywordsText.text.clear()
            ui.yearText.text.clear()
            ui.lengthText.text.clear()
        }

        ui.clearKeywords.setOnClickListener {
            ui.keywordsText.text.clear()
        }

        ui.clearYear.setOnClickListener {
            ui.yearText.text.clear()
        }

        ui.clearLength.setOnClickListener {
            ui.lengthText.text.clear()
        }
    }
}