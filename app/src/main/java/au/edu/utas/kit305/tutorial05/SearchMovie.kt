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

        ui.filterCancelBtn.setOnClickListener {
            finish()
        }

        ui.clearAll.setOnClickListener {
            ui.keywordsText.text.clear()
            ui.yearNumber.text.clear()
            ui.lengthNumber.text.clear()
        }

        ui.clearKeywords.setOnClickListener {
            ui.keywordsText.text.clear()
        }

        ui.clearYear.setOnClickListener {
            ui.yearNumber.text.clear()
        }

        ui.clearLength.setOnClickListener {
            ui.lengthNumber.text.clear()
        }

        ui.filterApplyBtn.setOnClickListener {
            MainActivity.isFiltered = true
        }
    }
}