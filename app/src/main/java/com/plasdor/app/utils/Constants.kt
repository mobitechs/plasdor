package  com.plasdor.app.utils

class Constants {

    companion object {

        const val clientBusinessId = "1"
        const val BASE_URL = "https://mobitechs.in/plasdor/api/plasdor.php"

        const val PROJECT_NAME = "Plasdor"
        const val USERDATA = "userData"
        const val ADMIN_EMAIL = "plasdor@gmail.com"
        const val ADMIN_MOBILE = "9876543210"
        const val ADMIN = "Admin"
        const val MERCHANT = "Merchant"
        const val USER = "User"
        const val REFERRAL_POINTS = 101

        const val ISLOGIN = "isLogin"
        const val userId = "userId"
        const val userType = "userType"
        const val userName = "userName"
        const val mobileNo = "mobileNo"
        const val EMAIL = "email"
        const val orderItemMsg = "orderItemMsg"
        const val orderTotalAmount = "orderTotalAmount"
        const val deliveryCharges = 100
        const val deliveryChargesBelow = 500
        const val CartList = "CartList"
        const val CartList2 = "CartList2"
        const val AllProductList = "AllProductList"
        const val tab1List = "tab1List"
        const val tab2List = "tab2List"
        const val tab3List = "tab3List"
        const val finalOrderMsg = "finalOrderMsg"
        const val totalDiscountedAmount = "totalDiscountedAmount"
        const val delCharges = "delCharges"
        const val totalAmount = "totalAmount"

        val weightArray = arrayOf(
            "250 grams",
            "500 grams",
            "750 grams",
            "1 kg",
            "1.5 kg",
            "2 kg",
            "2.5 kg ",
            "3 kg",
            "3.5 kg",
            "4 kg",
            "4.5 kg",
            "5 kg"
        )

        //if u add new item in weightArray then please add in qtyArray as well its must
        val qtyArray = arrayOf("1", "2", "3", "4", "6", "8 ", "10", "12", "14", "16", "18", "20")

        val pieceArray = arrayOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12")
        val unitTypeArray = arrayOf("Grams", "Piece", "Packet", "Dozen", "Bunch", "Stick")
        val orderStatusArray = arrayOf("Received", "Accepted", "Placed", "Completed")
        val categoryArray = arrayOf("Vegetables", "Exotic Vegetables", "Fruits")


    }
}