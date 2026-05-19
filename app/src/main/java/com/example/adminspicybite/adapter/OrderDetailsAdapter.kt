package com.example.adminspicybite.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.adminspicybite.databinding.ItemOrderFoodBinding

class OrderDetailsAdapter(
    private val names: ArrayList<String>,
    private val prices: ArrayList<String>,
    private val images: ArrayList<String>,
    private val quantities: ArrayList<Int>
) : RecyclerView.Adapter<OrderDetailsAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemOrderFoodBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val binding = ItemOrderFoodBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ViewHolder(binding)
    }

    override fun getItemCount() = names.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.binding.foodName.text = names[position]
        holder.binding.foodPrice.text = "₹${prices[position]}"
        holder.binding.foodQty.text =
            "Qty: ${quantities[position]}"

        Glide.with(holder.itemView.context)
            .load(images[position])
            .into(holder.binding.foodImage)
    }
}