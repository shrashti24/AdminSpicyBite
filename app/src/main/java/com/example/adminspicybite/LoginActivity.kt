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

        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
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

            if (email.isEmpty()) {

                binding.emailOrPhone.error = "Enter email"
                binding.emailOrPhone.requestFocus()
                return@setOnClickListener
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

                binding.emailOrPhone.error = "Enter valid email"
                binding.emailOrPhone.requestFocus()
                return@setOnClickListener
            }

            if (password.isEmpty()) {

                binding.password.error = "Enter password"
                binding.password.requestFocus()
                return@setOnClickListener
            }

            createUserAccount(email, password)

        }
        binding.googlebutton.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            launcher.launch(signInIntent)

        }
        binding.donthavebutton.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        var isPasswordVisible = false

        binding.eyeIcon.setOnClickListener {

            if (isPasswordVisible) {
                // 🔒 Hide password
                binding.password.inputType =
                    android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD

                binding.eyeIcon.setImageResource(R.drawable.eye_off)
                isPasswordVisible = false

            } else {
                // 👁️ Show password
                binding.password.inputType =
                    android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD

                binding.eyeIcon.setImageResource(R.drawable.eye)
                isPasswordVisible = true
            }

            // ✅ IMPORTANT: Font same rakho (XML wala)
            binding.password.typeface = resources.getFont(R.font.lato_regular)
            // 👆 yaha apna font name daalo (jo XML me hai)

            // cursor end me
            binding.password.setSelection(binding.password.text.length)
        }
        binding.forgotpassword.setOnClickListener {

            val email = binding.emailOrPhone.text.toString().trim()

            if (email.isEmpty()) {
                Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Enter valid email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            sendPasswordResetLink(email)
        }

    }

    private fun sendPasswordResetLink(email: String) {

        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {
                    Toast.makeText(
                        this,
                        "Reset link sent to your email",
                        Toast.LENGTH_LONG
                    ).show()

                } else {
                    Toast.makeText(this, "Failed to send reset link", Toast.LENGTH_LONG).show()
                }
            }
    }



    private fun createUserAccount(email: String, password: String) {

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {

                    // ✅ Login success
                    val user = auth.currentUser

                    updateUI(user)

                } else {

                    when {

                        task.exception?.message?.contains("password is invalid", true) == true -> {

                            Toast.makeText(
                                this,
                                "Incorrect Password",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        task.exception?.message?.contains("no user record", true) == true -> {

                            Toast.makeText(
                                this,
                                "Account does not exist. Please Sign Up.",
                                Toast.LENGTH_SHORT
                            ).show()

                            startActivity(Intent(this, SignUpActivity::class.java))
                        }

                        else -> {

                            Toast.makeText(
                                this,
                                "Login Failed",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
    }


    private val launcher=registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            if (task.isSuccessful) {
                val account: GoogleSignInAccount = task.result
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                auth.signInWithCredential(credential).addOnCompleteListener { task ->
                    if (task.isSuccessful) {

                        val user = auth.currentUser
                        val uid = user?.uid ?: return@addOnCompleteListener

                        // 🔥 CHECK ADMIN EXIST OR NOT
                        database.child("admin").child(uid).get()
                            .addOnSuccessListener { snapshot ->

                                if (snapshot.exists()) {
                                    // ✅ Already Admin
                                    Toast.makeText(this, "Admin Login Success", Toast.LENGTH_SHORT)
                                        .show()
                                    updateUI(user)

                                } else {
                                    // ❌ First time Google login → create admin
                                    val adminData = UserModel(
                                        userName = user?.displayName ?: "Admin",
                                        nameOfRestaurant = "",
                                        email = user?.email ?: "",
//                                        password = "",
                                        role = "admin"
                                    )

                                    database.child("admin").child(uid).setValue(adminData)

                                    Toast.makeText(this, "New Admin Created", Toast.LENGTH_SHORT)
                                        .show()
                                    updateUI(user)
                                }
                            }
                    } else {
                        Toast.makeText(this, "Google Sign-In Failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
    //check if user has alreadylogged in
    override fun onStart() {
        super.onStart()
        val currentUser:FirebaseUser?=auth.currentUser
       if(currentUser!=null){
           updateUI(currentUser)  // 🔥 ROLE CHECK
       }
    }
    private fun updateUI(user: FirebaseUser?) {

        val uid = user?.uid ?: return

        database.child("admin").child(uid).get()
            .addOnSuccessListener { snapshot ->

                if (snapshot.exists()) {
                    // ✅ Admin hai
                    Toast.makeText(this, "Login Successfully", Toast.LENGTH_SHORT).show()

                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    // ❌ Admin nahi hai
                    Toast.makeText(this, "Access Denied! Not an Admin", Toast.LENGTH_SHORT).show()
                    auth.signOut(

                    )
                }
            }
    }
}