package com.example.adminspicybite

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.adminspicybite.databinding.ActivityCreateUserBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class CreateUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateUserBinding

    private lateinit var authAdmin: FirebaseAuth
    private lateinit var authUser: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        authAdmin = FirebaseAuth.getInstance()
        authUser = FirebaseAuth.getInstance()

        // 🔙 Back button
        binding.backButton.setOnClickListener {
            finish()
        }

        // 🔥 Create User Button
        binding.createuserBtn.setOnClickListener {

            val name = binding.name.text.toString().trim()
            val email = binding.email.text.toString().trim()
            val phone = binding.phone.text.toString().trim()
            val password = binding.password.text.toString().trim()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()|| phone.isEmpty()) {
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

// ✅ Email validation
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Enter valid email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

// ✅ Strong password validation
            val passwordPattern = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#\$%^&+=!]).{7,}$")

            if (!password.matches(passwordPattern)) {
                Toast.makeText(
                    this,
                    "Password must contain:\nUppercase, Lowercase, Number, Special symbol & 7+ length",
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }

            // 🔥 Create user in Firebase Auth
            authUser.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->

                    if (task.isSuccessful) {

                        val userId = authUser.currentUser!!.uid

                        val userMap = HashMap<String, String>()
                        userMap["name"] = name
                        userMap["email"] = email
                        userMap["phone"] = phone
                        userMap["password"] = password
                        userMap["role"] = "deliveryBoy"

                        // 🔥 Save in Database
                        FirebaseDatabase.getInstance().reference
                            .child("Users")
                            .child(userId)
                            .setValue(userMap)
                            .addOnSuccessListener {

                                Toast.makeText(this, "Delivery Boy Created", Toast.LENGTH_SHORT).show()


                                // 🔥 Fix
                                authUser.signOut()
                                finish()
                            }

                    } else {
                        Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()
                    }
                }
        }
        var isPasswordVisible = false

        binding.eyeIcon.setOnClickListener {

            if (isPasswordVisible) {
                // 🙈 Hide password
                binding.password.inputType =
                    android.text.InputType.TYPE_CLASS_TEXT or
                            android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD

                binding.eyeIcon.setImageResource(R.drawable.eye_off)

            } else {
                // 👁️ Show password
                binding.password.inputType =
                    android.text.InputType.TYPE_CLASS_TEXT or
                            android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD

                binding.eyeIcon.setImageResource(R.drawable.eye)
            }

            // cursor end pe rakho
            binding.password.setSelection(binding.password.text.length)

            isPasswordVisible = !isPasswordVisible
        }
        binding.password.addTextChangedListener(object : android.text.TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                val password = s.toString()

                val pattern = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#\$%^&+=!]).{7,}$")

                if (password.isEmpty()) {
                    // 👉 Kuch mat dikhao
                    binding.passwordStatus.text = ""

                } else if (password.matches(pattern)) {
                    // ✅ Strong
                    binding.passwordStatus.text = "Strong Password ✅"
                    binding.passwordStatus.setTextColor(resources.getColor(android.R.color.holo_green_dark))

                } else {
                    // ❌ Weak
                    binding.passwordStatus.text = "Weak Password ❌"
                    binding.passwordStatus.setTextColor(resources.getColor(android.R.color.holo_red_dark))
                }
            }

            override fun afterTextChanged(s: android.text.Editable?) {}
        })
    }
}