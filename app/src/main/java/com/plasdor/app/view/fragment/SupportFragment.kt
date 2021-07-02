package com.plasdor.app.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import com.google.android.material.textfield.TextInputEditText
import com.plasdor.app.R
import com.plasdor.app.callbacks.ApiResponse
import com.plasdor.app.session.SharePreferenceManager
import com.plasdor.app.utils.Constants
import com.plasdor.app.utils.apiPostCall
import com.plasdor.app.utils.showToastMsg
import com.plasdor.app.view.activity.MerchantHomeActivity
import com.plasdor.app.view.activity.UserHomeActivity
import org.json.JSONException
import org.json.JSONObject


class SupportFragment : Fragment(), ApiResponse {

    lateinit var rootView:View
    lateinit var txtEmail: TextInputEditText
    lateinit var txtDetails: TextInputEditText
    lateinit var btnSubmit: AppCompatButton

    var userId=""
    var userType=""
    var email=""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_support, container, false)
        initView()
        return  rootView
    }

    private fun initView() {
        userId = SharePreferenceManager.getInstance(requireActivity()).getUserLogin(Constants.USERDATA)?.get(0)?.userId.toString()
        userType = SharePreferenceManager.getInstance(requireActivity()).getUserLogin(Constants.USERDATA)?.get(0)?.userType.toString()
        email = SharePreferenceManager.getInstance(requireActivity()).getUserLogin(Constants.USERDATA)?.get(0)?.email.toString()

        txtEmail = rootView.findViewById(R.id.txtEmail)!!
        txtDetails = rootView.findViewById(R.id.txtDetails)!!
        btnSubmit = rootView.findViewById(R.id.btnSubmit)!!

        txtEmail.setText(email)
        txtEmail.isEnabled = false

        btnSubmit.setOnClickListener {
            if(txtDetails.text.toString().equals("")){
                requireContext().showToastMsg("Please Enter Feedback")
            }else{
                val method = "addSupportDetails"
                val jsonObject = JSONObject()
                try {
                    jsonObject.put("method", method)
                    jsonObject.put("userId", userId)
                    jsonObject.put("email", email)
                    jsonObject.put("details", txtDetails.text.toString())

                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                apiPostCall(Constants.BASE_URL, jsonObject, this, method)
            }
        }

    }

    override fun onSuccess(data: Any, tag: String) {
        if(userType.equals(Constants.USER)){
            (context as UserHomeActivity).displayView(1)
        }else{
            (context as MerchantHomeActivity).displayView(1)
        }
    }

    override fun onFailure(message: String) {
        requireContext().showToastMsg("Please try again after some time.")
    }

}