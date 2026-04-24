package com.example.adminspicybite

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.adminspicybite.adapter.DeliveryAdapter
import com.example.adminspicybite.adapter.OutForDeliveryAdapter
import com.example.adminspicybite.databinding.ActivityOutForDeliveryBinding
import com.example.adminspicybite.model.OrderModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class OutForDeliveryActivity : AppCompatActivity() {
    private val binding: ActivityOutForDeliveryBinding by lazy {
        ActivityOutForDeliveryBinding.inflate(layoutInflater)
    }
    private lateinit var database: FirebaseDatabase
    private var listOfCompleteOrderList: ArrayList<OrderModel> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        binding.backButton.setOnClickListener {
            finish()
        }
        retrieveOutForDeliveryOrders() //

    }

    private fun retrieveOutForDeliveryOrders() {


        val ref = FirebaseDatabase.getInstance().reference.child("Orders")

        ref.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                listOfCompleteOrderList.clear()

                for (orderSnapshot in snapshot.children) {

                    val order = orderSnapshot.getValue(OrderModel::class.java)

                    if (order != null) {
                        listOfCompleteOrderList.add(order)
                    }
                }

                listOfCompleteOrderList.reverse()
                setDataIntoRecyclerView()
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
    private fun setDataIntoRecyclerView() {

        binding.DeliveryRecycleView.layoutManager =
            LinearLayoutManager(this)

        val adapter = OutForDeliveryAdapter(listOfCompleteOrderList)

        binding.DeliveryRecycleView.adapter = adapter
    }

}