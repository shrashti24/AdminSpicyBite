package com.example.adminspicybite

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.adminspicybite.adapter.PendingOrderAdapter
import com.example.adminspicybite.databinding.ActivityPendingOrderBinding
import com.example.adminspicybite.model.DeliveryBoy
import com.example.adminspicybite.model.OrderModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener

class PendingOrderActivity : AppCompatActivity(), PendingOrderAdapter.OnItemClicked {
    private lateinit var binding: ActivityPendingOrderBinding
    private var deliveryBoyList = ArrayList<DeliveryBoy>()
    private var selectedDeliveryBoyId: String? = null
    private lateinit var database: FirebaseDatabase
    private var listOfName: MutableList<String> = mutableListOf()
    private var listOfTotalPrice: MutableList<String> = mutableListOf()
    private var listOfImageFirstOrder: MutableList<String> = mutableListOf()
    private var listOfOrderItem: ArrayList<OrderModel> = arrayListOf()
    private lateinit var databaseOrderDetails: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPendingOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance()
        //initialisation of database reference
        databaseOrderDetails = database.reference.child("Orders")
        loadDeliveryBoys()
        //get data from firebase
        getOrderDetails()
        binding.backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

    }

    private fun loadDeliveryBoys() {

        FirebaseDatabase.getInstance().reference
            .child("DeliveryBoys")
            .addValueEventListener(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {

                    deliveryBoyList.clear()

                    for (snap in snapshot.children) {

                        val boy = snap.getValue(DeliveryBoy::class.java)

                        if (boy != null) {

                            // 🔥 VERY IMPORTANT: Firebase key as ID
                            boy.id = snap.key

                            deliveryBoyList.add(boy)
                        }
                    }

                    Log.d("BOY_SIZE", deliveryBoyList.size.toString())
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun getOrderDetails() {

        databaseOrderDetails
            .orderByChild("status")
            .equalTo("pending")
            .addValueEventListener(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {

                    listOfOrderItem.clear()

                    for (orderSnapshot in snapshot.children) {

                        val order = orderSnapshot.getValue(OrderModel::class.java)

                        if (order != null) {
                            order.itemPushKey = orderSnapshot.key
                            listOfOrderItem.add(order)
                        }
                    }

                    addDataToListForRecyclerView()
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun addDataToListForRecyclerView() {
        listOfName.clear()
        listOfTotalPrice.clear()
        listOfImageFirstOrder.clear()
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
        updateOrderAcceptStatus(position)
        val order = listOfOrderItem[position]
        val orderId = order.itemPushKey ?: return


        showDeliveryBoyDialog(orderId, position)
    }

    private fun showDeliveryBoyDialog(orderId: String, position: Int) {

        if (deliveryBoyList.isEmpty()) {
            Toast.makeText(this, "No Delivery Boys Found", Toast.LENGTH_SHORT).show()
            return
        }

        val names = deliveryBoyList.map { boy ->
            "${boy.name ?: "Unknown"} (Orders: ${boy.assignedOrders})"
        }.toTypedArray()

        val dialog = android.app.AlertDialog.Builder(this)
        dialog.setTitle("Select Delivery Boy")

        dialog.setItems(names) { _, which ->

            val selectedBoy = deliveryBoyList[which]

            if (selectedBoy.id == null) {
                Toast.makeText(this, "Invalid Boy", Toast.LENGTH_SHORT).show()
                return@setItems
            }

            assignOrder(orderId, selectedBoy.id!!, position)
        }

        dialog.show()
    }


    private fun assignOrder(orderId: String, deliveryBoyId: String, position: Int) {
        val boyRef = FirebaseDatabase.getInstance().reference
            .child("DeliveryBoys")
            .child(deliveryBoyId)

        boyRef.child("activeDrops").get().addOnSuccessListener { snapshot ->

            val activeDrops = snapshot.getValue(Int::class.java) ?: 0

            if (activeDrops >= 1) {
                Toast.makeText(this, "Delivery Boy already busy 🚫", Toast.LENGTH_SHORT).show()
                return@addOnSuccessListener

            }
            val updates = hashMapOf<String, Any>(
                "status" to "assigned",
                "assignedTo" to deliveryBoyId
            )

            FirebaseDatabase.getInstance().reference
                .child("Orders")
                .child(orderId)
                .updateChildren(updates)

            // counters update
            val boyRef = FirebaseDatabase.getInstance().reference
                .child("DeliveryBoys")
                .child(deliveryBoyId)

            boyRef.child("assignedOrders")
                .setValue(ServerValue.increment(1))

            boyRef.child("activeDrops")
                .setValue(ServerValue.increment(1))

            listOfOrderItem.removeAt(position)
            setAdapter()

            Toast.makeText(this, "Order Assigned ✅", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onItemDispatchClickListener(position: Int) {

        val order = listOfOrderItem[position]
        val orderId = order.itemPushKey ?: return
        val orderRef = FirebaseDatabase.getInstance()
            .reference.child("Orders")
            .child(orderId)

        // 1. FIRST update status to OUT_FOR_DELIVERY
        val updates = hashMapOf<String, Any>(
            "status" to "out_for_delivery"
        )
        orderRef.updateChildren(updates)
//        // 2. Build completed order object
//        val orderMap = HashMap<String, Any>()
//        orderMap["orderId"] = orderId
//        orderMap["customerName"] = order.userName ?: ""
//        orderMap["address"] = order.address ?: ""
//        orderMap["phone"] = order.phoneNumber ?: ""
//        orderMap["totalAmount"] = order.totalPrice?.replace("₹", "")?.toIntOrNull() ?: 0
//        orderMap["payment"] = "COD"
//        orderMap["status"] = "out_for_delivery"
//
//        // 3. Get assigned delivery boy safely
//        orderRef.child("assignedTo").get()
//            .addOnSuccessListener { snap ->
//
//                orderMap["assignedTo"] = snap.value ?: ""
//
//                // 4. Save to CompletedOrder ONLY IF delivered (optional later)
//                FirebaseDatabase.getInstance()
//                    .reference.child("CompletedOrder")
//                    .child(orderId)
//                    .setValue(orderMap)
//            }


        // update user history
        order.userUid?.let { uid ->
            database.reference
                .child("user")
                .child(uid)
                .child("BuyHistory")
                .child(orderId)
                .child("paymentReceived")
                .setValue(false)
        }

        statusUpdateOrderDetails(orderId)

        Toast.makeText(this, "Order sent for delivery 🚚", Toast.LENGTH_SHORT).show()
    }

    private fun statusUpdateOrderDetails(dispatchItemPushKey: String) {

        val orderRef =
            FirebaseDatabase.getInstance()
                .reference
                .child("Orders")
                .child(dispatchItemPushKey)

        val updates = hashMapOf<String, Any>(
            "status" to "out_for_delivery"
        )

        orderRef.updateChildren(updates)
            .addOnSuccessListener {
                Toast.makeText(this, "Order is Dispatch", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Order is not Dispatch", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateOrderAcceptStatus(position: Int) {

        val userIdOfClickedItem = listOfOrderItem[position].userUid
        val pushKeyOfClickedItem = listOfOrderItem[position].itemPushKey

        val buyHistoryReference =
            FirebaseDatabase.getInstance().reference
                .child("user")
                .child(userIdOfClickedItem!!)
                .child("BuyHistory")
                .child(pushKeyOfClickedItem!!)

        buyHistoryReference.child("orderAccepted").setValue(true)

        FirebaseDatabase.getInstance().reference
            .child("Orders")
            .child(pushKeyOfClickedItem)
            .child("orderAccepted")
            .setValue(true)
    }
}