package com.example.adminspicybite

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.adminspicybite.adapter.DeliveryAdapter
import com.example.adminspicybite.adapter.PendingOrderAdapter
import com.example.adminspicybite.databinding.ActivityPendingOrderBinding
import com.example.adminspicybite.databinding.PendingOrdersItemBinding
import com.example.adminspicybite.model.OrderDetails
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PendingOrderActivity : AppCompatActivity(), PendingOrderAdapter.OnItemClicked {
    private lateinit var binding: ActivityPendingOrderBinding
    private var listOfName: MutableList<String> = mutableListOf()
    private var listOfTotalPrice: MutableList<String> = mutableListOf()
    private var listOfImageFirstOrder: MutableList<String> = mutableListOf()
    private var listOfOrderItem: ArrayList<OrderDetails> = arrayListOf()
    private lateinit var database: FirebaseDatabase
    private lateinit var databaseOrderDetails: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPendingOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //initialization of database
        database = FirebaseDatabase.getInstance()
        //initialisation of database reference
        databaseOrderDetails = database.reference.child("Order Details")

        //get data from firebase
        getOrderDetails()




        binding.backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

    }

    private fun getOrderDetails() {
        //retreive order detailds from firebase database
        databaseOrderDetails.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (orderSnapshot in snapshot.children) {
                    val orderDetails = orderSnapshot.getValue(OrderDetails::class.java)
                    orderDetails?.let {
                        listOfOrderItem.add(it)
                    }
                }
                addDataToListForRecyclerView()
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }


    private fun addDataToListForRecyclerView() {
        for (orderItem in listOfOrderItem) {
            //add data to respective list for populstiong the recycler view
            orderItem.userName?.let { listOfName.add(it) }
            orderItem.totalPrice?.let { listOfTotalPrice.add(it) }
            val firstImage = orderItem.foodImages?.firstOrNull()
            if (!firstImage.isNullOrEmpty()) {
                listOfImageFirstOrder.add(firstImage)
            }
        }
        setAdapter()
    }

    private fun setAdapter() {
        binding.pendingorderrecyclerview.layoutManager = LinearLayoutManager(this)
        val adapter =
            PendingOrderAdapter(this, listOfName, listOfTotalPrice, listOfImageFirstOrder, this)
        binding.pendingorderrecyclerview.adapter = adapter
    }


    override fun onItemClickListener(position: Int) {
        val intent = Intent(this, OrderDetailsActivity::class.java)
        val userOrderDetails = listOfOrderItem[position]
        intent.putExtra("UserOrderDetails", userOrderDetails)
        startActivity(intent)

    }

    override fun onItemAcceptClickListener(position: Int) {
        // handle item acceptance and update database
        val childItemPushKey = listOfOrderItem[position].itemPushKey
        val clickItemOrderReference = childItemPushKey?.let {
            database.reference.child("Order Details").child(it)
        }
        clickItemOrderReference?.child("orderAccepted")?.setValue(true)
        updateOrderAcceptStatus(position)
    }


    override fun onItemDispatchClickListener(position: Int) {
        // handle item dispatch and update database
        val dispatchItemPushKey = listOfOrderItem[position].itemPushKey!!

                val item = listOfOrderItem[position]

// ✅ 1. Move to CompletedOrder
        val completedRef = database.reference
            .child("CompletedOrder")
            .child(dispatchItemPushKey)

        item.orderAccepted = true
        item.paymentReceived = false

        completedRef.setValue(item).addOnSuccessListener {

                // 🔥🔥🔥 MOST IMPORTANT FIX
                val userRef = database.reference
                    .child("user")
                    .child(item.userUid!!)
                    .child("BuyHistory")
                    .child(dispatchItemPushKey)

                userRef.child("orderAccepted").setValue(true)
                userRef.child("paymentReceived").setValue(false)

                deleteThisItemFromOrderDetails(dispatchItemPushKey)
            }
        }


    private fun deleteThisItemFromOrderDetails(dispatchItemPushKey: String) {
            val orderDetailsItemReference= database.reference.child("Order Details").child(dispatchItemPushKey)
            orderDetailsItemReference.removeValue()
                .addOnSuccessListener {
                    Toast.makeText(this,"Order is Dispatch",Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this,"Order is not Dispatch",Toast.LENGTH_SHORT).show()
                }
    }

    private fun updateOrderAcceptStatus(position: Int) {
        //update order acceptance in user buyhistory and orderDetails
        val userIdOfClickedItem = listOfOrderItem[position].userUid
        val pushKeyOfClickedItem = listOfOrderItem[position].itemPushKey
        val buyHistoryReference =
            database.reference.child("user").child(userIdOfClickedItem!!).child("BuyHistory")
                .child(pushKeyOfClickedItem!!)
        buyHistoryReference.child("orderAccepted").setValue(true)
        databaseOrderDetails.child(pushKeyOfClickedItem).child("orderAccepted").setValue(true)


    }

}
