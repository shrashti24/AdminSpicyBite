package com.example.adminspicybite.model

import android.annotation.SuppressLint
import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

@SuppressLint("ParcelCreator")
class OrderDetails: Serializable {
    var userUid: String? = null
    var userName: String? = null
    var foodNames: ArrayList<String>? = null
    var foodImages: ArrayList<String>? = null
    var foodPrices: ArrayList<String>? = null
    var foodQuantities: ArrayList<Int>? = null
    var address: String? = null
    var totalPrice: String? = null
    var phoneNumber: String? = null
    var orderAccepted: Boolean = false
    var paymentReceived: Boolean = false
    var itemPushKey: String? = null
    var currentTime: Long = 0

     fun describeContents(): Int {
        TODO("Not yet implemented")
    }

    fun writeToParcel(dest: Parcel, flags: Int) {
        TODO("Not yet implemented")
    }
}