package com.plasdor.app.view.fragment.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.Fragment
import com.plasdor.app.R
import com.plasdor.app.callbacks.ApiResponse
import com.plasdor.app.model.UserModel
import com.plasdor.app.utils.Constants
import com.plasdor.app.utils.apiPostCall
import com.plasdor.app.utils.setImage
import com.plasdor.app.utils.showToastMsg
import com.plasdor.app.view.activity.AdminHomeActivity
import org.json.JSONException
import org.json.JSONObject


class AdminPendingUserDetailsFragment : Fragment(), ApiResponse {

    lateinit var rootView: View
    lateinit var txtAddress: TextView
    lateinit var imgElectricityBill: AppCompatImageView
    lateinit var imgAdhar: AppCompatImageView
    lateinit var btnVerify: AppCompatButton
    lateinit var btnBlock: AppCompatButton

    lateinit var listItem: UserModel


    var isVerified = 0
    var userId = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_admin_pending_user_details, container, false)
        initView()
        return rootView

    }

    private fun initView() {
        // take data from intent/bundle userId electricity bill and address
        listItem = arguments?.getParcelable("userDetails")!!
        txtAddress = rootView.findViewById(R.id.txtAddress)
        imgElectricityBill = rootView.findViewById(R.id.imgElectricityBill)
        imgAdhar = rootView.findViewById(R.id.imgAdhar)
        btnVerify = rootView.findViewById(R.id.btnVerify)
        btnBlock = rootView.findViewById(R.id.btnBlock)


        userId = listItem.userId
        txtAddress.setText(listItem.address + " " + listItem.city + " " + listItem.pincode)

        val imagepath = listItem.imgPathEBill
        if (imagepath == null || imagepath == "") {
            imgElectricityBill!!.background =
                requireContext().resources.getDrawable(R.drawable.img_not_available)
        } else {
            imgElectricityBill!!.setImage(imagepath)
        }

        val imagepath2 = listItem.imgPathAdhar
        if (imagepath2 == null || imagepath2 == "") {
            imgAdhar!!.background =
                requireContext().resources.getDrawable(R.drawable.img_not_available)
        } else {
            imgAdhar!!.setImage(imagepath2)
        }


        imgElectricityBill.setOnClickListener {
            var bundle = Bundle()
            bundle.putString("imagePath",listItem.imgPathEBill)
            (context as AdminHomeActivity).OpenFullScreenImageFragment(bundle)
        }
        imgAdhar.setOnClickListener {
            var bundle = Bundle()
            bundle.putString("imagePath",listItem.imgPathAdhar)
            (context as AdminHomeActivity).OpenFullScreenImageFragment(bundle)
        }



        btnBlock.setOnClickListener {
            isVerified = 2
            verifyBlockAPICall()
        }
        btnVerify.setOnClickListener {
            isVerified = 1
            verifyBlockAPICall()
        }
    }

    private fun verifyBlockAPICall() {
        val method = "isVerifiedOrRejected"
        val jsonObject = JSONObject()
        try {
            jsonObject.put("method", method)
            jsonObject.put("userId", userId)
            jsonObject.put("isVerified", isVerified)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        apiPostCall(Constants.BASE_URL, jsonObject, this, method)
    }

    override fun onSuccess(data: Any, tag: String) {
        if (data.equals("FAILED")) {
            requireContext().showToastMsg("Verification Failed")
        } else {
            requireContext().showToastMsg("Verified Successfully")
            btnVerify.setText("Verified")
            btnVerify.isEnabled = false
            btnVerify.isClickable = false

            (context as AdminHomeActivity).openUserVerificationPendingList()
        }
    }

    override fun onFailure(message: String) {
        requireContext().showToastMsg(message)
    }


}