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
    private lateinit var email: String
    private lateinit var password: String
    private  lateinit var userName: String
    private lateinit var nameOfRestaurant: String
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
            email = binding.emailOrPhone.text.toString().trim()
            password = binding.password.text.toString().trim()
            userName = binding.owner.text.toString().trim()
            nameOfRestaurant = binding.restaurant.text.toString().trim()
            if (userName.isBlank() || nameOfRestaurant.isBlank() || email.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Please fill all the details", Toast.LENGTH_SHORT).show()
            }   else if (!isValidEmail(email)) {
                Toast.makeText(this, "Enter a valid email address", Toast.LENGTH_SHORT).show()
            }
            else if (!isValidPassword(password)) {
                Toast.makeText(
                    this,
                    "Password must be 8 characters, include 1 capital letter and 1 number",
                    Toast.LENGTH_LONG
                ).show()
            }
            else {
                createAccount(email, password)
            }
        }
        binding.alreadybtn.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        val locationList = arrayOf("Jaipur", "Odisha", "Bundi", "Sikar")

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            locationList
        )


        binding.location.setAdapter(adapter)
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

                val strongPattern = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#\$%^&+=!]).{7,}$")

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
    // Email validation
    private fun isValidEmail(email: String): Boolean {
        val emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
        return Regex(emailPattern).matches(email)
    }

    // Password validation
    private fun isValidPassword(password: String): Boolean {
        val passwordPattern = "^(?=.*[A-Z])(?=.*[0-9]).{8,}$"
        return Regex(passwordPattern).matches(password)
    }

    private fun createAccount(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Account Created Successfully", Toast.LENGTH_SHORT).show()
                    saveUserData()
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Account Creation Failed", Toast.LENGTH_SHORT).show()
                    Log.d("Account", "createAccount:Failur", task.exception)
                }
            }
    }
//save data in  to database
    fun saveUserData() {
        email = binding.emailOrPhone.text.toString().trim()
        password = binding.password.text.toString().trim()
        userName = binding.owner.text.toString().trim()
        nameOfRestaurant = binding.restaurant.text.toString().trim()
    val user = UserModel(
        userName,
        nameOfRestaurant,
        email,
        binding.location.text.toString(), // address
        "", // phone
        "admin" // role// 🔥 MOST IMPORTANT
    )
        val userId:String=FirebaseAuth.getInstance().currentUser!!.uid
    //save user data firebase database
        database.child("admin").child(userId).setValue(user)


    }
}