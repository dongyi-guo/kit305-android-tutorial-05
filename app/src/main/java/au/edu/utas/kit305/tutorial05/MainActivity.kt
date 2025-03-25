package au.edu.utas.kit305.tutorial05

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import au.edu.utas.kit305.tutorial05.databinding.ActivityMainBinding
import au.edu.utas.kit305.tutorial05.databinding.MyListItemBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject

val items: MutableList<Movie> = mutableListOf<Movie>()
const val FIREBASE_TAG = "FirebaseLogging"
const val LOADING_TXT = "Loading..."
const val MOVIE_INDEX = "Movie_Index"

class MainActivity : AppCompatActivity()
{
    private lateinit var ui : ActivityMainBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ui = ActivityMainBinding.inflate(layoutInflater)
        setContentView(ui.root)

        ui.lblMovieCount.text = "${items.size} Movies"
        ui.myList.adapter = MovieAdapter(movies = items)

        //vertical list
        ui.myList.layoutManager = LinearLayoutManager(this)

        var db = Firebase.firestore
        Log.d(FIREBASE_TAG, "Firebase Connected: ${db.app.name}")
        val moviesCollection = db.collection("movies")

        val lotr = Movie(
            title = "Lord of the Rings: Fellowship of the Ring",
            year = 2001,
            duration = 9001F
        )
        var hpstone = Movie(
            title = "Harry Potter and the Philosopher's Stone",
            year = 2001,
            duration = 152F * 60
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
        ui.lblMovieCount.text = LOADING_TXT
        moviesCollection
            .get()
            .addOnSuccessListener { result ->
                items.clear() //this line clears the list, and prevents a bug where items would be duplicated upon rotation of screen
                Log.d(FIREBASE_TAG, "--- all movies ---")
                for (document in result)
                {
                    //Log.d(FIREBASE_TAG, document.toString())
                    val movie = document.toObject<Movie>()
                    movie.id = document.id
                    Log.d(FIREBASE_TAG, movie.toString())

                    items.add(movie)
                }
//                Log.d("WTF1",items.size.toString())
                ui.lblMovieCount.text = "${items.size} Movies"
//                (ui.myList.adapter as MovieAdapter).notifyDataSetChanged()
                //you may choose to fix the warning that notifyDataSetChanged() is not specific enough using:
                (ui.myList.adapter as MovieAdapter).notifyItemRangeInserted(0, items.size)
            }
//        Log.d("WTF2",items.size.toString())
//        Don't get size here, here happens after .addOnSuccessListener {}
    }

    inner class MovieHolder(var ui: MyListItemBinding) : RecyclerView.ViewHolder(ui.root) {}

    inner class MovieAdapter(private val movies: MutableList<Movie>) : RecyclerView.Adapter<MovieHolder>()
    {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainActivity.MovieHolder {
            val ui = MyListItemBinding.inflate(layoutInflater, parent, false)   //inflate a new row from the my_list_item.xml
            return MovieHolder(ui)                                                            //wrap it in a ViewHolder
        }

        override fun getItemCount(): Int {
            return movies.size
        }

        override fun onBindViewHolder(holder: MainActivity.MovieHolder, position: Int) {
            val movie = movies[position]   //get the data at the requested position
            holder.ui.txtName.text = movie.title
            holder.ui.txtYear.text = movie.year.toString()

            holder.ui.root.setOnClickListener {
                Log.d("CLICKING_VIEW", "Clicked on ${movie.title}")
                val i = Intent(this@MainActivity, MovieDetails::class.java)
                i.putExtra(MOVIE_INDEX, position)
                startActivity(i)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        ui.myList.adapter?.notifyDataSetChanged() //without a more complicated set-up, we can't be more specific than "dataset changed"
    }
}

