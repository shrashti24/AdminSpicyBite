package com.example.adminspicybite

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.adminspicybite.databinding.ActivityCreateUserBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class CreateUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateUserBinding

    private lateinit var auth: FirebaseAuth   // ✅ single instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        val adminUser = auth.currentUser   // ✅ admin ko save karo

        binding.backButton.setOnClickListener {
            finish()
        }
        var isPasswordVisible = false

        binding.eyeIcon.setOnClickListener {

            if (isPasswordVisible) {
                binding.password.inputType =
                    android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD

                binding.eyeIcon.setImageResource(R.drawable.eye_off)
                isPasswordVisible = false

            } else {
                binding.password.inputType =
                    android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD

                binding.eyeIcon.setImageResource(R.drawable.eye)
                isPasswordVisible = true
            }

            binding.password.typeface = resources.getFont(R.font.lato_regular)
            binding.password.setSelection(binding.password.text.length)
        }

        binding.phone.addTextChangedListener(object : android.text.TextWatcher {

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {

                val phone = s.toString()
                val pattern = Regex("^[6-9][0-9]{9}$")

                if (phone.isEmpty()) {
                    binding.phone.error = null
                } else if (phone.matches(pattern)) {
                    binding.phone.error = null   // ✅ valid
                } else {
                    binding.phone.error = "Invalid phone number ❌"
                }
            }

            override fun afterTextChanged(s: android.text.Editable?) {}
        })
        binding.password.addTextChangedListener(object : android.text.TextWatcher {

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {

                val password = s.toString()

                val pattern =
                    Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#\$%^&+=!]).{7,}$")

                if (password.isEmpty()) {
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

        binding.createuserBtn.setOnClickListener {

            val name = binding.name.text.toString().trim()
            val email = binding.email.text.toString().trim()
            val phone = binding.phone.text.toString().trim()
            val password = binding.password.text.toString().trim()

            val phonePattern = Regex("^[6-9][0-9]{9}$")

            if (!phone.matches(phonePattern)) {
                Toast.makeText(this, "Enter valid 10-digit phone number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || phone.isEmpty()) {
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Enter valid email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val passwordPattern =
                Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#\$%^&+=!]).{7,}$")

            if (!password.matches(passwordPattern)) {
                Toast.makeText(
                    this,
                    "Strong password required",
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }

            // 🔥 CREATE DELIVERY BOY
            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener {

                    val userId = auth.currentUser!!.uid
                    val db = FirebaseDatabase.getInstance().reference

                    // 👤 USER NODE (LOGIN PURPOSE)
                    val userMap = HashMap<String, Any>()
                    userMap["name"] = name
                    userMap["email"] = email
                    userMap["phone"] = phone
                    userMap["role"] = "deliveryBoy"
                    userMap["isAvailable"] = true
                    userMap["earnings"] = 0

                    // 🚴 DELIVERY BOY NODE (REAL WORK DATA)
                    val deliveryMap = HashMap<String, Any>()
                    deliveryMap["id"] = userId
                    deliveryMap["name"] = name
                    deliveryMap["email"] = email
                    deliveryMap["phone"] = phone
                    deliveryMap["assignedOrders"] = 0
                    deliveryMap["deliveredOrders"] = 0
                    deliveryMap["activeDrops"] = 0
                    deliveryMap["codEnabled"] = true
                    deliveryMap["isAvailable"] = true

                    // 🔥 SAVE TO FIREBASE
                    db.child("Users").child(userId).setValue(userMap)
                    db.child("DeliveryBoys").child(userId).setValue(deliveryMap)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Delivery Boy Created", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Failed: ${it.message}", Toast.LENGTH_SHORT).show()
                        }
                }
                .addOnFailureListener {
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }




        }
    }
}