package com.plasdor.app.view.fragment.user

import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
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
    lateinit var txtControllerQty: AppCompatTextView
    lateinit var txtControllerCharges: AppCompatTextView
    lateinit var txtTotalPayable: AppCompatTextView


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
        address =
            SharePreferenceManager.getInstance(requireContext()).getUserLogin(Constants.USERDATA)
                ?.get(0)?.address.toString()
        city = SharePreferenceManager.getInstance(requireContext()).getUserLogin(Constants.USERDATA)
            ?.get(0)?.city.toString()
        pinCode =
            SharePreferenceManager.getInstance(requireContext()).getUserLogin(Constants.USERDATA)
                ?.get(0)?.pincode.toString()

        ivProdImage = rootView.findViewById(R.id.ivProdImage)
        tvProductName = rootView.findViewById(R.id.tvProductName)
        txtType = rootView.findViewById(R.id.txtType)
        txtPrice = rootView.findViewById(R.id.txtPrice)
        txtPriceToSell = rootView.findViewById(R.id.txtPriceToSell)
        txtNoOfDays = rootView.findViewById(R.id.txtNoOfDays)
        txtControllerQty = rootView.findViewById(R.id.txtControllerQty)
        txtControllerCharges = rootView.findViewById(R.id.txtControllerCharges)
        txtTotalPayable = rootView.findViewById(R.id.txtTotalPayable)

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