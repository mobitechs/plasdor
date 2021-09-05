package com.plasdor.app.view.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.plasdor.app.R
import com.plasdor.app.callbacks.ApiResponse
import com.plasdor.app.model.UserModel
import com.plasdor.app.session.SharePreferenceManager
import com.plasdor.app.utils.*
import com.plasdor.app.view.activity.AuthActivity
import org.json.JSONException
import org.json.JSONObject
import java.util.ArrayList

class AuthAddCityPinCodeFragment : Fragment(), ApiResponse {


    lateinit var rootView: View
    lateinit var etCity: AppCompatEditText
    lateinit var etPincode: AppCompatEditText
    lateinit var btnSignUp: AppCompatButton
    lateinit var layoutLoader: RelativeLayout
    lateinit var latlong: LatLng

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        rootView = inflater.inflate(R.layout.fragment_auth_add_city_pin_code, container, false)
        initView()
        return rootView
    }

    private fun initView() {

        etCity = rootView.findViewById(R.id.etCity)
        etPincode = rootView.findViewById(R.id.etPincode)
        layoutLoader = rootView.findViewById(R.id.layoutLoader)
        btnSignUp = rootView.findViewById(R.id.btnSignUp)

        btnSignUp.setOnClickListener {
            if(etCity.text.toString().equals("")){
                requireActivity().showToastMsg("Please enter city")
            }else if(etPincode.text.toString().equals("")){
                requireActivity().showToastMsg("Please enter pincode")
            }else{
                var address =
                    arguments?.getString("address").toString() + " " + etCity.text.toString() + "" + etPincode.text.toString()
                latlong = getLocationFromAddress(requireContext(), address)!!
                callRegisterAPI()
            }

        }

    }

    private fun callRegisterAPI() {
        layoutLoader.visibility = View.VISIBLE
        val method = "userRegister"
        val jsonObject = JSONObject()
        try {
            jsonObject.put("method", method)
            jsonObject.put("userType",  arguments?.getString("userType").toString())
            jsonObject.put("name", arguments?.getString("name").toString())
            jsonObject.put("mobile", arguments?.getString("mobile").toString())
            jsonObject.put("email", arguments?.getString("email").toString())
            jsonObject.put("password", arguments?.getString("password").toString())
            jsonObject.put("dob", arguments?.getString("dob").toString())


            jsonObject.put("address", arguments?.getString("address").toString())
            jsonObject.put("city", etCity.text.toString())
            jsonObject.put("pincode", etPincode.text.toString())
            jsonObject.put("latitude", latlong.latitude.toString())
            jsonObject.put("longitude", latlong.longitude.toString())
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        apiPostCall(Constants.BASE_URL, jsonObject, this, method)
    }

    override fun onSuccess(data: Any, tag: String) {

        if (data.equals("USER_ALREADY_EXIST")) {
            requireActivity().showToastMsg("User details already exist")
        } else if (data.equals("FAILED")) {
            requireActivity().showToastMsg("Registration failed")
        } else {
            requireActivity().showToastMsg("Registration successfully done")

            //do logout
            SharePreferenceManager.getInstance(requireContext()).clearSharedPreference(requireContext())
            requireActivity().finish()

            val gson = Gson()
            val type = object : TypeToken<ArrayList<UserModel>>() {}.type
            var user: ArrayList<UserModel>? = gson.fromJson(data.toString(), type)

            Log.d("user", "" + user)
            SharePreferenceManager.getInstance(requireActivity()).save(Constants.ISLOGIN, true)
            SharePreferenceManager.getInstance(requireActivity())
                .saveUserLogin(Constants.USERDATA, user)

            SharePreferenceManager.getInstance(requireContext()).save(
                Constants.EARNED_POINTS,
                user!![0].wallet
            )

            SharePreferenceManager.getInstance(requireContext()).save(Constants.FIRST_FREE_ORDER_COMPLETE, user!![0].firstFreeOrder)

            requireActivity().checkLogin()
//            requireActivity().openActivity(UserHomeActivity::class.java)
        }
        layoutLoader.visibility = View.GONE
    }

    override fun onFailure(message: String) {
        requireActivity().showToastMsg(message)
        layoutLoader.visibility = View.GONE
    }


}