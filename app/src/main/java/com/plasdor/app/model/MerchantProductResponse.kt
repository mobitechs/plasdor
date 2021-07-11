package com.plasdor.app.model

import android.annotation.SuppressLint
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@SuppressLint("ParcelCreator")
@Parcelize
data class MerchantProductResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("Response")
    val mProductListItems: List<MerchantProductListItems>,
    @SerializedName("status")
    val status: Int
) : Parcelable


@SuppressLint("ParcelCreator")
@Parcelize
data class MerchantProductListItems(
    val pId: String,
    val productName: String,
    val type: String,
    val description: String,
    val priceToShowDaily: String,
    val priceToSellDaily: String,
    val img: String,
    var isAdded: String,
    var totalQty: String,
    var totalControllerQty: String,
    var remainingControllerQty: String,
    var soldQty: String,
    var isSold: String

) : Parcelable