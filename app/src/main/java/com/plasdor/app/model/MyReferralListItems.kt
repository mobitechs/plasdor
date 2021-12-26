package com.plasdor.app.model

data class MyReferralListItems(
    val Response: List<ReferralList>,
    val code: Int,
    val status: Int
)

data class ReferralList(
    val addedDate: String,
    val address: String,
    val city: String,
    val email: String,
    val installerUserId: String,
    val mobile: String,
    val name: String,
    val pincode: String,
    val referralDetalsId: String,
    val referralPoints: String,
    val referralType: String,
    val senderUserId: String
)