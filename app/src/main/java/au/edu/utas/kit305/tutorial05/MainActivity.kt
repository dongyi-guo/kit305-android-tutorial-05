package au.edu.utas.kit305.tutorial05

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import au.edu.utas.kit305.tutorial05.databinding.ActivityMainBinding
import au.edu.utas.kit305.tutorial05.databinding.ActivityMovieDetailsBinding
import au.edu.utas.kit305.tutorial05.databinding.MyListItemBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject

val items: MutableList<Movie> = mutableListOf()
const val FIREBASE_TAG = "FIREBASE"
const val FILTER_TAG = "FILTER"
const val MOVIE_INDEX = "MOVIE_INDEX"



class MainActivity : AppCompatActivity()
{
    private lateinit var ui : ActivityMainBinding
    // This is a ref for the collection, no internet activity has been done.
    val db = Firebase.firestore
    val moviesCollection = db.collection("movies")

    companion object {
        var isFiltered = false
    }

    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    private val getMovieHandler =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        )
        {
            result: ActivityResult ->

            if (result.resultCode == RESPONSE_CLEAR)
            {
                Log.d(FILTER_TAG, "No Filter Applied")
                ui.removeFilterBtn.visibility = View.GONE
                // Retrieve all movies
                moviesCollection
                    .get()
                    .addOnSuccessListener { result ->
                        items.clear()
                        //this line clears the list,
                        // and prevents a bug where items would be duplicated upon rotation of screen
                        Log.d(FIREBASE_TAG, "--- all movies ---")
                        for (document in result)
                        {
                            //Log.d(FIREBASE_TAG, document.toString())
                            val movie = document.toObject<Movie>()
                            movie.id = document.id
                            Log.d(FIREBASE_TAG, movie.toString())

                            items.add(movie)
                        }
                        ui.lblMovieCount.text = "${items.size} Movies"
                        (ui.myList.adapter as MovieAdapter).notifyDataSetChanged()

                        //DON'T USE notifyItemRangeInserted(0, items.size) here! as you are refreshing! Not inserting!
                        //(ui.myList.adapter as MovieAdapter).notifyItemRangeInserted(0, items.size)
                    }
            }
            else if (result.resultCode == RESPONSE_APPLY)
            {
                Log.d(FIREBASE_TAG, "Filter Applied")
                ui.removeFilterBtn.visibility = View.VISIBLE
                val data = result.data
                Log.d(FILTER_TAG, "Data: $data")
                if (null != data){
                    if (isFiltered){
                        moviesCollection
                            .get()
                            .addOnSuccessListener { result ->
                                items.clear()
                                //this line clears the list,
                                // and prevents a bug where items would be duplicated upon rotation of screen
                                Log.d(FIREBASE_TAG, "--- all movies ---")
                                for (document in result) {
                                    //Log.d(FIREBASE_TAG, document.toString())
                                    val movie = document.toObject<Movie>()
                                    movie.id = document.id
                                    Log.d(FIREBASE_TAG, movie.toString())

                                    items.add(movie)
                                }
                            }
                    }
                    val titleFilter = data.getStringExtra(MOVIE_TITLE).orEmpty()
                    val yearFilter = data.getStringExtra(MOVIE_YEAR).orEmpty()
                    val lengthFilter = data.getStringExtra(MOVIE_LENGTH).orEmpty()

                    // Apply the filters
                    isFiltered = true
                    val filteredMovies = items.filter { movie ->
                        (titleFilter.isBlank() || movie.title!!.contains(titleFilter, ignoreCase = true)) &&
                        (yearFilter.isBlank() || movie.year.toString() == yearFilter) &&
                        (lengthFilter.isBlank() || movie.duration.toString() == lengthFilter)
                    }

                    // Update the adapter with filtered movies
                    (ui.myList.adapter as MovieAdapter).apply {
                        items.clear()
                        items.addAll(filteredMovies)
                        ui.lblMovieCount.text = "${items.size} Movies"
                        // (ui.myList.adapter as MovieAdapter).notifyDataSetChanged()
                        //notifyDataSetChanged() is not specific enough. so using:
                        (ui.myList.adapter as MovieAdapter).notifyItemRangeInserted(0, items.size)
                    }
                }
                else
                {
                    Log.e(FILTER_TAG, "Error: No data returned")
                }
            }
            else if (result.resultCode == RESULT_CANCELED)
            {
                Log.d(FILTER_TAG, "User cancelled changing filter")
            }
            else
            {
                Log.e(FILTER_TAG, "Error: ${result.resultCode}")
            }

        }

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        ui = ActivityMainBinding.inflate(layoutInflater)
        ui.myList.adapter = MovieAdapter(movies = items)
        ui.myList.layoutManager = LinearLayoutManager(this)
        setContentView(ui.root)
        ViewCompat.setOnApplyWindowInsetsListener(ui.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        ui.addBtn.setOnClickListener {
            val i = Intent(this, AddMovie::class.java)
            startActivity(i)
        }

        // Reuse MovieDetails for both adding and editing
        ui.addBtnPop.setOnClickListener {
            // Build the dialog using the Movie Details Page's Layout
            // Use AlertDialog.Builder(this)
            val dialogUI = ActivityMovieDetailsBinding.inflate(layoutInflater)
            val dialogBuilder = android.app.AlertDialog.Builder(this)
            dialogBuilder.setView(dialogUI.root)
            dialogBuilder.setTitle("Add Movie")

            val addDialog = dialogBuilder.show()

            dialogUI.btnCancel.setOnClickListener {
                addDialog.dismiss()
            }
            dialogUI.btnSave.setOnClickListener {
                if (dialogUI.txtTitle.text.isNotBlank() &&
                    dialogUI.txtYear.text.isNotBlank() &&
                    dialogUI.txtDuration.text.isNotBlank()) {

                    //get the user input
                    val title = dialogUI.txtTitle.text.toString()
                    val year = dialogUI.txtYear.text.toString().toInt()
                    val duration = dialogUI.txtDuration.text.toString().toFloat()

                    //update the database
                    val movie = Movie(title = title, year = year, duration = duration)
                    moviesCollection.add(movie)
                        .addOnSuccessListener {
                            Log.d(FIREBASE_TAG, "Successfully added movie ${it.id}")
                            movie.id = it.id
                            items.add(movie)
                            ui.lblMovieCount.text = "${items.size} Movies"
                            ui.myList.adapter?.notifyDataSetChanged()
                            addDialog.dismiss()
                        }
                        .addOnFailureListener {
                            Log.d(FIREBASE_TAG, "Failed to add movie", it)
                        }
                    }
            }
        }

        ui.filterBtn.setOnClickListener {
            val i = Intent(this, SearchMovie::class.java)
            getMovieHandler.launch(i)
        }

        ui.removeFilterBtn.setOnClickListener {
            isFiltered = false
            ui.removeFilterBtn.visibility = View.GONE
            // Retrieve all movies
            moviesCollection
                .get()
                .addOnSuccessListener { result ->
                    items.clear()
                    //this line clears the list,
                    // and prevents a bug where items would be duplicated upon rotation of screen
                    Log.d(FIREBASE_TAG, "--- all movies ---")
                    for (document in result)
                    {
                        //Log.d(FIREBASE_TAG, document.toString())
                        val movie = document.toObject<Movie>()
                        movie.id = document.id
                        Log.d(FIREBASE_TAG, movie.toString())

                        items.add(movie)
                    }
                    ui.lblMovieCount.text = "${items.size} Movies"
                    (ui.myList.adapter as MovieAdapter).notifyDataSetChanged()

                    //DON'T USE notifyItemRangeInserted(0, items.size) here! as you are refreshing! Not inserting!
                    //(ui.myList.adapter as MovieAdapter).notifyItemRangeInserted(0, items.size)
                }
        }
        ui.removeFilterBtn.visibility = View.GONE

        val lotr = Movie(
            title = "Lord of the Rings: Fellowship of the Ring",
            year = 2001,
            duration = 103F
        )
        val hpStone = Movie(
            title = "Harry Potter and the Philosopher's Stone",
            year = 2001,
            duration = 152F
        )

        val ff = Movie(
            title = "Fast and Furious",
            year = 2001,
            duration = 106F
        )

        val ff2 = Movie(
            title = "Fast and Furious 2",
            year = 2003,
            duration = 107F
        )

        // Populate Movie Objects
        fun writeData(movie: Movie) {
            moviesCollection
                .add(movie)
                .addOnSuccessListener {
                    Log.d(FIREBASE_TAG, "Document created with id ${it.id}")
                    lotr.id = it.id
                }
                .addOnFailureListener {
                    Log.e(FIREBASE_TAG, "Error writing document", it)
                }
        }

        // Retrieve all movies
        moviesCollection
            .get()
            .addOnSuccessListener { result ->
                items.clear()
                //this line clears the list,
                // and prevents a bug where items would be duplicated upon rotation of screen
                Log.d(FIREBASE_TAG, "--- all movies ---")
                for (document in result)
                {
                    //Log.d(FIREBASE_TAG, document.toString())
                    val movie = document.toObject<Movie>()
                    movie.id = document.id
                    Log.d(FIREBASE_TAG, movie.toString())

                    items.add(movie)
                }
                ui.lblMovieCount.text = "${items.size} Movies"
                // (ui.myList.adapter as MovieAdapter).notifyDataSetChanged()
                //notifyDataSetChanged() is not specific enough. so using:
                (ui.myList.adapter as MovieAdapter).notifyItemRangeInserted(0, items.size)
            }

        // Don't get size here, here happens after .addOnSuccessListener {}

    }

    inner class MovieHolder(var ui: MyListItemBinding) : RecyclerView.ViewHolder(ui.root)

    inner class MovieAdapter(private val movies: MutableList<Movie>) :
        RecyclerView.Adapter<MovieHolder>()
    {
        private val db = Firebase.firestore
        private val moviesCollection = db.collection("movies")
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
                MovieHolder {
            val ui =
                //inflate a new row from the my_list_item.xml
                MyListItemBinding.inflate(layoutInflater, parent, false)
            return MovieHolder(ui) //wrap it in a ViewHolder
        }

        override fun getItemCount(): Int {
            return movies.size
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: MovieHolder, position: Int) {
            val movie = movies[position]   //get the data at the requested position
            holder.ui.txtName.text = movie.title
            holder.ui.txtYear.text = movie.year.toString()

            holder.ui.root.setOnClickListener {
                val currentPosition = holder.adapterPosition
                if (currentPosition != RecyclerView.NO_POSITION){
                    Log.d("CLICKING_VIEW", "Clicked on ${movie.title}")
                    val i = Intent(this@MainActivity, MovieDetails::class.java)
                    i.putExtra(MOVIE_INDEX, currentPosition)
                    startActivity(i)
                }
            }

            holder.ui.removeBtn.setOnClickListener {
                val currentPosition = holder.adapterPosition
                if (currentPosition != RecyclerView.NO_POSITION){
                    moviesCollection.document(movie.id!!).delete()
                        .addOnSuccessListener {
                            Log.d(FIREBASE_TAG, "Successfully deleted movie ${movie.id} at $currentPosition")
                            Log.d(FIREBASE_TAG, "CurrentPosition is $currentPosition and Position is $position")
                            movies.removeAt(currentPosition)
                            ui.lblMovieCount.text = "${movies.size} Movies"
                            (ui.myList.adapter as MovieAdapter).notifyItemRemoved(currentPosition)
                        }
                        .addOnFailureListener {
                            Log.e(FIREBASE_TAG, "Error deleting document", it)
                        }
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    override fun onResume() {
        super.onResume()
        ui.myList.adapter?.notifyDataSetChanged()
        ui.lblMovieCount.text = "${items.size} Movies"
        ui.removeFilterBtn.visibility = if (isFiltered) View.VISIBLE else View.GONE
    }
}

