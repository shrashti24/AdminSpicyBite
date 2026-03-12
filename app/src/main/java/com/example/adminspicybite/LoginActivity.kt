package com.example.adminspicybite

import android.app.Activity
import android.content.Intent
import android.os.Bundle

import android.widget.Toast

import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

import com.example.adminspicybite.databinding.ActivityLoginBinding
import com.example.adminspicybite.model.UserModel
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.database.DatabaseReference


class LoginActivity : AppCompatActivity() {
    private var userName: String? = null
    private var nameOfRestaurant: String? = null
    private lateinit var email: String
    private lateinit var password: String

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var googleSignInClient: GoogleSignInClient


    private val binding: ActivityLoginBinding by lazy { ActivityLoginBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val googleSignInOptions= GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()


        //initialse firebase auth
        auth = FirebaseAuth.getInstance()
        database = com.google.firebase.database.FirebaseDatabase.getInstance().reference
        //initialise google signin
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)

        binding.loginbutton.setOnClickListener {

            email = binding.emailOrPhone.text.toString().trim()
            password = binding.password.text.toString().trim()

            if (email.isBlank() || password.isBlank()) {

                Toast.makeText(this, "Please fill all the details", Toast.LENGTH_SHORT).show()

            }
            else if (!isValidEmail(email)) {

                binding.emailOrPhone.error = "Enter valid email"
                return@setOnClickListener

            }
            else if (!isValidPassword(password)) {

                binding.password.error =
                    "Password must contain uppercase, lowercase, number and special character"
                return@setOnClickListener
            }
            else {

                createUserAccount(email, password)

            }
        }
        binding.googlebutton.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            launcher.launch(signInIntent)

        }
        binding.donthavebutton.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }


    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isValidPassword(password: String): Boolean {
        val passwordRegex =
            Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[@#\$%^&+=!]).{6,}$")
        return passwordRegex.matches(password)
    }
    private fun createUserAccount(email: String, password: String) {

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {

                    val user = auth.currentUser
                    Toast.makeText(this, "Login Successfully", Toast.LENGTH_SHORT).show()
                    updateUI(user)

                } else {

                    // Account nahi mila → naya create karo
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { createTask ->

                            if (createTask.isSuccessful) {

                                val user = auth.currentUser

                                val userData = UserModel(
                                    "User",
                                    "Not Available",
                                    email,
                                    password
                                )

                                val userId = user?.uid

                                userId?.let {
                                    database.child("user").child(it).setValue(userData)
                                }

                                Toast.makeText(
                                    this,
                                    "Account Created Successfully",
                                    Toast.LENGTH_SHORT
                                ).show()

                                updateUI(user)

                            } else {

                                Toast.makeText(
                                    this,
                                    "Authentication Failed",
                                    Toast.LENGTH_SHORT
                                ).show()

                            }

                        }

                }

            }
    }


    private fun saveUserData() {

        val email = binding.emailOrPhone.text.toString().trim()
        val password = binding.password.text.toString().trim()

        val user = UserModel(
            userName = "User",
            nameOfRestaurant = "Not Available",
            email = email,
            password = password
        )

        val userId = auth.currentUser?.uid

        if (userId != null) {
            database.child("user").child(userId).setValue(user)
        }
    }


    private val launcher=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
    result->
        if(result.resultCode== Activity.RESULT_OK){
            val task=GoogleSignIn.getSignedInAccountFromIntent(result.data)
            if(task.isSuccessful){
                val account: GoogleSignInAccount =task.result
                val credential= GoogleAuthProvider.getCredential(account.idToken,null)
                auth.signInWithCredential(credential).addOnCompleteListener{it->
                    if(it.isSuccessful) {
                        Toast.makeText(this, "Succesfully sign in with Google", Toast.LENGTH_SHORT)
                            .show()
                        updateUI(it.result?.user)
                        finish()
                    }
                    else{
                        Toast.makeText(this, "Sign in with Google failed", Toast.LENGTH_SHORT).show()
                    }
                }

                    }
            else{
                Toast.makeText(this, "Sign in with Google failed", Toast.LENGTH_SHORT).show()
            }
    }
    }
    //check if user has alreadylogged in
    override fun onStart() {
        super.onStart()
        val currentUser:FirebaseUser?=auth.currentUser
       if(currentUser!=null){
           startActivity(Intent(this, MainActivity::class.java))
           finish()
       }
    }
    private fun updateUI(user: FirebaseUser?) {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}