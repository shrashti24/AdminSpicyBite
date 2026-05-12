package com.example.adminspicybite

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.adminspicybite.databinding.ActivitySignUpBinding
import com.example.adminspicybite.model.UserModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database

class SignUpActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    private lateinit var database: DatabaseReference

    private lateinit var binding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //initilase firebsse auth
        auth = FirebaseAuth.getInstance()

        //initialse firebase database
        database = Firebase.database.reference

        binding.createBtn.setOnClickListener {
            //get text from edittext
           createAccount()
        }
        binding.alreadybtn.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

//        val locationList = arrayOf("Jaipur", "Odisha", "Bundi", "Sikar")
//
//        val adapter = ArrayAdapter(
//            this,
//            android.R.layout.simple_list_item_1,
//            locationList
//        )
//
//
//        binding.location.setAdapter(adapter)
        var isPasswordVisible2 = false

        binding.eyeIcon2.setOnClickListener {

            if (isPasswordVisible2) {
                binding.password.inputType =
                    android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD

                binding.eyeIcon2.setImageResource(R.drawable.eye_off)
                isPasswordVisible2 = false

            } else {
                binding.password.inputType =
                    android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD

                binding.eyeIcon2.setImageResource(R.drawable.eye)
                isPasswordVisible2 = true
            }

            // font fix
            binding.password.typeface = resources.getFont(R.font.lato_regular)

            binding.password.setSelection(binding.password.text.length)
        }
        binding.password.addTextChangedListener(object : android.text.TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                val password = s.toString()

                val strongPattern =
                    Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#\$%^&+=!]).{8,}$")

                if (password.isEmpty()) {
                    // 👉 start me kuch show nahi
                    binding.passwordStatus.text = ""

                } else if (strongPattern.matches(password)) {
                    // ✅ Strong
                    binding.passwordStatus.text = "Strong Password ✅"
                    binding.passwordStatus.setTextColor(
                        androidx.core.content.ContextCompat.getColor(
                            this@SignUpActivity,
                            android.R.color.holo_green_dark
                        )
                    )

                } else {
                    // ❌ Weak
                    binding.passwordStatus.text = "Weak Password ❌"
                    binding.passwordStatus.setTextColor(
                        androidx.core.content.ContextCompat.getColor(
                            this@SignUpActivity,
                            android.R.color.holo_red_dark
                        )
                    )
                }
            }

            override fun afterTextChanged(s: android.text.Editable?) {}
        })
    }

    private fun createAccount() {
        val firstName = binding.firstName.text.toString().trim()
        val middleName = binding.middleName.text.toString().trim()
        val lastName = binding.lastName.text.toString().trim()
        val restaurant = binding.restaurant.text.toString().trim()
        val email = binding.emailOrPhone.text.toString().trim()
        val password = binding.password.text.toString().trim()

        // FULL NAME
        val fullName =
            listOf(firstName, middleName, lastName)
                .filter { it.trim().isNotEmpty() }
                .joinToString(" ")
        // ================= VALIDATION =================

        if (firstName.isEmpty()) {
            binding.firstName.error = "Enter first name"
            return
        }

        if (!firstName.matches(Regex("^[A-Za-z ]+$"))) {
            binding.firstName.error = "Only alphabets allowed"
            return
        }
        if (middleName.isNotEmpty() &&
            !middleName.matches(Regex("^[A-Za-z ]+$"))
        ) {
            binding.middleName.error = "Only alphabets allowed"
            return
        }
        if (lastName.isNotEmpty() &&
            !lastName.matches(Regex("^[A-Za-z ]+$"))
        ) {
            binding.lastName.error = "Only alphabets allowed"
            return
        }
        if (restaurant.isEmpty()) {
            binding.restaurant.error = "Enter restaurant name"
            return
        }

        if (email.isEmpty()) {
            binding.emailOrPhone.error = "Enter email"
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailOrPhone.error = "Invalid email"
            return
        }

        if (password.isEmpty()) {
            binding.password.error = "Enter password"
            return
        }

        val passwordPattern =
            Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#\$%^&+=!]).{8,}$")

        if (!password.matches(passwordPattern)) {
            binding.password.error = "Weak password"
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    saveUserData(
                        firstName,
                        middleName,
                        lastName,
                        fullName,
                        restaurant,
                        email
                    )

                } else {
                    Toast.makeText(
                        this,
                        task.exception?.message.toString(),
                        Toast.LENGTH_LONG
                    ).show()

                    Log.e("FirebaseError", task.exception.toString())
                }
            }
    }
// ================= SAVE DATA =================

    private fun saveUserData(
        firstName: String,
        middleName: String,
        lastName: String,
        fullName: String,
        restaurantName: String,
        email: String
    ) {

        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId == null) {
            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show()
            return
        }
        val user = UserModel(

            firstName = firstName,
            middleName = middleName,
            lastName = lastName,

            userName = fullName,

            nameOfRestaurant = restaurantName,

            email = email,

            address = "",

            phone = "",

            role = "admin"
        )

        database.child("admin")
            .child(userId)
            .setValue(user)
            .addOnSuccessListener {

                Toast.makeText(
                    this,
                    "Account Created Successfully",
                    Toast.LENGTH_SHORT
                ).show()

                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
            .addOnFailureListener {

                Toast.makeText(
                    this,
                    "Failed to save data",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}