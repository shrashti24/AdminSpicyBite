package com.example.adminspicybite.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.adminspicybite.databinding.DeliveryItemBinding

class DeliveryAdapter(private val customerNames: ArrayList<String>,
                      private val moneyStatus: ArrayList<String>
): RecyclerView.Adapter<DeliveryAdapter.DeliveryViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DeliveryViewHolder {
       val binding= DeliveryItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return DeliveryViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: DeliveryViewHolder,
        position: Int
    ) {
        holder.bind(position)
    }

    override fun getItemCount(): Int =customerNames.size


    inner class DeliveryViewHolder (private val binding: DeliveryItemBinding):RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.apply {
                customername.text = customerNames[position]
                statusmoney.text = moneyStatus[position]
                val colorMap = mapOf(
                    "received" to Color.GREEN,
                    "not received" to Color.RED,
                    "Pending" to Color.GRAY
                )
                val status = moneyStatus[position].lowercase()

                val color = colorMap[status] ?: Color.BLACK

                statusmoney.setTextColor(color)

                statusColor.backgroundTintList =
                    ColorStateList.valueOf(color)


            }
        }
    }
}