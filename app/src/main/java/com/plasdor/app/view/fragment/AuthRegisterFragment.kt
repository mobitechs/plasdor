package com.plasdor.app.view.fragment

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.Fragment
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.fxn.utility.PermUtil
import com.github.dhaval2404.imagepicker.ImagePicker
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
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.File
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
    lateinit var latlong: LatLng

    var adharPath = ""
    var adharFile: File? = null
    var electricityBillPath = ""
    var electricityBillFile: File? = null
    lateinit var imgElectricityBill: AppCompatImageView
    lateinit var imgAdhar: AppCompatImageView
    var imageType = 0

    lateinit var etName: AppCompatEditText
    lateinit var etEmail: AppCompatEditText
    lateinit var etMobileNo: AppCompatEditText
    lateinit var etPassword: TextInputEditText
    lateinit var etConfirmPassword: TextInputEditText
    lateinit var etAddress: TextInputEditText
    lateinit var etCity: TextInputEditText
    lateinit var etPincode: TextInputEditText
    lateinit var checkIsMerchant: CheckBox


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
        IamFor = arguments?.getString("IamFor").toString()

        datePicker = DatePickerHelper(requireActivity(), true)

        layoutLoader = rootView.findViewById(R.id.layoutLoader)
        val btnSignUp: Button = rootView.findViewById(R.id.btnSignUp)!!
        etName = rootView.findViewById(R.id.etName)
        etEmail = rootView.findViewById(R.id.etEmail)
        etMobileNo = rootView.findViewById(R.id.etMobileNo)
        txtDob = rootView.findViewById(R.id.txtDob)
        etPassword = rootView.findViewById(R.id.etPassword)
        etConfirmPassword = rootView.findViewById(R.id.etConfirmPassword)
        etAddress = rootView.findViewById(R.id.etAddress)
        etCity = rootView.findViewById(R.id.etCity)
        etPincode = rootView.findViewById(R.id.etPincode)
        checkIsMerchant = rootView.findViewById(R.id.checkIsMerchant)
        imgElectricityBill = rootView.findViewById(R.id.imgElectricityBill)!!
        imgAdhar = rootView.findViewById(R.id.imgAdhar)!!

        val tvTnc: TextView = rootView.findViewById(R.id.tvTnc)
        val checkTnc: CheckBox = rootView.findViewById(R.id.checkTnc)

        tvTnc.setOnClickListener {
            var bundle =  Bundle()
            bundle.putString("url",Constants.TNC)
            (context as AuthActivity).openWebView(bundle)
        }

