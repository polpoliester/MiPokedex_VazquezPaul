package vazquez.paul.mipokedex_vazquezpaul

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.google.firebase.database.FirebaseDatabase
import vazquez.paul.mipokedex_vazquezpaul.databinding.ActivityAddPokemonBinding

class AddPokemonActivity : AppCompatActivity() {
    private val pokemones = FirebaseDatabase.getInstance().getReference("pokemones/")
    private lateinit var binding: ActivityAddPokemonBinding
    private val REQUEST_IMAGE_GET = 1
    private val CLOUD_NAME = "dluszraa2"
    private val UPLOAD_PRESET = "pokemon-upload"
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAddPokemonBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initCloudinary()

        binding.uploadButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_IMAGE_GET)
        }

        binding.saveButton.setOnClickListener {
            savePokemon()
        }
    }

    private fun initCloudinary() {
        val config: MutableMap<String, String> = HashMap()
        config["cloud_name"] = CLOUD_NAME
        MediaManager.init(this, config)
    }

    @Deprecated("Usa registerForActivityResult en lugar de onActivityResult", ReplaceWith("ActivityResultContracts"))
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_GET && resultCode == Activity.RESULT_OK) {
            val fullPhotoUrl: Uri? = data?.data
            if (fullPhotoUrl != null) {
                changeImage(fullPhotoUrl)
                imageUri = fullPhotoUrl
            }
        }
    }

    private fun changeImage(uri: Uri) {
        try {
            binding.pokemonImage.setImageURI(uri)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun savePokemon() {
        if (imageUri != null) {
            MediaManager.get().upload(imageUri)
                .unsigned(UPLOAD_PRESET)
                .callback(object : UploadCallback {
                    override fun onStart(requestId: String?) {}

                    override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {}

                    override fun onSuccess(requestId: String?, resultData: MutableMap<Any?, Any?>?) {
                        val url = resultData?.get("secure_url") as String? ?: ""
                        val numero = binding.etNumero.text.toString()
                        val nombre = binding.etNombre.text.toString()

                        val pokemon = Pokemon(numero, nombre, url)
                        pokemones.push().setValue(pokemon)

                        Toast.makeText(
                            this@AddPokemonActivity,
                            "¡Pokemon subido con éxito!",
                            Toast.LENGTH_LONG
                        ).show()

                        val intent = Intent(this@AddPokemonActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }

                    override fun onError(requestId: String?, error: ErrorInfo?) {
                        Toast.makeText(
                            this@AddPokemonActivity,
                            "Error al subir la imagen: ${error?.description}",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    override fun onReschedule(requestId: String?, error: ErrorInfo?) {}
                }).dispatch()
        } else {
            Toast.makeText(this, "Selecciona una imagen primero", Toast.LENGTH_SHORT).show()
        }
    }
}
