package com.example.adminspicybite

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.adminspicybite.databinding.ActivityAdminProfileBinding
import com.example.adminspicybite.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdminProfileActivity : AppCompatActivity() {
    private val binding: ActivityAdminProfileBinding by lazy {
        ActivityAdminProfileBinding.inflate(layoutInflater)
    }
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private lateinit var adminReference: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance(

        )
        adminReference = database.reference.child("admin")
        binding.backButton.setOnClickListener {
            finish()
        }
        binding.saveInfoButton.setOnClickListener {
            updateUserData()
        }
        binding.name.isEnabled = false
        binding.address.isEnabled = false
        binding.email.isEnabled = false
        binding.phone.isEnabled = false
        // 👈 ADD THIS
        binding.saveInfoButton.isEnabled = false

        var isEnable = false
        binding.editbutton.setOnClickListener {
            isEnable = !isEnable
            binding.name.isEnabled = isEnable
            binding.address.isEnabled = isEnable
            binding.email.isEnabled = isEnable
            binding.phone.isEnabled = isEnable

            binding.saveInfoButton.isEnabled = isEnable
            if (isEnable) {
                binding.name.requestFocus()
            }
        }

        var isPasswordVisibleTop = false


        retrieveUserData()

    }


    private fun retrieveUserData() {
        val uid = auth.currentUser?.uid

        if (uid != null) {
            adminReference.child(uid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {

                        if (snapshot.exists()) {
                            val userName = snapshot.child("userName").value
                            val email = snapshot.child("email").value
                            val address = snapshot.child("address").value
                            val phone = snapshot.child("phone").value

                            binding.name.setText(userName.toString())
                            binding.email.setText(email.toString())
                            binding.address.setText(address.toString())
                            binding.phone.setText(phone.toString())
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(this@AdminProfileActivity, error.message, Toast.LENGTH_SHORT)
                            .show()
                    }
                })
        }
    }


    private fun setDataToTextView(
        userName: Any?,
        email: Any?,
        password: Any?,
        address: Any?,
        phone: Any?
    ) {
        binding.name.setText(userName.toString())
        binding.email.setText(email.toString())

        binding.address.setText(address.toString())
        binding.phone.setText(phone.toString())
    }

    private fun updateUserData() {
        val updateName = binding.name.text.toString()
        var updateEmail = binding.email.text.toString()
        var updateAddress = binding.address.text.toString()
        var updatePhone = binding.phone.text.toString()
        // ✅ Phone validation
        if (updatePhone.length != 10) {
            Toast.makeText(this, "Enter valid 10 digit phone number", Toast.LENGTH_SHORT).show()
            return
        }

        val currentUserUid = auth.currentUser?.uid

        if (currentUserUid != null) {
            val userMap = mapOf(
                "userName" to updateName,
                "email" to updateEmail,

                "address" to updateAddress,
                "phone" to updatePhone
            )

            adminReference.child(currentUserUid).updateChildren(userMap)
                .addOnCompleteListener {
                    Toast.makeText(this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show()
                    auth.currentUser?.updateEmail(updateEmail)
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "Failed: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}