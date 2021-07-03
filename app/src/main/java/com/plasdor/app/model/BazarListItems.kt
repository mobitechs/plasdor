package com.plasdor.app.model

import android.annotation.SuppressLint
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@SuppressLint("ParcelCreator")
@Parcelize
data class BazarListItemsResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("Response")
    val productListItems: List<ProductListItems>,
    @SerializedName("status")
    val status: Int
) : Parcelable


@SuppressLint("ParcelCreator")
@Parcelize
data class BazarListItems(
    val bazarId: String,
    val productId: String,
    val forFreePoints: String,
    val oneDayPoints: String,
    val threeDayPoints: String,
    val fiveDayPoints: String,
    val pId: String,
    val productName: String,
    val type: String,
    val description: String,
    var price: String,
    var priceToSell: String,
    val img: String

) : Parcelable