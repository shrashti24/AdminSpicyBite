package com.example.adminspicybite

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.adminspicybite.adapter.DeliveryAdapter
import com.example.adminspicybite.adapter.PendingOrderAdapter
import com.example.adminspicybite.databinding.ActivityPendingOrderBinding
import com.example.adminspicybite.databinding.PendingOrdersItemBinding

class PendingOrderActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPendingOrderBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityPendingOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val orderedCustomerNames = arrayListOf("John Doe", "Jane Smith", "Alice Johnson")
        val orderedQuantity = arrayListOf("8", "6", "5")
        val orderedFoodImage=arrayListOf(R.drawable.menu1,R.drawable.menu2,R.drawable.menu3)
        val adapter = PendingOrderAdapter(orderedCustomerNames, orderedQuantity,orderedFoodImage,this)
        binding.pendingorderrecyclerview.adapter = adapter
        binding.pendingorderrecyclerview.layoutManager = LinearLayoutManager(this)
        binding.backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

    }
}