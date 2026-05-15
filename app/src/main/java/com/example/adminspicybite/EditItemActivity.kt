package com.example.adminspicybite

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.adminspicybite.databinding.ActivityEditItemBinding
import com.google.firebase.database.FirebaseDatabase

class EditItemActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditItemBinding

    private var itemKey: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEditItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Receive Data
        itemKey = intent.getStringExtra("key") ?: ""

        binding.enterFoodName.setText(
            intent.getStringExtra("foodName")
        )

        binding.enterpricename.setText(
            intent.getStringExtra("foodPrice")
        )

        binding.categoryEditText.setText(
            intent.getStringExtra("foodCategory")
        )

        binding.description.setText(
            intent.getStringExtra("foodDescription")
        )

        binding.ingredient.setText(
            intent.getStringExtra("foodIngrediant")
        )

        // Update Button
        binding.AddItmeButton.setOnClickListener {

            updateItem()
        }
        binding.availabilitySwitch.isChecked =
            intent.getBooleanExtra(
                "itemAvailable",
                true
            )
        // Back Button
        binding.backButton.setOnClickListener {

            finish()
        }
    }

    private fun updateItem() {

        val updatedData = mapOf(

            "foodName" to binding.enterFoodName.text.toString(),

            "foodPrice" to binding.enterpricename.text.toString(),

            "foodCategory" to binding.categoryEditText.text.toString(),

            "foodDescription" to binding.description.text.toString(),

            "foodIngrediant" to binding.ingredient.text.toString(),
            "itemAvailable" to binding.availabilitySwitch.isChecked
        )

        FirebaseDatabase.getInstance()
            .reference
            .child("menu")
            .child(itemKey)
            .updateChildren(updatedData)

            .addOnSuccessListener {

                Toast.makeText(
                    this,
                    "Item Updated Successfully",
                    Toast.LENGTH_SHORT
                ).show()

                finish()
            }

            .addOnFailureListener {

                Toast.makeText(
                    this,
                    "Update Failed",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}