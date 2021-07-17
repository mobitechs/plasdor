package com.plasdor.app.model


import android.annotation.SuppressLint
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@SuppressLint("ParcelCreator")
@Parcelize
data class ProductListItemsResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("Response")
    val productListItems: List<ProductListItems>,
    @SerializedName("status")
    val status: Int
) : Parcelable


@SuppressLint("ParcelCreator")
@Parcelize
data class ProductListItems(
    val pId: String,
    val productName: String,
   // val type: String,
    val description: String,
    var priceToShowDaily: String,
    var priceToSellDaily: String,
    var priceToShowHourly: String,
    var priceToSellHourly: String,
    val img: String,
    var qty: Int = 1,
    var qtyWisePrice: Int = 0,
    var controllerQty: Int = 1,
    var controllerCharges: String,
    var discountedPrice: String,
    var totalPayable: Int = 0,
    val forFreePoints: String?,
    val oneDayPoints: String?,
    val threeDayPoints: String?,
    val fiveDayPoints: String?
) : Parcelable