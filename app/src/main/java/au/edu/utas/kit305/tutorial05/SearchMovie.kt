package au.edu.utas.kit305.tutorial05

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import au.edu.utas.kit305.tutorial05.databinding.ActivitySearchMovieBinding


const val RESPONSE_CLEAR = 0
const val RESPONSE_APPLY = 1
const val MOVIE_TITLE = "movie_title"
const val MOVIE_YEAR = "movie_year"
const val MOVIE_LENGTH = "movie_length"

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
            ui.keywordsText.text.clear()
            ui.yearNumber.text.clear()
            ui.lengthNumber.text.clear()
            MainActivity.isFiltered = false
            setResult(RESPONSE_CLEAR)
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
            if (ui.keywordsText.text.toString().isBlank() &&
                ui.yearNumber.text.toString().isBlank() &&
                ui.lengthNumber.text.toString().isBlank()) {
                MainActivity.isFiltered = false
                setResult(RESPONSE_CLEAR)
            } else {
                val intent = Intent().apply{
                    putExtra(MOVIE_TITLE, ui.keywordsText.text.toString())
                    putExtra(MOVIE_YEAR, ui.yearNumber.text.toString())
                    putExtra(MOVIE_LENGTH, ui.lengthNumber.text.toString())
                }
                MainActivity.isFiltered = true
                setResult(RESPONSE_APPLY, intent)
            }
            finish()
        }
    }
}