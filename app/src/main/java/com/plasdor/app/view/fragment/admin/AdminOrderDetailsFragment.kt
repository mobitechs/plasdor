package com.plasdor.app.view.fragment.admin

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatSpinner
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import com.plasdor.app.R
import com.plasdor.app.callbacks.ApiResponse
import com.plasdor.app.model.AdminAllOrderListItems
import com.plasdor.app.session.SharePreferenceManager
import com.plasdor.app.utils.Constants
import com.plasdor.app.utils.apiPostCall
import com.plasdor.app.utils.showToastMsg
import org.json.JSONException
import org.json.JSONObject

class AdminOrderDetailsFragment : Fragment(), ApiResponse {

    lateinit var rootView: View
    lateinit var listItem: AdminAllOrderListItems
    lateinit var txtOrderId: AppCompatTextView
    lateinit var txtOrderStatus: AppCompatTextView
    lateinit var txtOrderDate: AppCompatTextView
    lateinit var txtProductDetails: AppCompatTextView
    lateinit var txtAmount: AppCompatTextView
    lateinit var txtDelivery: AppCompatTextView
    lateinit var txtTotal: AppCompatTextView

    lateinit var txtMerchantName: AppCompatTextView
    lateinit var txtMerchantEmail: AppCompatTextView
    lateinit var txtMerchantMobile: AppCompatTextView
    lateinit var txtMerchantAddress: AppCompatTextView

    lateinit var txtUserName: AppCompatTextView
    lateinit var txtUserEmail: AppCompatTextView
    lateinit var txtUserMobileNo: AppCompatTextView
    lateinit var txtUserAddress: AppCompatTextView

    lateinit var labelNoOf: AppCompatTextView
    lateinit var txtPaymentStatus: AppCompatTextView
    lateinit var txtPaymentType: AppCompatTextView
    lateinit var txtOderStatus: AppCompatTextView
    lateinit var txtNoOfDaysHours: AppCompatTextView
    lateinit var txtRentalType: AppCompatTextView
    lateinit var txtControllerQty: AppCompatTextView
    lateinit var txtControllerCharges: AppCompatTextView
    lateinit var txtOrderDeliverBy: AppCompatTextView
    lateinit var txtMerchantEarned: AppCompatTextView
    lateinit var txtAdminEarned: AppCompatTextView
    lateinit var layoutDirection: LinearLayout

    lateinit var layoutRedeemPoints: RelativeLayout
    lateinit var txtRedeemPoint: AppCompatTextView
    lateinit var layoutPaymentDetails: LinearLayout

