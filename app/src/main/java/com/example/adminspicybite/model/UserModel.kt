package com.example.adminspicybite.model

data class UserModel(

    var firstName: String = "",
    var middleName: String = "",
    var lastName: String = "",

    var userName: String = "",

    var nameOfRestaurant: String = "",

    var email: String = "",

    var address: String = "",

    var phone: String = "",

    var role: String? = null
)