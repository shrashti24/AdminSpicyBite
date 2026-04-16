package com.example.adminspicybite

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.adminspicybite.adapter.DeliveryAdapter
import com.example.adminspicybite.databinding.ActivityOutForDeliveryBinding
import com.example.adminspicybite.model.OrderDetails
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class OutForDeliveryActivity : AppCompatActivity() {
    private val binding: ActivityOutForDeliveryBinding by lazy {
        ActivityOutForDeliveryBinding.inflate(layoutInflater)
    }
    private lateinit var database: FirebaseDatabase
    private var listOfCompleteOrderList: ArrayList<OrderDetails> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        binding.backButton.setOnClickListener {
            finish()
        }

        //retrieve and display compltetd order
        retrieveCompleteOrderDetail()


    }

    private fun retrieveCompleteOrderDetail() {
        //initialise firebase database
        database = FirebaseDatabase.getInstance()
        val completeOrderReference = database.reference.child("CompletedOrder")
            .orderByChild("currentTime")
        completeOrderReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listOfCompleteOrderList.clear()
                for (orderSnapshot in snapshot.children) {
                    val completeOrder = orderSnapshot.getValue(OrderDetails::class.java)
                    completeOrder?.let {
                        listOfCompleteOrderList.add(it)
                    }
                }

                //reverse the list to display latest order first
                listOfCompleteOrderList.reverse()

                setDataIntoRecyclerView()
            }


            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun setDataIntoRecyclerView() {
        //initialisation list to hold cuxtomer name and payment status
        val customerNames = mutableListOf<String>()
        val moneyStatus = mutableListOf<Boolean>()
        for (order: OrderDetails in listOfCompleteOrderList) {
            order.userName?.let {
                customerNames.add(it) }
            moneyStatus.add(order.paymentReceived)
        }
        val adapter = DeliveryAdapter(this, customerNames, moneyStatus)
        binding.DeliveryRecycleView.adapter = adapter
        binding.DeliveryRecycleView.layoutManager = LinearLayoutManager(this)


    }

}