package com.plasdor.app.view.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.plasdor.app.R
import com.plasdor.app.callbacks.ApiResponse
import com.plasdor.app.model.UserModel
import com.plasdor.app.session.SharePreferenceManager
import com.plasdor.app.utils.*
import com.plasdor.app.view.activity.AdminHomeActivity
import com.plasdor.app.view.activity.AuthActivity
import com.plasdor.app.view.activity.MerchantHomeActivity
import com.plasdor.app.view.activity.UserHomeActivity
import org.json.JSONException
import org.json.JSONObject

class AuthLoginFragment : Fragment(), ApiResponse {

    lateinit var rootView: View
    lateinit var layoutLoader: RelativeLayout
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_login, container, false)

        initView()
        return rootView
    }

    private fun initView() {
        val btnSignUp: TextView = rootView.findViewById(R.id.btnSignUp)!!
        val forgetPassword: TextView = rootView.findViewById(R.id.forgetPassword)!!
        val btnSignIn: Button = rootView.findViewById(R.id.btnSignIn)!!
        val etEmail: TextInputEditText = rootView.findViewById(R.id.etEmail)!!
        val etPassword: TextInputEditText = rootView.findViewById(R.id.etPassword)!!

//        val navController = activity?.let { Navigation.findNavController(it, R.id.navFragment) }

        layoutLoader = rootView.findViewById(R.id.layoutLoader)
        btnSignUp.setOnClickListener {
//            navController?.navigate(R.id.action_login_to_register)
            (context as AuthActivity).openRegisterPage()
        }

        forgetPassword.setOnClickListener {
//            navController?.navigate(R.id.action_login_to_forgotPasswordFragment)
            (context as AuthActivity).openForgotPasswordPage()
        }

        btnSignIn.setOnClickListener {
            if (etEmail.text.toString().equals("")) {
                requireActivity().showToastMsg("Enter Email Id")
            } else if (etPassword.text.toString().equals("")) {
                requireActivity().showToastMsg("Enter Password")
            } else {
                layoutLoader.visibility = View.VISIBLE
                val method = "userLogin"
                val jsonObject = JSONObject()
                try {
                    jsonObject.put("method", method)
                    jsonObject.put("email", etEmail.text.toString())
                    jsonObject.put("password", etPassword.text.toString())
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                apiPostCall(Constants.BASE_URL, jsonObject, this, method)
            }
        }

    }

    override fun onSuccess(data: Any, tag: String) {

        if (data.equals("FAILED")) {
            requireContext().showToastMsg("Login Failed")
        } else {
            requireContext().showToastMsg("Login Successful")
            val gson = Gson()
            val type = object : TypeToken<ArrayList<UserModel>>() {}.type
            var user: ArrayList<UserModel>? = gson.fromJson(data.toString(), type)

            Log.d("user", "" + user)
            SharePreferenceManager.getInstance(requireContext()).save(Constants.ISLOGIN, true)
            SharePreferenceManager.getInstance(requireContext())
                .saveUserLogin(Constants.USERDATA, user)

            requireActivity().checkLogin()

        }

    }

    override fun onFailure(message: String) {
        requireContext().showToastMsg(message)
        layoutLoader.visibility = View.GONE
    }

}