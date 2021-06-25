package com.plasdor.app.model

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize



@SuppressLint("ParcelCreator")
@Parcelize
data class HomeMenuItems(
    val menuId: String,
    val menuName: String,
    val img: Int

) : Parcelable