package com.example.adminspicybite

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.adminspicybite.databinding.ActivityAddItemBinding
import com.example.adminspicybite.model.AllMenu
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import org.json.JSONObject
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody


class AddItemActivity : AppCompatActivity() {
    //Food Item Details
    private lateinit var foodName: String
    private lateinit var foodPrice: String
    private lateinit var foodDescription: String
    private lateinit var foodIngrediant: String
    private var foodImageUri: Uri? = null

    //firebase
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    //binding


    private val binding: ActivityAddItemBinding by lazy {
        ActivityAddItemBinding.inflate(layoutInflater)
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        //initialise firebase
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        binding.selectedImage.setOnClickListener {
            pickImage.launch("image/*")

        }
        binding.AddItmeButton.setOnClickListener {
            //get data from fields
            foodName = binding.enterFoodName.text.toString().trim()
            foodPrice = binding.enterpricename.text.toString().trim()
            foodDescription = binding.description.text.toString().trim()
            foodIngrediant = binding.ingredient.text.toString().trim()

            if (!(foodName.isBlank() || foodPrice.isBlank() || foodDescription.isBlank() || foodIngrediant.isBlank())) {
                uploadData()
                Toast.makeText(this, "Item Added Successfully", Toast.LENGTH_SHORT).show()

            } else {
                Toast.makeText(this, "Please fill all the details", Toast.LENGTH_SHORT).show()
            }



            binding.backButton.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
        }

    }

    private fun uploadData() {

        val menuRef: DatabaseReference = database.child("menu")
        val newItemKey: String? = menuRef.push().key

        if (foodImageUri != null) {

            val inputStream = contentResolver.openInputStream(foodImageUri!!)
            val file = File(cacheDir, "upload_image.jpg")
            file.outputStream().use { inputStream!!.copyTo(it) }

            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

            val preset = "food_upload".toRequestBody("text/plain".toMediaTypeOrNull())

            val client = OkHttpClient()

            val request = Request.Builder()
                .url("https://api.cloudinary.com/v1_1/dgnacg3bg/image/upload")
                .post(
                    MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("file", file.name, requestFile)
                        .addFormDataPart("upload_preset", "food_upload")
                        .build()
                )
                .build()

            Thread {
                try {

                    val response = client.newCall(request).execute()
                    val responseData = response.body?.string()

                    val json = JSONObject(responseData!!)
                    val imageUrl = json.getString("secure_url")

                    val newItem = AllMenu(
                        newItemKey,
                        foodName = foodName,
                        foodPrice = foodPrice,
                        foodDescription = foodDescription,
                        foodImage = imageUrl,
                        foodIngrediant = foodIngrediant,
                    )

                    newItemKey?.let { key ->
                        menuRef.child(key).setValue(newItem)
                    }

                    runOnUiThread {
                        Toast.makeText(this, "Item Added Successfully", Toast.LENGTH_SHORT).show()
                        finish()
                    }

                } catch (e: Exception) {

                    runOnUiThread {
                        Toast.makeText(this, "Image Upload Failed", Toast.LENGTH_SHORT).show()
                    }

                }
            }.start()

        } else {
            Toast.makeText(this, "Please Select an Image", Toast.LENGTH_SHORT).show()
        }
    }
    // ✅ Image Picker Launcher (CLASS KE ANDAR)
    private val pickImage =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                foodImageUri = uri
                binding.selectedImage.setImageURI(uri)
            }
        }
    }