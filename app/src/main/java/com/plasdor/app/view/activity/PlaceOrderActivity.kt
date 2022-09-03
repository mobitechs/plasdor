    package com.plasdor.app.view.activity

import android.app.Activity
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.plasdor.app.R
import com.plasdor.app.callbacks.ApiResponse
import com.plasdor.app.model.AvailableMerchantListItem
import com.plasdor.app.model.ProductListItems
import com.plasdor.app.model.UserModel
import com.plasdor.app.session.SharePreferenceManager
import com.plasdor.app.utils.*
import com.plasdor.app.view.fragment.UpdateKycFragment
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

        var productId = ""
        var merchantId = ""
        var userId = ""
        var userEmail = ""
        var userMobile = ""
    var address = ""
    var city = ""
    var pinCode = ""
    var isVerified = ""

    var transactionNo = ""
    var amount = "0"
    var fAmount = 0
    var paymentType = ""
    var paymentStatus = ""
    var note = ""
    var rentalType = ""
    var deliveryType = ""
    var deliveryCharges = 0
    var adminWillGet = 0f
    var merchantWillGet = 0f
    var deliveredBy = ""
    var finalPayableAmount =0
    var method=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place_order)
        val window: Window = window
        setStatusColor(window, resources.getColor(R.color.black))

        initView()
    }


    private fun initView() {
        Checkout.preload(applicationContext)
        userId = SharePreferenceManager.getInstance(this).getUserLogin(Constants.USERDATA)
            ?.get(0)?.userId.toString()
        userEmail = SharePreferenceManager.getInstance(this).getUserLogin(Constants.USERDATA)
            ?.get(0)?.email.toString()
        userMobile = SharePreferenceManager.getInstance(this).getUserLogin(Constants.USERDATA)
            ?.get(0)?.mobile.toString()
        address =
            SharePreferenceManager.getInstance(this).getUserLogin(Constants.USERDATA)
                ?.get(0)?.address.toString()
        city = SharePreferenceManager.getInstance(this).getUserLogin(Constants.USERDATA)
            ?.get(0)?.city.toString()
        pinCode =
            SharePreferenceManager.getInstance(this).getUserLogin(Constants.USERDATA)
                ?.get(0)?.pincode.toString()
        isVerified =
            SharePreferenceManager.getInstance(this).getUserLogin(Constants.USERDATA)
                ?.get(0)?.isVerified.toString()

        listItem = intent.getParcelableExtra("productItem")!!
        merchantItem = intent.getParcelableExtra("merchantItem")!!
        rentalType = intent.getStringExtra("rentalType")!!
        deliveryType = intent.getStringExtra("deliveryType")!!
        deliveryCharges = intent.getIntExtra("deliveryCharges", 0)!!


        merchantWillGet = (listItem.totalPayable.toInt() * (Constants.percentBetweenMerchantNPlasdor / 100.0f))
        adminWillGet = (listItem.totalPayable.toInt() - merchantWillGet)

        if(deliveryType.equals(Constants.selfPickup)){
            deliveredBy = Constants.SELF
        }
        else{
            if(merchantItem.willDeliver.equals("Yes")){
                deliveredBy = Constants.MERCHANT
                merchantWillGet = merchantWillGet + Constants.deliveryCharges
            }
            else if(merchantItem.willDeliver.equals("No")){
                deliveredBy = Constants.COMPANY
                merchantWillGet = merchantWillGet - Constants.deliveryCharges
                adminWillGet = adminWillGet + Constants.deliveryCharges
            }
        }

        productId = listItem.pId
        ivProdImage.setImage(listItem.img!!)

        var priceToShow = ""
        var priceToSell = ""
        var price = 0
        var additionalDiscount = 0
        var discountedPrice = 0

        tvProductName.text = listItem.productName
        txtRentalType.text = rentalType
        txtNoOfDaysHours.text = listItem.qty.toString()

        if (rentalType.equals(Constants.Hourly)) {
            labelNoOf.text = "No of Hour:"
            priceToShow = (listItem.priceToShowHourly.toInt() * 1).toString()
            priceToSell = listItem.priceToSellHourly

//            labelPriceWithQty.text = "Price (${listItem.priceToSellHourly} * ${listItem.qty} Hr)"
            labelPriceWithQty.text = "Price for ${listItem.qty} Hr"
            price  = listItem.qty * listItem.priceToShowHourly.toInt()
            additionalDiscount = (listItem.qty * listItem.priceToShowHourly.toInt()) - listItem.qtyWisePrice
        } else {
            labelNoOf.text = "No of Day:"
            priceToShow = listItem.priceToShowDaily
            priceToSell = listItem.priceToSellDaily

//            labelPriceWithQty.text = "Price (${listItem.priceToSellDaily} * ${listItem.qty} Day)"
            labelPriceWithQty.text = "Price for  ${listItem.qty} Day"
            price  = listItem.qty * listItem.priceToSellDaily.toInt()
            additionalDiscount = (listItem.qty * listItem.priceToSellDaily.toInt()) - listItem.qtyWisePrice
        }

        txtDisplayPriceToShow.text = "Rs. "+priceToShow
        txtDisplayPriceToSell.text = "Rs. "+priceToSell
        txtDisplayPriceToShow.setPaintFlags(txtDisplayPriceToShow.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG)

        txtPrice.text = "Rs. "+price
        txtAdditionalDiscount.text = "- Rs. "+additionalDiscount
        discountedPrice = price-additionalDiscount
        txtDiscountedPrice.text = "Rs. "+discountedPrice

        txtControllerQty.text = listItem.controllerQty.toString()
        txtControllerCharges.text =  "+ Rs. "+listItem.controllerCharges

        txtDeliveryCharges.text =  "+ Rs. "+deliveryCharges

        finalPayableAmount = listItem.totalPayable+deliveryCharges.toInt()
//        txtTotalPayable.text = listItem.totalPayable.toString()
        txtTotalPayable.text =  "Rs. "+finalPayableAmount.toString()

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
        txtDistance.text = "Distance "+merchantItem.distance  +" Km"

        imgRadioBtn.visibility = View.GONE

        if(isVerified.equals("1")){
            btnPlaceOrder.visibility = View.VISIBLE
            btnVerificationPending.visibility = View.GONE
        }else{
            getUserDetails()
            btnPlaceOrder.visibility = View.GONE
            btnVerificationPending.visibility = View.VISIBLE
        }
        btnPlaceOrder.setOnClickListener {
            if(isVerified.equals("1")){
                transactionNo = "TID" + System.currentTimeMillis()
//              amount = listItem.totalPayable.toString()
                amount = finalPayableAmount.toString()
//              note = "Payment for Plasdor Services."
                note = listItem.productName
                //payUsingUpi(amount, upiId, name, note)
                showSelectPaymentTypeDialog()
            }else{

            }

        }
        btnVerificationPending.setOnClickListener {
           // getUserDetails()
            OpenKYCUpdate()

        }
    }
    fun OpenKYCUpdate() {
        nav_host_fragment.visibility = View.VISIBLE
        addFragment(
            UpdateKycFragment(),
            false,
            R.id.nav_host_fragment,
            "UpdateKycFragment"
        )
    }

    private fun getUserDetails() {
        layoutLoader.visibility = View.VISIBLE
        method = "GetUserDetails"
        val jsonObject = JSONObject()
        try {
            jsonObject.put("method", method)
            jsonObject.put("userId", userId)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        apiPostCall(Constants.BASE_URL, jsonObject, this, method)
    }


    private fun razorPayGateway() {

        val activity: Activity = this
        val co = Checkout()
        //test key
//        co.setKeyID("rzp_test_VNPFueei0vUGKm")
        //live key
        co.setKeyID("rzp_live_OSidjlMuYoFOV4")

        try {
            // amount = "1"
            fAmount = Math.round(amount.toFloat() * 100)
            val options = JSONObject()
            options.put("name", "Plasdor Services")
            options.put("description", note)
//            options.put("image", R.drawable.logo)
            options.put("theme.color", "#116AF9")
            options.put("currency", "INR")
            options.put("amount", fAmount)
            options.put("send_sms_hash", true)
            options.put("email", userEmail)
            options.put("contact", userMobile)

            val retryObj = JSONObject()
            retryObj.put("enabled", true)
            retryObj.put("max_count", 4)
            options.put("retry", retryObj)


            co.open(activity, options)
        } catch (e: Exception) {
            Toast.makeText(activity, "Error in payment: " + e.message, Toast.LENGTH_LONG).show()
           // e.printStackTrace()
        }
    }

    override fun onSuccess(data: Any, tag: String) {
        layoutLoader.visibility = View.GONE

        if(method.equals("GetUserDetails")){
            val gson = Gson()
            val type = object : TypeToken<ArrayList<UserModel>>() {}.type
            var user: ArrayList<UserModel>? = gson.fromJson(data.toString(), type)

            Log.d("user", "" + user)

            SharePreferenceManager.getInstance(this)
                .saveUserLogin(Constants.USERDATA, user)
            isVerified = user!![0].isVerified
            if(user!![0].isVerified.equals("1")){
                btnPlaceOrder.visibility = View.VISIBLE
                btnVerificationPending.visibility = View.GONE
            }else{
                btnPlaceOrder.visibility = View.GONE
                btnVerificationPending.visibility = View.VISIBLE
                this.showToastMsg("You are not verified yet.")
            }
        }else{
            if (data.equals("SUCCESS")) {
                this.showToastMsg("Order Successfully Placed")
                this.openClearActivity(UserHomeActivity::class.java)
            } else {
                this.showToastMsg("Order failed try again latter")
            }
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
         method = "placeOrder"
        val jsonObject = JSONObject()
        try {
            jsonObject.put("method", method)
            jsonObject.put("userId", userId)
            jsonObject.put("userEmail", userEmail)
            jsonObject.put("pId", productId)
            jsonObject.put("merchantId", merchantId)
            jsonObject.put("rentalType", rentalType)
            jsonObject.put("noOfDaysHours", listItem.qty.toString())
            jsonObject.put("productPriceDaily", listItem.priceToSellDaily)
            jsonObject.put("productPriceHourly", listItem.priceToSellHourly)
            jsonObject.put("noOfController", listItem.controllerQty)
            jsonObject.put("controllerCharges", listItem.controllerCharges)
            jsonObject.put("discountedPrice", listItem.discountedPrice)
            jsonObject.put("deliveryCharges", deliveryCharges)
            jsonObject.put("totalPrice", listItem.totalPayable)
            jsonObject.put("transactionNo", transactionNo)
            jsonObject.put("paymentType", paymentType)
            jsonObject.put("paymentStatus", paymentStatus)
            jsonObject.put("adminWillGet", adminWillGet)
            jsonObject.put("merchantWillGet", merchantWillGet)
            jsonObject.put("deliveredBy", deliveredBy)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        apiPostCall(Constants.BASE_URL, jsonObject, this, method)
    }


    fun showSelectPaymentTypeDialog() {

        // Create an alert builder
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Select Payment Option")
        builder.setPositiveButton("Submit", null)

        // set the custom layout
        val customLayout: View = layoutInflater.inflate(R.layout.payment_type_dialog_layout, null)
        builder.setView(customLayout)
        val radio_group: RadioGroup = customLayout.findViewById(R.id.radio_group)
        radio_group.setOnCheckedChangeListener(
            RadioGroup.OnCheckedChangeListener { group, checkedId ->
                val radio: RadioButton = customLayout.findViewById(checkedId)
                paymentType = radio.text.toString()
            })


        // create and show
        // the alert dialog
        val dialog: AlertDialog = builder.create()
        dialog.setOnShowListener {
            val positiveButton: Button = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            positiveButton.setOnClickListener {
                if (paymentType.equals("")) {
                    showToastMsg("Please select payment option")
                } else {
                    if (paymentType.equals("UPI/Card/NEFT")) {
                        razorPayGateway()
                    } else if (paymentType.equals("Cash On Delivery")) {
                        callAPIToSaveOrder()
                    }
                    dialog.dismiss();
                }
            }
        }
//        dialog.setCancelable(false)
        dialog.show()
    }

}