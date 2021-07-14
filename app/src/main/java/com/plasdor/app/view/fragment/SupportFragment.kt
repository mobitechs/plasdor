package com.plasdor.app.view.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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
    lateinit var txtSupport1: TextView
    lateinit var txtSupport2: TextView
    lateinit var txtSupport3: TextView
    lateinit var txtSupport4: TextView
    lateinit var txtSupport5: TextView
    lateinit var txtSupport6: TextView

    var userId=""
    var userType=""
    var email=""
    var videoUrl=""

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
        txtSupport1 = rootView.findViewById(R.id.txtSupport1)!!
        txtSupport2 = rootView.findViewById(R.id.txtSupport2)!!
        txtSupport3 = rootView.findViewById(R.id.txtSupport3)!!
        txtSupport4 = rootView.findViewById(R.id.txtSupport4)!!
        txtSupport5 = rootView.findViewById(R.id.txtSupport5)!!
        txtSupport6 = rootView.findViewById(R.id.txtSupport6)!!

        txtSupport1.setOnClickListener {
            videoUrl = "https://youtu.be/51ZcdbimBL0"
            playOnYoutube()
        }
        txtSupport2.setOnClickListener  {
            videoUrl = "https://youtu.be/vXZYYCx-_-c"
            playOnYoutube()
        }
        txtSupport3.setOnClickListener {
            videoUrl = "https://www.youtube.com/watch?v=USccSZnS8MQ"
            playOnYoutube()
        }
        txtSupport4.setOnClickListener {
            videoUrl = "https://www.youtube.com/watch?v=DUwlGduupRI"
            playOnYoutube()
        }
        txtSupport5.setOnClickListener  {
            videoUrl = "https://www.youtube.com/watch?v=h7gyJRWrjbg"
            playOnYoutube()
        }
        txtSupport6.setOnClickListener  {
            videoUrl = "https://www.youtube.com/watch?v=KVh4KtUSW3A"
            playOnYoutube()
        }

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

    private fun playOnYoutube() {
        var intent = Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setPackage("com.google.android.youtube");
        startActivity(intent)
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