//        val navController = activity?.let { Navigation.findNavController(it, R.id.navFragment) }


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

        imgAdhar.setOnClickListener {
            imageType = 1
            getImage()
        }
        imgElectricityBill.setOnClickListener {
            imageType = 2
            getImage()
        }

        btnSignUp.setOnClickListener {

            if (etName.text.toString().equals("")) {
                requireActivity().showToastMsg("Enter Name")
            } else if (etEmail.text.toString().equals("")) {
                requireActivity().showToastMsg("Enter Email Id")
            } else if (etMobileNo.text.toString().equals("")) {
                requireActivity().showToastMsg("Enter Mobile No")
            }
//            else if (etPassword.text.toString().equals("")) {
//                requireActivity().showToastMsg("Enter Password")
//            }
//            else if (etConfirmPassword.text.toString().equals("")) {
//                requireActivity().showToastMsg("Enter Confirm Password ")
//            }
//            else if (etAddress.text.toString().equals("")) {
//                requireActivity().showToastMsg("Enter Address")
//            } else if (etCity.text.toString().equals("")) {
//                requireActivity().showToastMsg("Enter City")
//            } else if (etPincode.text.toString().equals("")) {
//                requireActivity().showToastMsg("Enter PinCode ")
//            }
//            else if (adharFile == null) {
//                requireActivity().showToastMsg("Please add Adhar card Photo. ")
//            } else if (electricityBillFile == null) {
//                requireActivity().showToastMsg("Please add Electricity Bill Photo. ")
//            }
            else {
//                var address =
//                    etAddress.text.toString() + " " + etCity.text.toString() + "" + etPincode.text.toString()
//                latlong = getLocationFromAddress(requireContext(), address)!!
                if (isEmailValid(etEmail.text.toString()) != true) {
                    requireActivity().showToastMsg("Email is not valid")
                }
//                else if (!etPassword.text.toString().equals(etConfirmPassword.text.toString())) {
//                    requireActivity().showToastMsg("Passwords are not matched")
//                }
                else {
                    if (checkIsMerchant.isChecked) {
                        userType = "Merchant"
                    }

                    if (checkTnc.isChecked) {
//                        layoutLoader.visibility = View.VISIBLE
//                        callRegisterAPIWithImg()
//                        callRegisterAPI()

                        val bundle = Bundle()
                        bundle.putString("userType", userType)
                        bundle.putString("name", etName.text.toString())
                        bundle.putString("mobile", etMobileNo.text.toString())
                        bundle.putString("email", etEmail.text.toString())
                        bundle.putString("password", etPassword.text.toString())
                        (context as AuthActivity).openDOBFragment(bundle)
                    }
                    else {
                        requireActivity().showToastMsg("Please accept terms and conditions")
                    }

                }
            }
        }
    }

    private fun callRegisterAPI() {
        val method = "userRegister"
        val jsonObject = JSONObject()
        try {
            jsonObject.put("method", method)
            jsonObject.put("userType", userType)
            jsonObject.put("name", etName.text.toString())
            jsonObject.put("mobile", etMobileNo.text.toString())
            jsonObject.put("dob", txtDob.text.toString())
            jsonObject.put("email", etEmail.text.toString())
            jsonObject.put("password", etPassword.text.toString())
            jsonObject.put("address", etAddress.text.toString())
            jsonObject.put("city", etCity.text.toString())
            jsonObject.put("pincode", etPincode.text.toString())
            jsonObject.put("latitude", latlong.latitude.toString())
            jsonObject.put("longitude", latlong.longitude.toString())
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        apiPostCall(Constants.BASE_URL, jsonObject, this, method)
    }

    private fun callRegisterAPIWithImg() {
        val method = "registerWithImage"
        val androidNetworking = AndroidNetworking.upload(Constants.BASE_URL)

        if (adharFile?.isFile == true) {
            androidNetworking.addMultipartFile("adharFile", adharFile)
        }
        if (electricityBillFile?.isFile == true) {
            androidNetworking.addMultipartFile("electricityBillFile", electricityBillFile)
        }

        androidNetworking.addMultipartParameter("name", etName.text.toString())
        androidNetworking.addMultipartParameter("userType", userType)
        androidNetworking.addMultipartParameter("email", etEmail.text.toString())
        androidNetworking.addMultipartParameter("mobile", etMobileNo.text.toString())
        androidNetworking.addMultipartParameter("dob", txtDob.text.toString())
        androidNetworking.addMultipartParameter("password", etConfirmPassword.text.toString())
        androidNetworking.addMultipartParameter("address", etAddress.text.toString())
        androidNetworking.addMultipartParameter("city", etCity.text.toString())
        androidNetworking.addMultipartParameter("pincode", etPincode.text.toString())
        androidNetworking.addMultipartParameter("latitude", latlong.latitude.toString())
        androidNetworking.addMultipartParameter("longitude", latlong.longitude.toString())
        androidNetworking.addMultipartParameter("adharImgUrl", adharPath)
        androidNetworking.addMultipartParameter("electricityBillUrl", electricityBillPath)
        androidNetworking.addMultipartParameter("method", method)
        androidNetworking.setTag(method)
        androidNetworking.setPriority(Priority.HIGH)
        androidNetworking.build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(data: JSONObject) {
                    try {
                        if (data.get("Response").equals("USER_ALREADY_EXIST")) {
                            requireActivity().showToastMsg("User details already exist")
                        } else if (data.get("Response").equals("FAILED")) {
                            requireActivity().showToastMsg("Registration failed")
                        } else if (data.get("Response").equals("FAILED_IMAGE")) {
                            requireActivity().showToastMsg("Failed to upload image")
                        } else {
                            if (data.get("Response") is JSONArray) {
                                val userData = data.getJSONArray("Response")
                                requireActivity().showToastMsg("Registration successfully done")
                                //do logout
                                SharePreferenceManager.getInstance(requireContext())
                                    .clearSharedPreference(requireContext())
                                val gson = Gson()
                                val type = object : TypeToken<ArrayList<UserModel>>() {}.type
                                var user: ArrayList<UserModel>? =
                                    gson.fromJson(userData.toString(), type)
                                Log.d("user", "" + user)
                                SharePreferenceManager.getInstance(requireActivity())
                                    .save(Constants.ISLOGIN, true)
                                SharePreferenceManager.getInstance(requireActivity())
                                    .saveUserLogin(Constants.USERDATA, user)
                                SharePreferenceManager.getInstance(requireContext())
                                    .save(Constants.EARNED_POINTS, user!![0].wallet)
                                SharePreferenceManager.getInstance(requireContext()).save(Constants.FIRST_FREE_ORDER_COMPLETE, user!![0].firstFreeOrder)
                                requireActivity().checkLogin()
                            }

                        }
                        layoutLoader.visibility = View.GONE
                    } catch (e: java.lang.Exception) {
                        e.message
                        requireContext().showToastMsg("Exception: " + e.message)
                    }
                }

                override fun onError(error: ANError) {
                    layoutLoader.visibility = View.GONE
                    error.errorDetail
                    requireContext().showToastMsg("Error: " + error.errorDetail)
                }
            })
    }

    fun getImage() {
        ImagePicker.with(this)
            .crop()                    // Crop image(Optional), Check Customization for more option
            .saveDir(requireActivity().filesDir.path + File.separator + "Images/")

            .compress(1024)            //Final image size will be less than 1 MB(Optional)
            .maxResultSize(
                1080,
                1080
            )    //Final image resolution will be less than 1080 x 1080(Optional)
            .start()

//        Pix.start(this, Options.init().setRequestCode(100))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            val fileUri = data?.data
            if (imageType == 1) {
                adharFile = null
                adharFile = ImagePicker.getFile(data)!!
                adharPath = ImagePicker.getFilePath(data)!!
                imgAdhar.setImageURI(fileUri)
            } else if (imageType == 2) {
                electricityBillFile = null
                electricityBillFile = ImagePicker.getFile(data)!!
                electricityBillPath = ImagePicker.getFilePath(data)!!
                imgElectricityBill.setImageURI(fileUri)
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

            SharePreferenceManager.getInstance(requireContext()).save(
                Constants.EARNED_POINTS,
                user!![0].wallet
            )

            requireActivity().checkLogin()
//            requireActivity().openActivity(UserHomeActivity::class.java)
        }
        layoutLoader.visibility = View.GONE
    }

    override fun onFailure(message: String) {
        requireActivity().showToastMsg(message)
        layoutLoader.visibility = View.GONE
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PermUtil.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getImage()
                } else {
                    requireContext().showToastMsg("Approve permissions to open Pix ImagePicker.")
                }
                return
            }
        }
    }

}