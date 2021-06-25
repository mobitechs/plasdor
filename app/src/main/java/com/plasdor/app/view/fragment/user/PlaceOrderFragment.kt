package com.plasdor.app.view.fragment.user

import android.graphics.Paint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatSpinner
import androidx.appcompat.widget.AppCompatTextView
import com.plasdor.app.R
import com.plasdor.app.callbacks.ApiResponse
import com.plasdor.app.model.ProductListItems
import com.plasdor.app.model.UserModel
import com.plasdor.app.session.SharePreferenceManager
import com.plasdor.app.utils.*
import com.plasdor.app.view.activity.UserHomeActivity
import org.json.JSONException
import org.json.JSONObject

class PlaceOrderFragment : Fragment(), ApiResponse {


    lateinit var merchantItem: UserModel
    lateinit var listItem: ProductListItems

    lateinit var rootView: View
    lateinit var btnPlaceOrder: AppCompatTextView
    lateinit var ivProdImage: AppCompatImageView
    lateinit var tvProductName: AppCompatTextView
    lateinit var txtType: AppCompatTextView
    lateinit var txtPrice: AppCompatTextView
    lateinit var txtPriceToSell: AppCompatTextView
    lateinit var txtNoOfDays: AppCompatTextView
    lateinit var txtUAddress: AppCompatTextView
    lateinit var txtUCity: AppCompatTextView
    lateinit var txtUPinCode: AppCompatTextView

    lateinit var imgRadioBtn: AppCompatImageView
    lateinit var txtName: AppCompatTextView
    lateinit var txtMobile: AppCompatTextView
    lateinit var txtEmail: AppCompatTextView
    lateinit var txtAddress: AppCompatTextView
    lateinit var txtCity: AppCompatTextView
    lateinit var txtPinCode: AppCompatTextView

    lateinit var layoutLoader: RelativeLayout

    var productId = ""
    var merchantId = ""
    var userId = ""
    var userEmail = ""
    var address = ""
    var city = ""
    var pinCode = ""
    var price =""
    var priceToSell =""
    var qty = 0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_place_order, container, false)
        initView()
        return rootView
    }

    private fun initView() {
        userId = SharePreferenceManager.getInstance(requireContext()).getUserLogin(Constants.USERDATA)?.get(0)?.userId.toString()
        userEmail = SharePreferenceManager.getInstance(requireContext()).getUserLogin(Constants.USERDATA)?.get(0)?.email.toString()
        address = SharePreferenceManager.getInstance(requireContext()).getUserLogin(Constants.USERDATA)?.get(0)?.address.toString()
        city = SharePreferenceManager.getInstance(requireContext()).getUserLogin(Constants.USERDATA)?.get(0)?.city.toString()
        pinCode = SharePreferenceManager.getInstance(requireContext()).getUserLogin(Constants.USERDATA)?.get(0)?.pincode.toString()

        ivProdImage = rootView.findViewById(R.id.ivProdImage)
        tvProductName = rootView.findViewById(R.id.tvProductName)
        txtType = rootView.findViewById(R.id.txtType)
        txtPrice = rootView.findViewById(R.id.txtPrice)
        txtPriceToSell = rootView.findViewById(R.id.txtPriceToSell)
        txtNoOfDays = rootView.findViewById(R.id.txtNoOfDays)

        txtUAddress = rootView.findViewById(R.id.txtUAddress)
        txtUCity = rootView.findViewById(R.id.txtUCity)
        txtUPinCode = rootView.findViewById(R.id.txtUPinCode)


        imgRadioBtn = rootView.findViewById(R.id.imgRadioBtn)
        txtName = rootView.findViewById(R.id.txtName)
        txtMobile = rootView.findViewById(R.id.txtMobile)
        txtEmail = rootView.findViewById(R.id.txtEmail)
        txtAddress = rootView.findViewById(R.id.txtAddress)
        txtCity = rootView.findViewById(R.id.txtCity)
        txtPinCode = rootView.findViewById(R.id.txtPinCode)

        btnPlaceOrder = rootView.findViewById(R.id.btnPlaceOrder)
        layoutLoader = rootView.findViewById(R.id.layoutLoader)

        listItem = arguments?.getParcelable("productItem")!!
        merchantItem = arguments?.getParcelable("merchantItem")!!
        qty = arguments?.getInt("noOfDays")!!

        productId = listItem.pId
        ivProdImage.setImage(listItem.img!!)

        tvProductName.text = listItem.productName

        price = (qty*listItem.price.toInt()).toString()
        priceToSell = (qty*listItem.priceToSell.toInt()).toString()
        txtPrice.text = "Rs. " + price
        txtPriceToSell.text = "Rs. " + priceToSell
        txtType.text = "Type " + listItem.type
        txtNoOfDays.text = qty.toString()

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
            layoutLoader.visibility = View.VISIBLE
            val method = "placeOrder"
            val jsonObject = JSONObject()
            try {
                jsonObject.put("method", method)
                jsonObject.put("userId", userId)
                jsonObject.put("userEmail", userEmail)
                jsonObject.put("productId", productId)
                jsonObject.put("merchantId", merchantId)
                jsonObject.put("noOfDays", qty.toString())
                jsonObject.put("productPrice", listItem.priceToSell)
                jsonObject.put("totalPrice", priceToSell)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            apiPostCall(Constants.BASE_URL, jsonObject, this, method)
        }
    }

    override fun onSuccess(data: Any, tag: String) {
        layoutLoader.visibility = View.GONE
        if(data.equals("SUCCESS")){
            requireContext().showToastMsg("Order Successfully Placed")
            requireContext().openClearActivity(UserHomeActivity::class.java)
        }
        else{
            requireContext().showToastMsg("Order failed try again latter")
        }
    }

    override fun onFailure(message: String) {
        layoutLoader.visibility = View.GONE
        requireContext().showToastMsg("Error: $message")
    }


}