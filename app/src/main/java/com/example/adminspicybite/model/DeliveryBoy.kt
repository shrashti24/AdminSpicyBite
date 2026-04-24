package com.example.adminspicybite.model

data class DeliveryBoy(
    var id: String? = null,
    var name: String? = null,
    var phone: String? = null,
    var email: String? = null,
    var password: String? = null,
    var activeDrops: Int = 0,
    var assignedOrders: Int = 0,
    var codEnabled: Boolean = true
)