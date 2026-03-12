package com.example.adminspicybite

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.adminspicybite.adapter.DeliveryAdapter
import com.example.adminspicybite.databinding.ActivityOutForDeliveryBinding

class OutForDeliveryActivity : AppCompatActivity() {
    private val binding: ActivityOutForDeliveryBinding by lazy {
        ActivityOutForDeliveryBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        val customerNames = arrayListOf("John Doe", "Jane Smith", "Alice Johnson")
        val moneyStatus = arrayListOf("Received", "Not Received", "Pending")

        val adapter = DeliveryAdapter(customerNames, moneyStatus)
        binding.DeliveryRecycleView.adapter = adapter
        binding.DeliveryRecycleView.layoutManager = LinearLayoutManager(this)
        binding.backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

    }

}