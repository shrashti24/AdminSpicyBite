package com.example.adminspicybite.model

data class DeliveryBoy(

    var id: String? = null,

    // ✅ Name fields
    var firstName: String? = null,
    var middleName: String? = null,
    var lastName: String? = null,

    // ✅ Full Name
    var name: String? = null,

    // ✅ Contact
    var phone: String? = null,
    var email: String? = null,


    // ✅ Delivery details
    var activeDrops: Int = 0,
    var assignedOrders: Int = 0,
    var deliveredOrders: Int = 0,

    // ✅ Availability
    var isAvailable: Boolean = true,
    var codEnabled: Boolean = true,

    // ✅ Role
    var role: String? = "deliveryBoy",

    // ✅ Optional future fields
    var vehicleNumber: String? = null,
    var vehicleType: String? = null,
    var drivingLicense: String? = null
)