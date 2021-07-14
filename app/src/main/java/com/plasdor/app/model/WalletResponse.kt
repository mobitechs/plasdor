package com.plasdor.app.model

data class WalletResponse(
    val Response: List<WalletPoint>,
    val code: Int,
    val status: Int
)

data class WalletPoint(
    val wallet: String,
    val referralPoints: String

)