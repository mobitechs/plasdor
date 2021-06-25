package com.plasdor.app.model

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

data class AddressListResponse(
    val Response: List<AddressListItems>,
    val code: Int,
    val status: Int
)

@SuppressLint("ParcelCreator")
@Parcelize
data class AddressListItems(
    val address: String,
    val addressId: String,
    val area: String,
    val city: String,
    val createdDate: String,
    val editedBy: String,
    val editedDate: String,
    val landMark: String,
    val pincode: String,
    val userId: String
) : Parcelable