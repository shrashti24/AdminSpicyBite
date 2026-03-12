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
        val user= UserModel(userName,nameOfRestaurant,email,password)
        val userId:String=FirebaseAuth.getInstance().currentUser!!.uid
    //save user data firebase database
        database.child("user").child(userId).setValue(user)


    }
}