package  com.plasdor.app.utils

class Constants {

    companion object {

        const val clientBusinessId = "1"
//        const val BASE_URL = "https://mobitechs.in/plasdor/api/plasdor.php"
        const val BASE_URL = "http://plasdorservices.in/plasdorApi/api/plasdor.php"
        const val TNC = "http://plasdorservices.in/tnc.html"
        const val Privacy = "http://plasdorservices.in/privacy.html"

        const val PROJECT_NAME = "Plasdor"
        const val USERDATA = "userData"
        const val ADMIN_EMAIL = "plasdor@gmail.com"
        const val ADMIN_MOBILE = "9876543210"
        const val SELF = "Self"
        const val ADMIN = "Admin"
        const val COMPANY = "Company"
        const val MERCHANT = "Merchant"
        const val USER = "User"
        const val DELIVERY_AGENT = "DeliveryAgent"
        const val POINTS_ON_SHARE = 15
        const val POINTS_ON_ADS    = 2
        const val POINTS_ON_ORDER= 20
        const val GET_REWARD_COUNTER= "getRewardCounter"
        const val GET_REWARD_DATE= "getRewardDate"
        const val LAST_REWARD_TIME= "lastRewardTime"
        const val EARNED_POINTS= "earnedPoints"
        const val FIRST_FREE_ORDER_COMPLETE= "firstFreeOrderComplete"
        const val REWARD_LIMIT= 5
        const val NEXT_REWARD_TIME= 20

        const val IS_TOKEN_SAVE_API_CALLED = "isTokenSAveAPICalled"
        const val IS_TOKEN_UPDATE = "isTokenUpdate"
        const val TOKEN = "token"
        const val DEVICE_ID = "deviceId"
        const val ISLOGIN = "isLogin"
        const val userId = "userId"
        const val userType = "userType"
        const val userName = "userName"
        const val mobileNo = "mobileNo"
        const val EMAIL = "email"
        const val orderItemMsg = "orderItemMsg"
        const val orderTotalAmount = "orderTotalAmount"
        const val deliveryCharges = 49
        const val deliveryChargesBelow = 500
        const val CartList = "CartList"
        const val CartList2 = "CartList2"
        const val AllProductList = "AllProductList"
        const val finalOrderMsg = "finalOrderMsg"
        const val totalDiscountedAmount = "totalDiscountedAmount"
        const val delChargesNormal = 40
        const val delCharges1Hour = 80
        const val percentBetweenMerchantNPlasdor = 50
        const val totalAmount = "totalAmount"
        const val Hourly = "Hourly"
        const val Daily = "Daily"
        const val selfPickup = "Self PickUp"
        const val deliveryByCompany = "Delivery"
        const val First_Order = "First Order"
        const val Order_ID_INITIAL = "ORD100"

        //if u add new item in weightArray then please add in qtyArray as well its must
        val qtyArray = arrayOf("1", "2", "3", "4", "6", "8 ", "10", "12", "14", "16", "18", "20")

        val controllerQtyArray = arrayOf("1", "2", "3", "4")
        val daysArray = arrayOf("1", "2")
        val hourArray = arrayOf("1","2", "4", "6","12")

        val ps5NdXSeriesXPriceArray = arrayOf("649", "999", "2499", "3199", "3499", "3999", "4499")
        val ps4NdXOneXPriceArray = arrayOf("499", "899", "1399", "1699", "2099", "2399", "2699")
        val XOneSPriceArray = arrayOf("449", "849", "1349", "1649", "2049", "2349", "2649")
        val XSeriesSPriceArray = arrayOf("499", "899", "1399", "1899", "2399", "2899", "3399")

        val ps4NdXOneXNdSPriceArrayHr = arrayOf("99","199", "299", "399","449")
        val ps5NdXSeriesXNdSPriceArrayHr = arrayOf("99","199", "299", "399", "499")
        val xboxSeriesSPriceArrayHr = arrayOf("79","149", "249", "349","449")
        val xboxOneSPriceArrayHr = arrayOf("79","149", "249", "349","399")

        val orderStatusArray = arrayOf("Pending", "Complete")


    }
}


