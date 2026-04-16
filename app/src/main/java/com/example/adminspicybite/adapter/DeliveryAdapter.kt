package com.example.adminspicybite.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.adminspicybite.databinding.DeliveryItemBinding

class DeliveryAdapter(
    private val context: Context, private val customerNames: MutableList<String>,
    private val moneyStatus: MutableList<Boolean>
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
                if (moneyStatus[position]==true) {
                    statusmoney.text = "Received"
                } else {
                    statusmoney.text = "NotReceived"
                }
                val colorMap = mapOf(
                    true to Color.GREEN,
                    false to Color.RED,

                )
                statusmoney.setTextColor(colorMap[moneyStatus[position]]?: Color.BLACK)

                statusColor.backgroundTintList =
                    ColorStateList.valueOf(colorMap[moneyStatus[position]]?: Color.BLACK)


            }
        }
    }
}