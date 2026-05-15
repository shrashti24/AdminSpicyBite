package com.example.adminspicybite.model

data class AllMenu(

    var key: String? = null,

    var foodName: String? = null,

    var foodPrice: String? = null,

    var foodCategory: String? = null,

    var foodDescription: String? = null,

    var foodIngrediant: String? = null,

    var foodImage: String? = null,

    var itemAvailable: Boolean = true,
    var isEditing: Boolean = false
)