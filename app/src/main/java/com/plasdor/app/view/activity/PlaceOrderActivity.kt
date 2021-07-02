package com.plasdor.app.view.activity

import android.app.Activity
import android.graphics.Paint
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.plasdor.app.R
import com.plasdor.app.callbacks.ApiResponse
import com.plasdor.app.model.AvailableMerchantListItem
import com.plasdor.app.model.ProductListItems
import com.plasdor.app.session.SharePreferenceManager
import com.plasdor.app.utils.*
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import kotlinx.android.synthetic.main.activity_place_order.*
import kotlinx.android.synthetic.main.adpater_item_merchant_available.*
import kotlinx.android.synthetic.main.loader.*
import org.json.JSONException
import org.json.JSONObject

class PlaceOrderActivity : AppCompatActivity(), ApiResponse, PaymentResultListener {


    lateinit var merchantItem: AvailableMerchantListItem
    lateinit var listItem: ProductListItems

//    lateinit var rootView: View
//    lateinit var btnPlaceOrder: AppCompatTextView
//    lateinit var ivProdImage: AppCompatImageView
//    lateinit var tvProductName: AppCompatTextView
//    lateinit var txtType: AppCompatTextView
//    lateinit var txtPrice: AppCompatTextView
//    lateinit var txtPriceToSell: AppCompatTextView
//    lateinit var txtNoOfDays: AppCompatTextView
//    lateinit var txtControllerQty: AppCompatTextView
//    lateinit var txtControllerCharges: AppCompatTextView
//    lateinit var txtTotalPayable: AppCompatTextView
//
//
//    lateinit var txtUAddress: AppCompatTextView
//    lateinit var txtUCity: AppCompatTextView
//    lateinit var txtUPinCode: AppCompatTextView
//
//    lateinit var imgRadioBtn: AppCompatImageView
//    lateinit var txtName: AppCompatTextView
//    lateinit var txtMobile: AppCompatTextView
//    lateinit var txtEmail: AppCompatTextView
//    lateinit var txtAddress: AppCompatTextView
//    lateinit var txtCity: AppCompatTextView
//    lateinit var txtPinCode: AppCompatTextView
//    lateinit var layoutLoader: RelativeLayout

    var productId = ""
    var merchantId = ""
    var userId = ""
    var userEmail = ""
    var userMobile = ""
    var address = ""
    var city = ""
    var pinCode = ""

    var transactionNo = ""
    var amount = "0"
    var fAmount = 0
    var paymentType = ""
    var paymentStatus = ""
    var note = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place_order)
        val window: Window = window
        setStatusColor(window, resources.getColor(R.color.colorPrimaryDark))

        initView()
    }

