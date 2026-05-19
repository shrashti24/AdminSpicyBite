package com.example.adminspicybite

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.adminspicybite.adapter.OutForDeliveryAdapter
import com.example.adminspicybite.databinding.ActivityCompletedOrderBinding
import com.example.adminspicybite.model.OrderModel
import com.google.firebase.database.*

class CompletedOrderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCompletedOrderBinding

    private var completedList: ArrayList<OrderModel> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCompletedOrderBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.backButton.setOnClickListener {
            finish()
        }

        getCompletedOrders()
    }

    private fun getCompletedOrders() {

        FirebaseDatabase.getInstance().reference
            .child("CompletedOrder")
            .addValueEventListener(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {

                    completedList.clear()

                    for (snap in snapshot.children) {

                        val order =
                            snap.getValue(OrderModel::class.java)

                        if (order != null) {

                            completedList.add(order)
                        }
                    }

                    completedList.reverse()

                    setAdapter()
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun setAdapter() {

        binding.recyclerView.layoutManager =
            LinearLayoutManager(this)

        binding.recyclerView.adapter =
            OutForDeliveryAdapter(completedList)
    }
}