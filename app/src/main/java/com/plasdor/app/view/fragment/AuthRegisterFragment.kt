package com.plasdor.app.view.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.RelativeLayout
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
import org.json.JSONException
import org.json.JSONObject
import java.util.*


class AuthRegisterFragment : Fragment(), ApiResponse {

    lateinit var rootView: View
    lateinit var layoutLoader: RelativeLayout
    lateinit var txtDob: TextInputEditText
    var userType = "User"
    var email = ""
    var dd = ""
    var mm = ""
    var yyyy = ""
    var dob = ""
    var age = 0
    var IamFor = ""

    lateinit var datePicker: DatePickerHelper


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_register, container, false)
        initView()
        return rootView
    }

    private fun initView() {
        email = arguments?.getString("email").toString()
        datePicker = DatePickerHelper(requireActivity(), true)

        layoutLoader = rootView.findViewById(R.id.layoutLoader)
        val btnSignUp: Button = rootView.findViewById(R.id.btnSignUp)!!
        val etName: TextInputEditText = rootView.findViewById(R.id.etName)!!
        val etEmail: TextInputEditText = rootView.findViewById(R.id.etEmail)
        val etMobileNo: TextInputEditText = rootView.findViewById(R.id.etMobileNo)
        txtDob = rootView.findViewById(R.id.txtDob)
        val etPassword: TextInputEditText = rootView.findViewById(R.id.etPassword)
        val etConfirmPassword: TextInputEditText = rootView.findViewById(R.id.etConfirmPassword)
        val txtAddress: TextInputEditText = rootView.findViewById(R.id.txtAddress)
        val txtCity: TextInputEditText = rootView.findViewById(R.id.txtCity)
        val txtPinCode: TextInputEditText = rootView.findViewById(R.id.txtPinCode)
        val checkIsMerchant: CheckBox = rootView.findViewById(R.id.checkIsMerchant)

//        val navController = activity?.let { Navigation.findNavController(it, R.id.navFragment) }

        IamFor = arguments?.getString("IamFor")!!
        if (IamFor.equals("RentOn")) {
            etEmail.isEnabled = true
            checkIsMerchant.isChecked = true
        } else {
            etEmail.setText(email)
            etEmail.isEnabled = false
        }

        txtDob.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                when (event?.action) {
                    MotionEvent.ACTION_DOWN -> showDatePickerDialog()
                    //Do Something
                }
                return v?.onTouchEvent(event) ?: true
            }
        })

        btnSignUp.setOnClickListener {

            if (etName.text.toString().equals("")) {
                requireActivity().showToastMsg("Enter Name")
            } else if (etEmail.text.toString().equals("")) {
                requireActivity().showToastMsg("Enter Email Id")
            } else if (etMobileNo.text.toString().equals("")) {
                requireActivity().showToastMsg("Enter Mobile No")
            } else if (etPassword.text.toString().equals("")) {
                requireActivity().showToastMsg("Enter Password")
            } else if (etConfirmPassword.text.toString().equals("")) {
                requireActivity().showToastMsg("Enter Confirm Password ")
            } else if (txtAddress.text.toString().equals("")) {
                requireActivity().showToastMsg("Enter Address")
            } else if (txtCity.text.toString().equals("")) {
                requireActivity().showToastMsg("Enter City")
            } else if (txtPinCode.text.toString().equals("")) {
                requireActivity().showToastMsg("Enter PinCode ")
            } else {
                var address =
                    txtAddress.text.toString() + " " + txtCity.text.toString() + "" + txtPinCode.text.toString()
                var latlong: LatLng = getLocationFromAddress(requireContext(), address)!!
                if (isEmailValid(etEmail.text.toString()) != true) {
                    requireActivity().showToastMsg("Email is not valid")
                } else if (!etPassword.text.toString().equals(etConfirmPassword.text.toString())) {
                    requireActivity().showToastMsg("Passwords are not matched")
                } else {
                    if (checkIsMerchant.isChecked) {
                        userType = "Merchant"
                    }

                    layoutLoader.visibility = View.VISIBLE
                    val method = "userRegister"
                    val jsonObject = JSONObject()
                    try {
                        jsonObject.put("method", method)
                        jsonObject.put("userType", userType)
                        jsonObject.put("name", etName.text.toString())
                        jsonObject.put("mobile", etMobileNo.text.toString())
                        jsonObject.put("email", etEmail.text.toString())
                        jsonObject.put("password", etPassword.text.toString())
                        jsonObject.put("address", txtAddress.text.toString())
                        jsonObject.put("city", txtCity.text.toString())
                        jsonObject.put("pincode", txtPinCode.text.toString())
                        jsonObject.put("latitude", latlong.latitude.toString())
                        jsonObject.put("longitude", latlong.longitude.toString())
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                    apiPostCall(Constants.BASE_URL, jsonObject, this, method)
                }
            }
        }
    }

    private fun showDatePickerDialog() {
        val cal = Calendar.getInstance()
        val d = cal.get(Calendar.DAY_OF_MONTH)
        val m = cal.get(Calendar.MONTH)
        val y = cal.get(Calendar.YEAR)
        datePicker.showDialog(d, m, y, object : DatePickerHelper.Callback {

            override fun onDateSelected(dayofMonth: Int, month: Int, year: Int) {
                val dayStr = if (dayofMonth < 10) "0${dayofMonth}" else "${dayofMonth}"
                val mon = month + 1
                val monthStr = if (mon < 10) "0${mon}" else "${mon}"
//                age =  getAge(year,mon,dayofMonth).toInt()
                txtDob.setText("${dayStr}-${monthStr}-${year}".toString())

            }
        })
        datePicker.setMaxDate(Calendar.getInstance().timeInMillis)
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

            SharePreferenceManager.getInstance(requireContext()).save(Constants.EARNED_POINTS, user!![0].wallet)

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