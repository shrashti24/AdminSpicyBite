package com.example.adminspicybite.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.adminspicybite.databinding.PendingOrdersItemBinding

class PendingOrderAdapter(
    private val customerNames: ArrayList<String>,
    private val quantity: ArrayList<String>,
    private val foodImage: ArrayList<Int>,
    private val context: Context
) : RecyclerView.Adapter<PendingOrderAdapter.PendingOrderViewHolder>() {

    // State list to track accepted items
    private val acceptedList = MutableList(customerNames.size) { false }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PendingOrderViewHolder {
        val binding = PendingOrdersItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return PendingOrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PendingOrderViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = customerNames.size

    inner class PendingOrderViewHolder(
        private val binding: PendingOrdersItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) {

            binding.customerName.text = customerNames[position]
            binding.pendingorderquantity.text = quantity[position]
            binding.orderedfoodImage.setImageResource(foodImage[position])

            // Set correct button text
            binding.acceptButton.text =
                if (acceptedList[position]) "Dispatch" else "Accept"

            binding.acceptButton.setOnClickListener {

                if (!acceptedList[position]) {
                    acceptedList[position] = true
                    notifyItemChanged(position)
                    showToast("Order is Accepted")

                } else {
                    // Remove from all lists
                    customerNames.removeAt(position)
                    quantity.removeAt(position)
                    foodImage.removeAt(position)
                    acceptedList.removeAt(position)

                    notifyItemRemoved(position)
                    showToast("Order is Dispatch")
                }
            }
        }

        private fun showToast(message: String) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }
}