package com.plasdor.app.view.activity

import android.app.Activity
import android.content.DialogInterface
import android.graphics.Paint
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
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
    var rentalType = ""
    var deliveryType = ""
    var deliveryCharges = 0
    var adminWillGet = 0f
    var merchantWillGet = 0f
    var deliveredBy = ""
    var finalPayableAmount =0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place_order)
        val window: Window = window
        setStatusColor(window, resources.getColor(R.color.colorPrimaryDark))

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
            priceToShow = listItem.priceToShowHourly
            priceToSell = listItem.priceToSellHourly

            labelPriceWithQty.text = "Price (${listItem.priceToSellHourly} * ${listItem.qty})"
            price  = listItem.qty * listItem.priceToSellHourly.toInt()
            additionalDiscount = (listItem.qty * listItem.priceToSellHourly.toInt()) - listItem.qtyWisePrice
        } else {
            labelNoOf.text = "No of Day:"
            priceToShow = listItem.priceToShowDaily
            priceToSell = listItem.priceToSellDaily

            labelPriceWithQty.text = "Price (${listItem.priceToSellDaily} * ${listItem.qty})"
            price  = listItem.qty * listItem.priceToSellDaily.toInt()
            additionalDiscount = (listItem.qty * listItem.priceToSellDaily.toInt()) - listItem.qtyWisePrice
        }

        txtDisplayPriceToShow.text = "Rs. "+priceToShow
        txtDisplayPriceToSell.text = "Rs. "+priceToSell
        txtDisplayPriceToShow.setPaintFlags(txtDisplayPriceToShow.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG)

        txtPrice.text = "Rs. "+price
        txtAdditionalDiscount.text = "Rs. "+additionalDiscount
        discountedPrice = price-additionalDiscount
        txtDiscountedPrice.text = "Rs. "+discountedPrice

        txtControllerQty.text = listItem.controllerQty.toString()
        txtControllerCharges.text =  "Rs. "+listItem.controllerCharges

        txtDeliveryCharges.text =  "Rs. "+deliveryCharges

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

        imgRadioBtn.visibility = View.GONE

        btnPlaceOrder.setOnClickListener {
            transactionNo = "TID" + System.currentTimeMillis()
//            amount = listItem.totalPayable.toString()
            amount = finalPayableAmount.toString()
            note = "Payment for Plasdor Services."
            //payUsingUpi(amount, upiId, name, note)

            showSelectPaymentTypeDialog()
        }
    }



    private fun razorPayGateway() {

        val activity: Activity = this
        val co = Checkout()
        //test key
        co.setKeyID("rzp_test_VNPFueei0vUGKm")
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
            e.printStackTrace()
        }
    }

    override fun onSuccess(data: Any, tag: String) {
        layoutLoader.visibility = View.GONE
        if (data.equals("SUCCESS")) {
            this.showToastMsg("Order Successfully Placed")
            this.openClearActivity(UserHomeActivity::class.java)
        } else {
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

        // set the custom layout
        val customLayout: View = layoutInflater.inflate(R.layout.payment_type_dialog_layout, null)
        builder.setView(customLayout)
        val radio_group: RadioGroup = customLayout.findViewById(R.id.radio_group)
        radio_group.setOnCheckedChangeListener(
            RadioGroup.OnCheckedChangeListener { group, checkedId ->
                val radio: RadioButton = customLayout.findViewById(checkedId)
                paymentType = radio.text.toString()
            })

        builder.setPositiveButton(
            "Submit",
            DialogInterface.OnClickListener { dialog, which -> // send data from the
                // AlertDialog to the Activity

                if(paymentType.equals("Online")){
                    razorPayGateway()
                }else{
                    callAPIToSaveOrder()
                }
            })
        // create and show
        // the alert dialog
        val dialog: AlertDialog = builder.create()
//        dialog.setCancelable(false)
        dialog.show()
    }

}