//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        // Inflate the layout for this fragment
//        rootView = inflater.inflate(R.layout.fragment_place_order, container, false)
//        initView()
//        return rootView
//    }

    private fun initView() {
        Checkout.preload(applicationContext)
        userId = SharePreferenceManager.getInstance(this).getUserLogin(Constants.USERDATA)?.get(0)?.userId.toString()
        userEmail = SharePreferenceManager.getInstance(this).getUserLogin(Constants.USERDATA)?.get(0)?.email.toString()
        userMobile = SharePreferenceManager.getInstance(this).getUserLogin(Constants.USERDATA)?.get(0)?.mobile.toString()
        address =
            SharePreferenceManager.getInstance(this).getUserLogin(Constants.USERDATA)
                ?.get(0)?.address.toString()
        city = SharePreferenceManager.getInstance(this).getUserLogin(Constants.USERDATA)
            ?.get(0)?.city.toString()
        pinCode =
            SharePreferenceManager.getInstance(this).getUserLogin(Constants.USERDATA)
                ?.get(0)?.pincode.toString()

//        ivProdImage = rootView.findViewById(R.id.ivProdImage)
//        tvProductName = rootView.findViewById(R.id.tvProductName)
//        txtType = rootView.findViewById(R.id.txtType)
//        txtPrice = rootView.findViewById(R.id.txtPrice)
//        txtPriceToSell = rootView.findViewById(R.id.txtPriceToSell)
//        txtNoOfDays = rootView.findViewById(R.id.txtNoOfDays)
//        txtControllerQty = rootView.findViewById(R.id.txtControllerQty)
//        txtControllerCharges = rootView.findViewById(R.id.txtControllerCharges)
//        txtTotalPayable = rootView.findViewById(R.id.txtTotalPayable)
//
//        txtUAddress = rootView.findViewById(R.id.txtUAddress)
//        txtUCity = rootView.findViewById(R.id.txtUCity)
//        txtUPinCode = rootView.findViewById(R.id.txtUPinCode)
//
//
//        imgRadioBtn = rootView.findViewById(R.id.imgRadioBtn)
//        txtName = rootView.findViewById(R.id.txtName)
//        txtMobile = rootView.findViewById(R.id.txtMobile)
//        txtEmail = rootView.findViewById(R.id.txtEmail)
//        txtAddress = rootView.findViewById(R.id.txtAddress)
//        txtCity = rootView.findViewById(R.id.txtCity)
//        txtPinCode = rootView.findViewById(R.id.txtPinCode)
//
//        btnPlaceOrder = rootView.findViewById(R.id.btnPlaceOrder)
//        layoutLoader = rootView.findViewById(R.id.layoutLoader)
        listItem = intent.getParcelableExtra("productItem")!!
        merchantItem = intent.getParcelableExtra("merchantItem")!!

//        listItem = arguments?.getParcelable("productItem")!!
//        merchantItem = arguments?.getParcelable("merchantItem")!!
//        qty = arguments?.getInt("noOfDays")!!
//        controllerQty = arguments?.getInt("controllerQty")!!
//        controllerCharges = arguments?.getInt("controllerCharges")!!
//        totalPayableAmount = arguments?.getInt("totalPayableAmount")!!

        productId = listItem.pId
        ivProdImage.setImage(listItem.img!!)

        tvProductName.text = listItem.productName


        txtPrice.text = "Rs. " + (listItem._qty * listItem.price.toInt()).toString()
        txtPriceToSell.text = "Rs. " + listItem._qtyWisePrice
        txtType.text = "Type " + listItem.type
        txtNoOfDays.text = listItem._qty.toString()

        txtControllerQty.text = listItem.controllerQty.toString()
        txtControllerCharges.text = listItem.controllerCharges.toString()
        txtTotalPayable.text = listItem.totalPayable.toString()

        txtPrice.setPaintFlags(txtPrice.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG)

        txtUAddress.text = address
        txtUCity.text = city
        txtUPinCode.text = pinCode

        merchantId = merchantItem.userId
        txtName.text = merchantItem.name
        txtMobile.text = merchantItem.mobile
        txtEmail.text = merchantItem.email
        txtAddress.text = merchantItem.address
        txtCity.text = merchantItem.city
        txtPinCode.text = merchantItem.pincode

        imgRadioBtn.visibility = View.GONE

        btnPlaceOrder.setOnClickListener {
            transactionNo = "TID" + System.currentTimeMillis()
            amount = listItem.totalPayable.toString()
            note = "Payment for Plasdor Services."
            //payUsingUpi(amount, upiId, name, note)
            razorPayGateway()
        }
    }

    private fun razorPayGateway() {

        val activity: Activity = this
        val co = Checkout()
        //test key
             co.setKeyID("rzp_test_VNPFueei0vUGKm\n")
        //live key
//        co.setKeyID("rzp_test_VNPFueei0vUGKm")

        try {
            // amount = "1"
            fAmount = Math.round(amount.toFloat() * 100)
            val options = JSONObject()
            options.put("name", "Plasdor Services")
            options.put("description", note)
            options.put("image", R.drawable.logo)
            options.put("theme.color", "#3169D1")
            options.put("currency", "INR")
            options.put("amount", fAmount)
            options.put("send_sms_hash",true)
            options.put("email", userEmail)
            options.put("contact", userMobile)

            val retryObj = JSONObject()
            retryObj.put("enabled", true)
            retryObj.put("max_count", 4)
            options.put("retry", retryObj)


            co.open(activity, options)
        } catch (e: Exception) {
            Toast.makeText(activity, "Error in payment: " + e.message, Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    override fun onSuccess(data: Any, tag: String) {
        layoutLoader.visibility = View.GONE
        if(data.equals("SUCCESS")){
            this.showToastMsg("Order Successfully Placed")
            this.openClearActivity(UserHomeActivity::class.java)
        }
        else{
            this.showToastMsg("Order failed try again latter")
        }
    }

    override fun onFailure(message: String) {
        layoutLoader.visibility = View.GONE
        this.showToastMsg("Error: $message")
    }

    override fun onPaymentError(p0: Int, p1: String?) {
        paymentStatus = "Transaction failed"
       // callAPIToSaveOrder()
    }

    override fun onPaymentSuccess(dataString: String?) {
        paymentStatus = "Success"
        transactionNo = dataString.toString()
        callAPIToSaveOrder()
    }

    private fun callAPIToSaveOrder() {
        layoutLoader.visibility = View.VISIBLE
        val method = "placeOrder"
        val jsonObject = JSONObject()
        try {
            jsonObject.put("method", method)
            jsonObject.put("userId", userId)
            jsonObject.put("userEmail", userEmail)
            jsonObject.put("productId", productId)
            jsonObject.put("merchantId", merchantId)
            jsonObject.put("noOfDays", listItem._qty.toString())
            jsonObject.put("productPrice", listItem.priceToSell)
            jsonObject.put("noOfController", listItem.controllerQty)
            jsonObject.put("controllerCharges", listItem.controllerCharges)
            jsonObject.put("discountedPrice", listItem.discountedPrice)
            jsonObject.put("totalPrice", listItem.totalPayable)
            jsonObject.put("transactionNo", transactionNo)
            jsonObject.put("paymentStatus", paymentStatus)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        apiPostCall(Constants.BASE_URL, jsonObject, this, method)
    }


}