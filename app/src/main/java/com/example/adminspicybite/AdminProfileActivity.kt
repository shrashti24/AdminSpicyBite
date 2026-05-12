package com.example.adminspicybite

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.adminspicybite.databinding.ActivityAdminProfileBinding
import com.example.adminspicybite.model.UserModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

class AdminProfileActivity : AppCompatActivity() {
    private val binding: ActivityAdminProfileBinding by lazy {
        ActivityAdminProfileBinding.inflate(layoutInflater)
    }
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private lateinit var adminReference: DatabaseReference

    private var isEnable = false
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
            updateProfile()
        }
        binding.name.isEnabled = false
        binding.houseNo.isEnabled = false
        binding.street.isEnabled = false
        binding.city.isEnabled = false
        binding.state.isEnabled = false
        binding.pincode.isEnabled = false
        binding.email.isEnabled = false
        binding.phone.isEnabled = false
        // 👈 ADD THIS
        binding.saveInfoButton.isEnabled = false
        binding.editbutton.text = "Edit Your Profile"

        binding.editbutton.setOnClickListener {
            isEnable = !isEnable
            binding.name.isEnabled = isEnable
            binding.houseNo.isEnabled = isEnable
            binding.street.isEnabled = isEnable
            binding.city.isEnabled = isEnable
            binding.state.isEnabled = isEnable
            binding.pincode.isEnabled = isEnable
            binding.email.isEnabled = isEnable
            binding.phone.isEnabled = isEnable

            binding.saveInfoButton.isEnabled = isEnable
            binding.editbutton.text =
                if (isEnable) "Cancel Edit"
                else "Edit Your Profile"
            if (isEnable) {
                binding.name.requestFocus()
            }
        }



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
                            val houseNo = snapshot.child("houseNo").value
                            val street = snapshot.child("street").value
                            val city = snapshot.child("city").value
                            val state = snapshot.child("state").value
                            val pincode = snapshot.child("pincode").value

                            val phone = snapshot.child("phone").value

                            binding.name.setText(userName?.toString() ?: "")
                            binding.email.setText(email?.toString() ?: "")
                            binding.houseNo.setText(houseNo?.toString() ?: "")
                            binding.street.setText(street?.toString() ?: "")
                            binding.city.setText(city?.toString() ?: "")
                            binding.state.setText(state?.toString() ?: "")
                            binding.pincode.setText(pincode?.toString() ?: "")
                            binding.phone.setText(phone?.toString() ?: "")
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(this@AdminProfileActivity, error.message, Toast.LENGTH_SHORT)
                            .show()
                    }
                })
        }
    }


    private fun updateProfile() {

        val fullName = binding.name.text.toString().trim()

        val houseNo = binding.houseNo.text.toString().trim()
        val street = binding.street.text.toString().trim()
        val city = binding.city.text.toString().trim()
        val state = binding.state.text.toString().trim()
        val pincode = binding.pincode.text.toString().trim()

        val email = binding.email.text.toString().trim()
        val phone = binding.phone.text.toString().trim()

        // ================= NAME VALIDATION =================

        if (fullName.isEmpty()) {
            binding.name.error = "Enter Full Name"
            return
        }

        if (!fullName.matches(Regex("^[A-Za-z ]+$"))) {
            binding.name.error = "Only alphabets allowed"
            return
        }

        // ================= ADDRESS VALIDATION =================

        if (houseNo.isEmpty() || houseNo.equals("null", true)) {
            binding.houseNo.error = "Enter House Number"
            return
        }

        if (street.isEmpty() || street.equals("null", true)) {
            binding.street.error = "Enter Street"
            return
        }

        if (city.isEmpty() || city.equals("null", true)) {
            binding.city.error = "Enter City"
            return
        }

        if (!city.matches(Regex("^[A-Za-z ]+$"))) {
            binding.city.error = "Only alphabets allowed"
            return
        }

        if (state.isEmpty() || state.equals("null", true)) {
            binding.state.error = "Enter State"
            return
        }

        if (!state.matches(Regex("^[A-Za-z ]+$"))) {
            binding.state.error = "Only alphabets allowed"
            return
        }

        if (pincode.isEmpty() || pincode.equals("null", true)) {
            binding.pincode.error = "Enter Pincode"
            return
        }

        if (!pincode.matches(Regex("^[0-9]{6}$"))) {
            binding.pincode.error = "Enter valid 6 digit pincode"
            return
        }

        // ================= EMAIL VALIDATION =================

        if (email.isEmpty()) {
            binding.email.error = "Enter Email"
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.email.error = "Invalid Email"
            return
        }

        // ================= PHONE VALIDATION =================

        if (phone.isEmpty()) {
            binding.phone.error = "Enter Phone Number"
            return
        }

        if (!phone.matches(Regex("^[6-9][0-9]{9}$"))) {
            binding.phone.error = "Invalid Phone Number"
            return
        }

        // ================= SPLIT NAME =================

        val nameParts = fullName.split(" ")

        val firstName = nameParts.getOrNull(0) ?: ""
        val middleName =
            if (nameParts.size > 2)
                nameParts.subList(1, nameParts.size - 1).joinToString(" ")
            else ""

        val lastName =
            if (nameParts.size > 1)
                nameParts.last()
            else ""

        // ================= FULL ADDRESS =================

        val fullAddress =
            "$houseNo, $street, $city, $state - $pincode"

        // ================= FIREBASE UPDATE =================

        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId != null) {
            // 🔥 First update Firebase Auth email
            auth.currentUser?.updateEmail(email)
                ?.addOnSuccessListener {

                    val updateMap = mapOf(

                        // name fields
                        "userName" to fullName,
                        "firstName" to firstName,
                        "middleName" to middleName,
                        "lastName" to lastName,

                        // address
                        "houseNo" to houseNo,
                        "street" to street,
                        "city" to city,
                        "state" to state,
                        "pincode" to pincode,
                        "address" to fullAddress,

                        // contact
                        "email" to email,
                        "phone" to phone
                    )

                    Firebase.database.reference
                        .child("admin")
                        .child(userId)
                        .updateChildren(updateMap)
                        .addOnSuccessListener {
                            // 🔥 Disable edit mode after save
                            isEnable = false

                            binding.name.isEnabled = false
                            binding.houseNo.isEnabled = false
                            binding.street.isEnabled = false
                            binding.city.isEnabled = false
                            binding.state.isEnabled = false
                            binding.pincode.isEnabled = false
                            binding.email.isEnabled = false
                            binding.phone.isEnabled = false

                            binding.saveInfoButton.isEnabled = false

                            binding.editbutton.text = "Edit Your Profile"
                            Toast.makeText(
                                this,
                                "Profile Updated Successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        .addOnFailureListener {

                            Toast.makeText(
                                this,
                                "Update Failed",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                }
                ?.addOnFailureListener {

                    Toast.makeText(
                        this,
                        "Email Update Failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }
}