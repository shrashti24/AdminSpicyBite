package com.example.adminspicybite.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
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

        // ✅ Full Name Logic
        val fullName = when {

            !item.name.isNullOrEmpty() ->
                item.name

            else -> listOf(
                item.firstName,
                item.middleName,
                item.lastName
            )
                .filter { !it.isNullOrEmpty() }
                .joinToString(" ")
        }


        // ✅ Show Name
        holder.name.text =
            if (!fullName.isNullOrEmpty())
                fullName
            else
                "No Name Found"

        // ✅ Click
        holder.itemView.setOnClickListener {
            onClick(item)
        }
    }

    override fun getItemCount() = list.size
}