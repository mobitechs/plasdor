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
    // user Details who's order
    val userId: String,
    val name: String,
    val mobile: String,
    val email: String,
    val address: String,
    val city: String,
    val pincode: String,
    val userLat: String,
    val userLon: String,
    //Merchant Details who took order
    val merchantId: String,
    val merchantName: String,
    val merchantMobile: String,
    val merchantEmail: String,
    val merchantAddress: String,
    val merchantCity: String,
    val merchantPinCode: String,
    //order Details
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