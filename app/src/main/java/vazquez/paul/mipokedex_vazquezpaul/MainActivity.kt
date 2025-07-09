package vazquez.paul.mipokedex_vazquezpaul

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.cloudinary.Url
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.FirebaseApp
import vazquez.paul.mipokedex_vazquezpaul.databinding.ActivityMainBinding
import java.net.URL

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val pokemones = FirebaseDatabase.getInstance().getReference("pokemones/")
    private val lista = ArrayList<Pokemon>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = PokemonAdapter(this, lista)
        binding.pokemones.adapter = adapter

        binding.addButton.setOnClickListener {
            val intent: Intent = Intent(this, AddPokemonActivity::class.java)
            startActivity(intent)
        }

        pokemones.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val pokemon = snapshot.getValue(Pokemon::class.java)
                if (pokemon != null) {
                    lista.add(pokemon)
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    inner class PokemonAdapter(val context: Context, val pokemones: ArrayList<Pokemon>): BaseAdapter() {
        override fun getCount(): Int {
            return pokemones.size
        }

        override fun getItem(position: Int): Any {
            return pokemones[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val registroPokemon = LayoutInflater.from(context).inflate(R.layout.pokemon, null)
            val tvNombre = registroPokemon.findViewById<TextView>(R.id.tvNombrePokemon)
            val tvNumero = registroPokemon.findViewById<TextView>(R.id.tvNumeroPokemon)
            val ivPokemon = registroPokemon.findViewById<ImageView>(R.id.ivPokemon)

            val pokemon = pokemones[position]
            tvNombre.text = pokemon.nombre
            tvNumero.text = "#${pokemon.numero}"

            Glide.with(registroPokemon)
                .load(pokemon.url)
                .centerInside()
                .fitCenter()
                .into(ivPokemon)

            return registroPokemon
        }
    }

}