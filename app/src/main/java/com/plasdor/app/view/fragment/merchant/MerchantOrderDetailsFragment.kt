package com.plasdor.app.view.fragment.merchant

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.widget.AppCompatSpinner
import androidx.appcompat.widget.AppCompatTextView
import com.plasdor.app.R
import com.plasdor.app.callbacks.ApiResponse
import com.plasdor.app.model.MyOrderListItems
import com.plasdor.app.session.SharePreferenceManager
import com.plasdor.app.utils.Constants
import com.plasdor.app.utils.apiPostCall
import com.plasdor.app.utils.showToastMsg
import org.json.JSONException
import org.json.JSONObject

class MerchantOrderDetailsFragment : Fragment(), ApiResponse {

    lateinit var rootView: View
    lateinit var listItem: MyOrderListItems
    lateinit var txtOrderId: AppCompatTextView
    lateinit var txtOrderStatus: AppCompatTextView
    lateinit var txtOrderDate: AppCompatTextView
    lateinit var txtProductDetails: AppCompatTextView
    lateinit var txtAddress: AppCompatTextView
    lateinit var txtEmail: AppCompatTextView
    lateinit var txtMobileNo: AppCompatTextView
    lateinit var txtAmount: AppCompatTextView
    lateinit var txtDelivery: AppCompatTextView
    lateinit var txtTotal: AppCompatTextView
    lateinit var txtAdminEmail: AppCompatTextView
    lateinit var txtAdminMobileNo: AppCompatTextView
    lateinit var txtMerchantAddress: AppCompatTextView
    lateinit var txtMerchantName: AppCompatTextView
    lateinit var spinner: AppCompatSpinner

    lateinit var txtNoOfDays: AppCompatTextView
    lateinit var txtControllerQty: AppCompatTextView
    lateinit var txtControllerCharges: AppCompatTextView

    var userType = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_merchant_order_details, container, false)
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
        txtAddress = rootView.findViewById(R.id.txtAddress)!!
        txtEmail = rootView.findViewById(R.id.txtEmail)!!
        txtMobileNo = rootView.findViewById(R.id.txtMobileNo)!!
        txtAmount = rootView.findViewById(R.id.txtAmount)!!
        txtDelivery = rootView.findViewById(R.id.txtDelivery)!!
        txtTotal = rootView.findViewById(R.id.txtTotal)!!
        txtAdminEmail = rootView.findViewById(R.id.txtAdminEmail)!!
        txtAdminMobileNo = rootView.findViewById(R.id.txtAdminMobileNo)!!
        spinner = rootView.findViewById(R.id.spinner)!!
        txtMerchantAddress = rootView.findViewById(R.id.txtMerchantAddress)!!
        txtMerchantName = rootView.findViewById(R.id.txtMerchantName)!!

        txtNoOfDays = rootView.findViewById(R.id.txtNoOfDays)!!
        txtControllerQty = rootView.findViewById(R.id.txtControllerQty)!!
        txtControllerCharges = rootView.findViewById(R.id.txtControllerCharges)!!
//        var details = listItem.orderDetails.toString().replace(",", "\n")

        txtOrderId.text = listItem.orderId
//        txtOrderStatus.text = listItem.status
        txtOrderDate.text = listItem.addedDate
//        txtOrderDetails.text = details
        txtAddress.text = listItem.address + " " + listItem.addedDate + " " + listItem.city + " " + listItem.pincode
        txtEmail.text = listItem.email
        txtMobileNo.text = listItem.mobile
        txtAmount.text = "Rs. " + listItem.totalPrice
        txtDelivery.text = if(listItem.deliveryCharges.equals("") || listItem.deliveryCharges.equals("null")) "Rs. 0" else "Rs. " + listItem.deliveryCharges
        txtTotal.text = "Rs. " + listItem.totalPrice
        txtProductDetails.text = listItem.productName //+" Type " + listItem.type

        txtAdminEmail.text = listItem.email
        txtAdminMobileNo.text = listItem.mobile
        txtMerchantName.text = listItem.name
        txtMerchantAddress.text = listItem.address+" "+listItem.city+" "+listItem.pincode

        txtNoOfDays.text = listItem.noOfDays
        txtControllerQty.text = listItem.noOfController
        txtControllerCharges.text = "Rs. "+listItem.controllerCharges

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