package com.plasdor.app.model

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

data class MyOrderResponse(
    val Response: List<MyOrderListItems>,
    val code: Int,
    val status: Int
)

@SuppressLint("ParcelCreator")
@Parcelize
data class MyOrderListItems(
    val addedDate: String,
    val address: String,
    val city: String,
    val deliveryCharges: String,
    val discountedPrice: String,
    val editedDate: String,
    val email: String,
    val isDeleted: String,
    val latitude: String,
    val longitude: String,
    val merchantId: String,
    val mobile: String,
    val name: String,
    val noOfDays: String,
    val noOfController: String,
    val controllerCharges: String,
    val orderId: String,
    val otp: String,
    val pId: String,
    val password: String,
    val pincode: String,
    val productName: String,
    val productPrice: String,
    val totalPrice: String,
    val type: String,
    val userId: String,
    val userType: String
) : Parcelable