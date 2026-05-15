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
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File

class AddItemActivity : AppCompatActivity() {

    // Food Item Details
    private lateinit var foodName: String
    private lateinit var foodPrice: String
    private lateinit var foodDescription: String
    private lateinit var foodIngrediant: String
    private lateinit var foodCategory: String

    private var foodImageUri: Uri? = null

    // Firebase
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private var itemAvailable: Boolean = true
    // View Binding
    private val binding: ActivityAddItemBinding by lazy {
        ActivityAddItemBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        // Pick Image
        binding.selectedImage.setOnClickListener {
            pickImage.launch("image/*")
        }

        // Back Button
        binding.backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Add Item Button
        binding.AddItmeButton.setOnClickListener {

            // Get Data
            foodName = binding.enterFoodName.text.toString().trim()
            foodPrice = binding.enterpricename.text.toString().trim()
            foodDescription = binding.description.text.toString().trim()
            foodIngrediant = binding.ingredient.text.toString().trim()

            // Category
            foodCategory = binding.categoryEditText.text.toString().trim()
            itemAvailable = binding.availabilitySwitch.isChecked
            // Validation
            if (
                foodName.isBlank() ||
                foodPrice.isBlank() ||
                foodDescription.isBlank() ||
                foodIngrediant.isBlank() ||
                foodCategory.isBlank()
            ) {

                Toast.makeText(
                    this,
                    "Please fill all details",
                    Toast.LENGTH_SHORT
                ).show()

            } else {

                uploadData()
            }
        }
    }

    private fun uploadData() {

        val menuRef: DatabaseReference = database.child("menu")

        val newItemKey: String? = menuRef.push().key

        if (foodImageUri != null) {

            val inputStream =
                contentResolver.openInputStream(foodImageUri!!)

            val file = File(cacheDir, "upload_image.jpg")

            file.outputStream().use {
                inputStream!!.copyTo(it)
            }

            val requestFile =
                file.asRequestBody("image/*".toMediaTypeOrNull())

            val client = OkHttpClient()

            val request = Request.Builder()
                .url("https://api.cloudinary.com/v1_1/dgnacg3bg/image/upload")
                .post(
                    MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart(
                            "file",
                            file.name,
                            requestFile
                        )
                        .addFormDataPart(
                            "upload_preset",
                            "food_upload"
                        )
                        .build()
                )
                .build()

            Thread {

                try {

                    val response =
                        client.newCall(request).execute()

                    val responseData =
                        response.body?.string()

                    val json =
                        JSONObject(responseData!!)

                    val imageUrl =
                        json.getString("secure_url")

                    // Create Menu Object
                    val newItem = AllMenu(

                        key = newItemKey,

                        foodName = foodName,

                        foodPrice = foodPrice,

                        foodCategory = foodCategory,

                        foodDescription = foodDescription,

                        foodIngrediant = foodIngrediant,

                        foodImage = imageUrl,

                        itemAvailable = itemAvailable
                    )

                    // Upload To Firebase
                    newItemKey?.let { key ->

                        menuRef.child(key)
                            .setValue(newItem)
                    }

                    runOnUiThread {

                        Toast.makeText(
                            this,
                            "Item Added Successfully",
                            Toast.LENGTH_SHORT
                        ).show()

                        finish()
                    }

                } catch (e: Exception) {

                    runOnUiThread {

                        Toast.makeText(
                            this,
                            "Image Upload Failed",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            }.start()

        } else {

            Toast.makeText(
                this,
                "Please Select Image",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // Image Picker
    private val pickImage =
        registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri: Uri? ->

            if (uri != null) {

                foodImageUri = uri

                binding.selectedImage.setImageURI(uri)
            }
        }
}