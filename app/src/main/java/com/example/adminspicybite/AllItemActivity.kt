package com.example.adminspicybite

import android.os.Bundle
import android.util.Log
import android.content.Intent
import androidx.appcompat.app.AlertDialog
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.adminspicybite.adapter.MenuItemAdapter
import com.example.adminspicybite.databinding.ActivityAllItemBinding
import com.example.adminspicybite.model.AllMenu
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AllItemActivity : AppCompatActivity() {
    private lateinit var databaseReference: DatabaseReference
    private lateinit var database: FirebaseDatabase
    private val menuItems: ArrayList<AllMenu> = ArrayList()
    private val binding: ActivityAllItemBinding by lazy {
        ActivityAllItemBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)
        databaseReference = FirebaseDatabase.getInstance().reference
        retrieveMenuItem()



        binding.backButton.setOnClickListener {
            finish()
        }
    }

    private fun retrieveMenuItem() {
        database = FirebaseDatabase.getInstance()
        val foodRef: DatabaseReference = database.reference.child("menu")
        //fetchdata from db
        foodRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                //clear existing data before populating
                menuItems.clear()

                //loop for though each food item
                for (foodSnapshot in snapshot.children) {
                    val menuItem = foodSnapshot.getValue(AllMenu::class.java)
                    menuItem?.let {
                        menuItems.add(it)

                    }
                }
                setAdapter()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("DatabaseError", "Error, ${error.message}")
            }
        })
    }

    private fun setAdapter() {

        val adapter = MenuItemAdapter(

            this@AllItemActivity,
            menuItems,
            databaseReference,

            onDeleteClick = { position ->
                deleteMenuItems(position)
            },

            onEditClick = { position ->

                val intent = Intent(
                    this@AllItemActivity,
                    EditItemActivity::class.java
                )

                val selectedItem = menuItems[position]

                intent.putExtra("key", selectedItem.key)

                intent.putExtra(
                    "foodName",
                    selectedItem.foodName
                )

                intent.putExtra(
                    "foodPrice",
                    selectedItem.foodPrice
                )

                intent.putExtra(
                    "foodCategory",
                    selectedItem.foodCategory
                )

                intent.putExtra(
                    "foodDescription",
                    selectedItem.foodDescription
                )

                intent.putExtra(
                    "foodIngrediant",
                    selectedItem.foodIngrediant
                )
                intent.putExtra(
                    "itemAvailable",
                    selectedItem.itemAvailable
                )
                startActivity(intent)
            }
        )
        binding.MenuRecycler.layoutManager = LinearLayoutManager(this)
        binding.MenuRecycler.adapter = adapter
    }

    private fun deleteMenuItems(position: Int) {

        val menuItemToDelete = menuItems[position]

        AlertDialog.Builder(this)
            .setTitle("Delete Item")
            .setMessage(
                "Are you sure you want to delete ${
                    menuItemToDelete.foodName
                } ?"
            )

            .setPositiveButton("Yes") { _, _ ->

                val menuItemKey = menuItemToDelete.key

                val foodMenuReference =
                    databaseReference
                        .child("menu")
                        .child(menuItemKey!!)

                foodMenuReference.removeValue()
                    .addOnCompleteListener { task ->

                        if (task.isSuccessful) {

                            menuItems.removeAt(position)

                            binding.MenuRecycler.adapter
                                ?.notifyItemRemoved(position)

                            Toast.makeText(
                                this,
                                "Item Deleted",
                                Toast.LENGTH_SHORT
                            ).show()

                        } else {

                            Toast.makeText(
                                this,
                                "Item Not Deleted",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }

            .setNegativeButton("Cancel", null)

            .show()
    }
}




