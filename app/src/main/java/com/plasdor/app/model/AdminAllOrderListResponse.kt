package com.plasdor.app.model

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


data class AdminAllOrderListResponse(
    val Response: List<AdminAllOrderListItems>,
    val code: Int,
    val status: Int
)

@SuppressLint("ParcelCreator")
@Parcelize
data class AdminAllOrderListItems(
    val addedDate: String,
    val address: String,
    val city: String,
    val deliveryChages: String,
    val discount: String,
    val email: String,
    val merchantAddress: String,
    val merchantCity: String,
    val merchantEmail: String,
    val merchantId: String,
    val merchantMobile: String,
    val merchantName: String,
    val merchantPinCode: String,
    val mobile: String,
    val name: String,
    val noOfDays: String,
    val orderId: String,
    val pId: String,
    val pincode: String,
    val productName: String,
    val productPrice: String,
    val totalPrice: String,
    val type: String,
    val userId: String
) : Parcelable