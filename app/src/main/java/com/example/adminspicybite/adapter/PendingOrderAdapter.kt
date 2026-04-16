package com.example.adminspicybite.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.adminspicybite.databinding.PendingOrdersItemBinding

class PendingOrderAdapter(
    private val context: Context,
    private val customerNames:MutableList<String>,
    private val quantity: MutableList<String>,
    private val foodImage: MutableList<String>,
    private val itemClicked: OnItemClicked
) : RecyclerView.Adapter<PendingOrderAdapter.PendingOrderViewHolder>() {

    interface OnItemClicked{
        fun onItemClickListener(position: Int)
        fun onItemAcceptClickListener(position: Int)
        fun onItemDispatchClickListener(position: Int)



    }
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
            Glide.with(binding.orderedfoodImage.context)
                .load(foodImage[position])
                .into(binding.orderedfoodImage)

            // Set correct button text
            binding.acceptButton.text =
                if (acceptedList[position]) "Dispatch" else "Accept"

            binding.acceptButton.setOnClickListener {

                if (!acceptedList[position]) {
                    acceptedList[position] = true
                    notifyItemChanged(position)
                    showToast("Order is Accepted")
                    itemClicked.onItemAcceptClickListener(position)
                } else {
                    // Remove from all lists
                    customerNames.removeAt(position)
                    quantity.removeAt(position)
                    foodImage.removeAt(position)
                    acceptedList.removeAt(position)

                    notifyItemRemoved(position)
                    showToast("Order is Dispatch")
                    itemClicked.onItemDispatchClickListener(position)
                }
            }
            itemView.setOnClickListener {
                itemClicked.onItemClickListener(position)
            }
        }

        private fun showToast(message: String) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }
}