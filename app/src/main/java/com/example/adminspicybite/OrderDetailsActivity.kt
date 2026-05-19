package com.example.adminspicybite

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.adminspicybite.adapter.OrderDetailsAdapter
import com.example.adminspicybite.databinding.ActivityOrderDetailsBinding
import com.example.adminspicybite.model.OrderModel

class OrderDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOrderDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val order =
            intent.getSerializableExtra("orderDetails") as? OrderModel

        binding.tvName.text = order?.userName
        binding.tvAddress.text = order?.address
        binding.tvPhone.text = order?.phoneNumber
        binding.tvAmount.text = order?.totalPrice
        binding.tvStatus.text = order?.status
        binding.tvDeliveryBoy.text = order?.deliveryBoyName

        val foodNames = order?.foodNames ?: arrayListOf()
        val foodPrices = order?.foodPrices ?: arrayListOf()
        val foodImages = order?.foodImages ?: arrayListOf()
        val foodQty = order?.foodQuantities ?: arrayListOf()

        binding.recyclerView.layoutManager =
            LinearLayoutManager(this)

        binding.recyclerView.adapter =
            OrderDetailsAdapter(
                foodNames,
                foodPrices,
                foodImages,
                foodQty
            )

        binding.backButton.setOnClickListener {
            finish()
        }
    }
}