    lateinit var spinner: AppCompatSpinner
    var userType = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_admin_order_details, container, false)
        intView()
        return rootView
    }

    private fun intView() {
        listItem = arguments?.getParcelable("OrderDetails")!!

        userType =
            SharePreferenceManager.getInstance(requireContext()).getUserLogin(Constants.USERDATA)
                ?.get(0)?.userType.toString()

        txtOrderId = rootView.findViewById(R.id.txtOrderId)!!
        txtOrderStatus = rootView.findViewById(R.id.txtOrderStatus)!!
        txtOrderDate = rootView.findViewById(R.id.txtOrderDate)!!
        txtProductDetails = rootView.findViewById(R.id.txtProductDetails)!!
        txtAmount = rootView.findViewById(R.id.txtAmount)!!
        txtDelivery = rootView.findViewById(R.id.txtDelivery)!!
        txtTotal = rootView.findViewById(R.id.txtTotal)!!

        spinner = rootView.findViewById(R.id.spinner)!!

        txtMerchantName = rootView.findViewById(R.id.txtMerchantName)!!
        txtMerchantEmail = rootView.findViewById(R.id.txtMerchantEmail)!!
        txtMerchantMobile = rootView.findViewById(R.id.txtMerchantMobile)!!
        txtMerchantAddress = rootView.findViewById(R.id.txtMerchantAddress)!!

        txtUserName = rootView.findViewById(R.id.txtUserName)!!
        txtUserEmail = rootView.findViewById(R.id.txtUserEmail)!!
        txtUserMobileNo = rootView.findViewById(R.id.txtUserMobileNo)!!
        txtUserAddress = rootView.findViewById(R.id.txtUserAddress)!!

        txtAdminEarned = rootView.findViewById(R.id.txtAdminEarned)!!
        txtMerchantEarned = rootView.findViewById(R.id.txtMerchantEarned)!!
        txtOrderDeliverBy = rootView.findViewById(R.id.txtOrderDeliverBy)!!
        txtOderStatus = rootView.findViewById(R.id.txtOderStatus)!!
        txtPaymentStatus = rootView.findViewById(R.id.txtPaymentStatus)!!
        txtPaymentType = rootView.findViewById(R.id.txtPaymentType)!!
        txtRentalType = rootView.findViewById(R.id.txtRentalType)!!
        labelNoOf = rootView.findViewById(R.id.labelNoOf)!!
        txtNoOfDaysHours = rootView.findViewById(R.id.txtNoOfDays)!!
        txtControllerQty = rootView.findViewById(R.id.txtControllerQty)!!
        txtControllerCharges = rootView.findViewById(R.id.txtControllerCharges)!!

        txtOrderId.text = listItem.orderId
        txtOrderDate.text = listItem.addedDate

        txtAmount.text = "Rs. " + listItem.totalPrice
        txtDelivery.text = if(listItem.deliveryCharges.equals("") || listItem.deliveryCharges.equals("null")) "Rs. 0" else "Rs. " + listItem.deliveryCharges
        txtTotal.text = "Rs. " + listItem.totalPrice
        txtProductDetails.text = listItem.productName// + " Type " + listItem.type

        txtNoOfDaysHours.text = listItem.noOfDaysHours
        txtControllerQty.text = listItem.noOfController
        txtControllerCharges.text = "Rs. "+listItem.controllerCharges

        txtUserName.text = listItem.name
        txtUserEmail.text = listItem.email
        txtUserMobileNo.text = listItem.mobile
        txtUserAddress.text = listItem.address + " " + listItem.city + " " + listItem.pincode

        txtMerchantName.text = listItem.merchantName
        txtMerchantEmail.text = listItem.merchantEmail
        txtMerchantMobile.text = listItem.merchantMobile
        txtMerchantAddress.text = listItem.merchantAddress + " " + listItem.merchantCity + " " + listItem.merchantPinCode


        if(listItem.deliveredBy.equals(Constants.SELF)){
            txtOrderDeliverBy.text = "Self Pickup from user."
        }
        else if(listItem.deliveredBy.equals(Constants.COMPANY)){
            txtOrderDeliverBy.text = "Company will deliver this order"
        }
        else{
            txtOrderDeliverBy.text = "You have to deliver this order"
        }
        txtMerchantEarned.text = "Rs. "+listItem.merchantWillGet
        txtAdminEarned.text = "Rs. "+listItem.adminWillGet
        txtRentalType.text = listItem.rentalType
        txtPaymentStatus.text = listItem.paymentStatus
        txtPaymentType.text = listItem.paymentType
        txtOderStatus.text = listItem.orderStatus

        if(listItem.rentalType.equals(Constants.Hourly)){
            labelNoOf.text = "No of Hours:"
        }
        else{
            labelNoOf.text = "No of Days:"
        }

        layoutDirection = rootView.findViewById(R.id.layoutDirection)!!
        layoutDirection.setOnClickListener {
            var sourceLatitude = SharePreferenceManager.getInstance(requireContext()).getUserLogin(Constants.USERDATA)?.get(0)?.latitude.toString()
            var sourceLongitude = SharePreferenceManager.getInstance(requireContext()).getUserLogin(Constants.USERDATA)?.get(0)?.longitude.toString()
            var destinationLatitude = listItem.userLat
            var destinationLongitude = listItem.userLon
            val uri =
                "http://maps.google.com/maps?saddr=" + sourceLatitude.toString() + "," + sourceLongitude.toString() + "&daddr=" + destinationLatitude.toString() + "," + destinationLongitude
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
            startActivity(intent)
        }

        layoutRedeemPoints = rootView.findViewById(R.id.layoutRedeemPoints)!!
        layoutPaymentDetails = rootView.findViewById(R.id.layoutPaymentDetails)!!
        txtRedeemPoint = rootView.findViewById(R.id.txtRedeemPoint)!!

        if(listItem.paymentType.equals("Redeem")){
            txtRedeemPoint.text = listItem.redeemPointsUsed
            layoutRedeemPoints.visibility = View.VISIBLE
            layoutPaymentDetails.visibility = View.GONE
        }else{
            layoutRedeemPoints.visibility = View.GONE
            layoutPaymentDetails.visibility = View.VISIBLE
        }

        setupOrderStatusSpinner()
    }

    private fun setupOrderStatusSpinner() {
        if (userType.equals(Constants.ADMIN)) {
            txtOrderStatus.visibility = View.GONE
            spinner.visibility = View.VISIBLE

            val adapter = ArrayAdapter(
                requireContext(),
                R.layout.spinner_layout,
                Constants.orderStatusArray
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.setAdapter(adapter)

//            spinner.setSelection(Constants.orderStatusArray.indexOf(listItem.status))
            spinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {

                override fun onNothingSelected(p0: AdapterView<*>?) {

                }

                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    var status = Constants.orderStatusArray[p2]
//                    if (!listItem.status.equals(status)) {
//                        callAPIToChangeOrderStatus(listItem.orderId, status)
//                    }

                }
            })
        } else {
            txtOrderStatus.visibility = View.VISIBLE
            spinner.visibility = View.GONE
        }
    }

    private fun callAPIToChangeOrderStatus(orderId: String, status: String) {
        val method = "ChangeOrderStatus"
        val jsonObject = JSONObject()
        try {
            jsonObject.put("method", method)
            jsonObject.put("orderId", orderId)
            jsonObject.put("orderStatus", status)
            jsonObject.put("clientBusinessId", Constants.clientBusinessId)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        apiPostCall(Constants.BASE_URL, jsonObject, this, method)
    }

    override fun onSuccess(data: Any, tag: String) {
        if (data.equals("SUCCESS")) {
            requireContext().showToastMsg("Order status change successfully.")
        } else {
            requireContext().showToastMsg("Failed to change order status.")
        }
    }

    override fun onFailure(message: String) {
        requireContext().showToastMsg(message)
    }
}