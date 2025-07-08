package vazquez.paul.mipokedex_vazquezpaul

import android.app.Activity
import android.app.ComponentCaller
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AddPokemonActivity : AppCompatActivity() {
    val CLOUD_NAME = "dluszraa2"
    val REQUEST_IMAGE_GET = 1
    val UPLOAD_PRESET = "pokemon-preset"
    var imageUri: Uri?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_pokemon)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val name: EditText = findViewById(R.id.etNombre)
        val number: EditText = findViewById(R.id.etNombre)
        val upload: Button = findViewById(R.id.uploadButton)
        val save: Button = findViewById(R.id.saveButton)

        upload.setOnClickListener {
            val intent: Intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_IMAGE_GET)
        }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
        caller: ComponentCaller
    ) {
        super.onActivityResult(requestCode, resultCode, data, caller)
        if (requestCode == REQUEST_IMAGE_GET && resultCode == Activity.RESULT_OK) {
            val fullPhotoUri: Uri? = data?.data

            if (fullPhotoUri != null)
                changeImage(fullPhotoUri)
        }
    }

    fun changeImage(uri: Uri) {
        val thumbnail: ImageView = findViewById(R.id.pokemonImage)
        try{
            thumbnail.setImageURI(uri)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}