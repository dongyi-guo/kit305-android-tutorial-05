package au.edu.utas.kit305.tutorial05

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import au.edu.utas.kit305.tutorial05.databinding.ActivityMovieDetailsBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class MovieDetails : AppCompatActivity() {
    private lateinit var ui : ActivityMovieDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ui = ActivityMovieDetailsBinding.inflate(layoutInflater)
        setContentView(ui.root)

        //Read in movie details and display on this screen
        //get movie object using id from intent
        val movieID = intent.getIntExtra(MOVIE_INDEX, -1)
        val movieObject = items[movieID]
        val title = movieObject.title
        val year = movieObject.year
        val duration = movieObject.duration
        // Set txtTitle, txtYear, txtDuration
        ui.txtTitle.setText(title)
        ui.txtYear.setText(year.toString())
        ui.txtDuration.setText(duration.toString())

        // Post changes to the database
        val db = Firebase.firestore
        val moviesCollection = db.collection("movies")

        ui.btnCancel.setOnClickListener {
            finish()
        }

        ui.btnSave.setOnClickListener {
            //get the user input
            movieObject.title = ui.txtTitle.text.toString()
            movieObject.year = ui.txtYear.text.toString().toInt() //good code would check this is really an int
            movieObject.duration = ui.txtDuration.text.toString().toFloat() //good code would check this is really a float

            //update the database
            moviesCollection.document(movieObject.id!!)
                .set(movieObject)
                .addOnSuccessListener {
                    Log.d(FIREBASE_TAG, "Successfully updated movie ${movieObject.id}")
                    //return to the list
                    finish()
                }
                .addOnFailureListener(
                    { Log.d(FIREBASE_TAG, "Failed to update movie", it) }
                )
        }
    }
}