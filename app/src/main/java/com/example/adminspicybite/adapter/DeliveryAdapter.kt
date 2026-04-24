package com.example.adminspicybite.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.adminspicybite.databinding.DeliveryItemBinding
import com.example.adminspicybite.model.DeliveryBoy

class DeliveryAdapter(
    private val list: ArrayList<DeliveryBoy>,
    private val onClick: (DeliveryBoy) -> Unit
) : RecyclerView.Adapter<DeliveryAdapter.VH>() {

    class VH(view: View) : RecyclerView.ViewHolder(view) {
        val name = view.findViewById<TextView>(android.R.id.text1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_1, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {

        val item = list[position]

        // ✔️ SHOW NAME NOT ID
        holder.name.text = item.name ?: "No Name Found"

        holder.itemView.setOnClickListener {
            onClick(item)
        }
    }

    override fun getItemCount() = list.size
}