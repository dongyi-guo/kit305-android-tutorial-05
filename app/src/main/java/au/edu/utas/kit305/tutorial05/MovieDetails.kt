package au.edu.utas.kit305.tutorial05

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.OnBackPressedCallback
import au.edu.utas.kit305.tutorial05.databinding.ActivityMovieDetailsBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class MovieDetails : AppCompatActivity() {
    private lateinit var ui : ActivityMovieDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ui = ActivityMovieDetailsBinding.inflate(layoutInflater)
        setContentView(ui.root)

        val db = Firebase.firestore
        val moviesCollection = db.collection("movies")
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })
        ui.btnCancel.setOnClickListener {
            finish()
        }

        //Read in movie details and display on this screen
        //get movie object using id from intent

        // So if we get movie ID, it will be the moviveID variable
        // But if not, it is -1
        // So you can use movieID's value to tell:
        // Do you want to edit a movie? Or add a new one?
        val movieID = intent.getIntExtra(MOVIE_INDEX, -1)
        // Reuse MovieDetails for both adding and editing

        if (-1 != movieID) {
            val movieObject = items[movieID]
            val title = movieObject.title
            val year = movieObject.year
            val duration = movieObject.duration
            // Set txtTitle, txtYear, txtDuration
            ui.txtTitle.setText(title)
            // Don't use setText() to catch integer,
            // because it will try to find pre-defined string variable's location!
            ui.txtYear.setText(year.toString())
            ui.txtDuration.setText(duration.toString())

            ui.btnSave.setOnClickListener {
                //get the user input
                movieObject.title = ui.txtTitle.text.toString()
                movieObject.year = ui.txtYear.text.toString().toInt() //good code would check this is really an int
                movieObject.duration = ui.txtDuration.text.toString().toFloat() //good code would check this is really a float

                //update the database
                moviesCollection.document(movieObject.id!!)
                    // If no ...merge(), your database will be overwritten with the new values
                    .set(movieObject, com.google.firebase.firestore.SetOptions.merge())
                    .addOnSuccessListener {
                        Log.d(FIREBASE_TAG, "Successfully updated movie ${movieObject.id}")
                        //return to the list
                        finish()
                    }
                    .addOnFailureListener {
                        Log.d(FIREBASE_TAG, "Failed to update movie", it)
                    }
            }
        }
        else
        {
            // This is a new movie, so we need to add it to the database
            ui.btnSave.setOnClickListener {
                //get the user input
                val title = ui.txtTitle.text.toString()
                val year = ui.txtYear.text.toString().toInt()
                val duration = ui.txtDuration.text.toString().toFloat()

                //update the database
                val movie = Movie(title = title, year = year, duration = duration)
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
}