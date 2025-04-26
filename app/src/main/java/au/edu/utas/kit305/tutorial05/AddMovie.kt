package au.edu.utas.kit305.tutorial05

import android.os.Bundle
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import au.edu.utas.kit305.tutorial05.databinding.ActivityAddMovieBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class AddMovie : AppCompatActivity() {

    private lateinit var ui : ActivityAddMovieBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        ui = ActivityAddMovieBinding.inflate(layoutInflater)
        setContentView(ui.root)
        ViewCompat.setOnApplyWindowInsetsListener(ui.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })

        ui.cancelMovieBtn.setOnClickListener {
            finish()
        }

        // Add a movie to the database
        ui.addMovieBtn.setOnClickListener {
            //get the user input
            val title = ui.movieTitle.text.toString()
            val year = ui.movieYear.text.toString().toInt()
            val duration = ui.movieLength.text.toString().toFloat()

            //update the database
            val movie = Movie(title = title, year = year, duration = duration)
            val db = Firebase.firestore
            val moviesCollection = db.collection("movies")
            moviesCollection.add(movie)
                .addOnSuccessListener {
                    Log.d(FIREBASE_TAG, "Successfully added movie ${it.id}")
                    movie.id = it.id
                    items.add(movie)
                    //return to the list
                    finish()
                }
                .addOnFailureListener {
                    Log.d(FIREBASE_TAG, "Failed to add movie", it)
                }
        }
    }
}