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

    val userId: String,
    val userType: String,
    val name: String,
    val email: String,
    val mobile: String,
    val address: String,
    val city: String,
    val pincode: String,
    val latitude: String,
    val longitude: String,
    val editedDate: String,
    val password: String,
    val otp: String,
    val isDeleted: String,
    val merchantId: String,

    val orderId: String,
    val addedDate: String,
    val pId: String,
    val productName: String,
    val type: String,
    val productPriceDaily: String,
    val productPriceHourly: String,
    val rentalType: String,
    val noOfDaysHours: String,
    val noOfController: String,
    val controllerCharges: String,
    val deliveryCharges: String,
    val discountedPrice: String,
    val transactionNo: String,
    val totalPrice: String,
    val paymentType: String,
    val paymentStatus: String,
    val redeemPointsUsed: String,
    val adminWillGet: String,
    val merchantWillGet: String,
    val orderStatus: String,
    val deliveredBy: String
) : Parcelable