package com.example.adminspicybite.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.adminspicybite.databinding.ItemItemBinding
import com.example.adminspicybite.model.AllMenu
import com.google.firebase.database.DatabaseReference

class MenuItemAdapter(

    private val context: Context,

    private val menuList: ArrayList<AllMenu>,

    private val databaseReference: DatabaseReference,

    private val onDeleteClick: (Int) -> Unit,

    private val onEditClick: (Int) -> Unit

) : RecyclerView.Adapter<MenuItemAdapter.MenuViewHolder>() {

    inner class MenuViewHolder(
        val binding: ItemItemBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MenuViewHolder {

        val binding = ItemItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return MenuViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: MenuViewHolder,
        position: Int
    ) {

        val menuItem = menuList[position]

        holder.binding.apply {

            foodNameTextView.text =
                menuItem.foodName

            priceTextView.text =
                "₹${menuItem.foodPrice}"

            categoryTextView.text =
                menuItem.foodCategory

            // Status
            if (menuItem.itemAvailable == true) {

                statusTextView.text =
                    "🟢 Available"

            } else {

                statusTextView.text =
                    "🔴 Out Of Stock"
            }

            // Image
            Glide.with(context)
                .load(menuItem.foodImage)
                .into(foodImageView)

            // Delete Click
            deleteButton.setOnClickListener {

                onDeleteClick(position)
            }

            // Edit Click
            // Edit Click (TOGGLE LOGIC)
            editButton.setOnClickListener {

                menuItem.isEditing = !menuItem.isEditing

                notifyItemChanged(position)

                onEditClick(position)
            }
        }
    }

    override fun getItemCount(): Int {

        return menuList.size
    }
}