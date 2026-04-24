package com.example.adminspicybite.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.adminspicybite.databinding.DeliveryItemBinding
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.adminspicybite.model.OrderModel

class OutForDeliveryAdapter(
    private val list: ArrayList<OrderModel>
) : RecyclerView.Adapter<OutForDeliveryAdapter.ViewHolder>() {

    class ViewHolder(val binding: DeliveryItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DeliveryItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val order = list[position]

        holder.binding.customername.text = order.userName ?: "No Name"

        val payment = order.paymentReceived ?: false

        if (payment) {
            holder.binding.statusmoney.text = "Received"
            holder.binding.statusmoney.setTextColor(Color.GREEN)
        } else {
            holder.binding.statusmoney.text = "Not Received"
            holder.binding.statusmoney.setTextColor(Color.RED)
        }

        val status = order.status ?: ""

        when (status) {
            "out_for_delivery" -> holder.binding.statusColor.setCardBackgroundColor(Color.CYAN)
            "Delivered" -> holder.binding.statusColor.setCardBackgroundColor(Color.GREEN)
            else -> holder.binding.statusColor.setCardBackgroundColor(Color.GRAY)
        }
    }
}