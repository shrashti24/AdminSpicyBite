package com.example.adminspicybite.adapter

import android.content.Context
import android.net.Uri
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
    databaseReference: DatabaseReference,
    private val onDeleteClickListener: (position: Int) -> Unit
) : RecyclerView.Adapter<MenuItemAdapter.AddItemViewHolder>() {

    private val itemQuantities = IntArray(menuList.size) { 1 }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddItemViewHolder {
        val binding = ItemItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AddItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AddItemViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = menuList.size

    inner class AddItemViewHolder(private val binding: ItemItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) {
            val quantity: Int=itemQuantities[position]
            val menuItem: AllMenu = menuList[position]
            val uriString: String?= menuItem.foodImage
            val uri: Uri = Uri.parse(uriString)
            binding.foodnametextview.text = menuItem.foodName
            binding.pricetextview.text =menuItem.foodPrice
            Glide.with(context)
                .load(uri)
                .into(binding.foodImageView)

            binding.quantity.text = itemQuantities[position].toString()

            binding.plusButton.setOnClickListener {
                if (itemQuantities[position] < 10) {
                    itemQuantities[position]++
                    binding.quantity.text =
                        itemQuantities[position].toString()
                }
            }

            binding.minusButton.setOnClickListener {
                if (itemQuantities[position] > 1) {
                    itemQuantities[position]--
                    binding.quantity.text =
                        itemQuantities[position].toString()
                }
            }

            binding.deleteButton.setOnClickListener {
                onDeleteClickListener(position)
            }
        }
    }

    private fun deleteItem(position: Int) {
        menuList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, menuList.size)
    }
}