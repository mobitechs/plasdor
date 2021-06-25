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
    val type: String,
    val description: String,
    var price: String,
    var priceToSell: String,
    val img: String,
    var _qty: Int = 1,
    var _qtyWisePrice: Int = 0

) : Parcelable