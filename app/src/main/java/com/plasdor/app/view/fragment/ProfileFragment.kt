package com.plasdor.app.view.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatEditText
import androidx.fragment.app.Fragment
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.plasdor.app.R
import com.plasdor.app.callbacks.ApiResponse
import com.plasdor.app.model.UserModel
import com.plasdor.app.session.SharePreferenceManager
import com.plasdor.app.utils.Constants
import com.plasdor.app.utils.apiPostCall
import com.plasdor.app.utils.checkLogin
import com.plasdor.app.utils.showToastMsg
import de.hdodenhof.circleimageview.CircleImageView
import org.json.JSONException
import org.json.JSONObject
import java.io.File


class ProfileFragment : Fragment(), ApiResponse {

    lateinit var rootView: View

    var userId = ""
    var userType = ""
    var name = ""
    var email = ""
    var mobile = ""
    var password = ""
    var address = ""
    var city = ""
    var pinCode = ""
    var avatarPath = ""
    var avatarFile: File? = null

    lateinit var imgAvatar: CircleImageView
    lateinit var tvName: AppCompatEditText
    lateinit var tvEmail: TextView
    lateinit var tvMobile: AppCompatEditText
    lateinit var txtAddress: AppCompatEditText
    lateinit var txtCity: AppCompatEditText
    lateinit var txtPinCode: AppCompatEditText

    lateinit var btnUpdate: Button
    lateinit var layoutLoader: RelativeLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        rootView = inflater.inflate(R.layout.fragment_profile, container, false)
        initView()
        return rootView
    }

    private fun initView() {

        tvName = rootView.findViewById(R.id.tvName)!!
        tvEmail = rootView.findViewById(R.id.tvEmail)!!
        tvMobile = rootView.findViewById(R.id.tvMobile)!!
        txtAddress = rootView.findViewById(R.id.txtAddress)!!
        txtCity = rootView.findViewById(R.id.txtCity)!!
        txtPinCode = rootView.findViewById(R.id.txtPinCode)!!

        btnUpdate = rootView.findViewById(R.id.btnUpdate)!!
        imgAvatar = rootView.findViewById(R.id.imgAvatar)!!
        layoutLoader = rootView.findViewById(R.id.layoutLoader)!!

        updadteUI()

        imgAvatar.setOnClickListener {
            getImage()
        }

        btnUpdate.setOnClickListener {
            name = tvName.text.toString()
            mobile = tvMobile.text.toString()
            address = txtAddress.text.toString()
            city = txtCity.text.toString()
            pinCode = txtPinCode.text.toString()

            if (name.equals("")) {
                requireContext().showToastMsg("Name cannot be empty")
            } else if (mobile.equals("")) {
                requireContext().showToastMsg("Mobile cannot be empty")
            } else if (address.equals("")) {
                requireContext().showToastMsg("Address cannot be empty")
            } else if (city.equals("")) {
                requireContext().showToastMsg("City cannot be empty")
            } else if (pinCode.equals("")) {
                requireContext().showToastMsg("PinCode cannot be empty")
            } else {
                layoutLoader.visibility = View.VISIBLE
                //callUpdateProfileAPI()
                profileUpdateAPI()
            }
        }
    }

    private fun profileUpdateAPI() {
        val method = "editProfileDetails"
        val jsonObject = JSONObject()
        try {
            jsonObject.put("method", method)
            jsonObject.put("userId", userId)
            jsonObject.put("name", name)
            jsonObject.put("mobile", mobile)

            jsonObject.put("address", address)
            jsonObject.put("city", city)
            jsonObject.put("pinCode", pinCode)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        apiPostCall(Constants.BASE_URL, jsonObject, this, method)
    }

    private fun updadteUI() {
        userId =
            SharePreferenceManager.getInstance(requireActivity()).getUserLogin(Constants.USERDATA)
                ?.get(0)?.userId.toString()
        userType =
            SharePreferenceManager.getInstance(requireActivity()).getUserLogin(Constants.USERDATA)
                ?.get(0)?.userType.toString()
        name =
            SharePreferenceManager.getInstance(requireActivity()).getUserLogin(Constants.USERDATA)
                ?.get(0)?.name.toString()
        email =
            SharePreferenceManager.getInstance(requireActivity()).getUserLogin(Constants.USERDATA)
                ?.get(0)?.email.toString()
        mobile =
            SharePreferenceManager.getInstance(requireActivity()).getUserLogin(Constants.USERDATA)
                ?.get(0)?.mobile.toString()

        address =
            SharePreferenceManager.getInstance(requireActivity()).getUserLogin(Constants.USERDATA)
                ?.get(0)?.address.toString()

        city =
            SharePreferenceManager.getInstance(requireActivity()).getUserLogin(Constants.USERDATA)
                ?.get(0)?.city.toString()

        pinCode =
            SharePreferenceManager.getInstance(requireActivity()).getUserLogin(Constants.USERDATA)
                ?.get(0)?.pincode.toString()

//        avatarPath = SharePreferenceManager.getInstance(requireActivity()).getUserLogin(Constants.USERDATA)?.get(0)?.userImgPath.toString()

//        if(avatarPath != null){
//            requireContext().setImage(avatarPath, R.drawable.ic_man,imgAvatar)
//        }

        if (email.equals("")) {
            email = "Not available"
        }

        tvEmail.text = email
        tvName.setText(name)
        tvMobile.setText(mobile)
        txtAddress.setText(address)
        txtCity.setText(city)
        txtPinCode.setText(pinCode)

    }

    fun getImage() {
        ImagePicker.with(this)
            .crop()                    //Crop image(Optional), Check Customization for more option
            .compress(1024)            //Final image size will be less than 1 MB(Optional)
            .maxResultSize(
                1080,
                1080
            )    //Final image resolution will be less than 1080 x 1080(Optional)
            .start()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {

            avatarFile = ImagePicker.getFile(data)!!
            val fileUri = data?.data
            imgAvatar.setImageURI(null)
            imgAvatar.setImageURI(fileUri)
            avatarPath = ImagePicker.getFilePath(data)!!
        }
    }

    private fun callUpdateProfileAPI() {

        val method = "editProfile"
        AndroidNetworking.upload(Constants.BASE_URL)
            .addMultipartFile("imgPath", avatarFile)
            .addMultipartParameter("userId", userId)
            .addMultipartParameter("userName", name)
            .addMultipartParameter("mobileNo", mobile)
            .addMultipartParameter("password", password)
            .addMultipartParameter("userImgUrl", avatarPath)
            .addMultipartParameter("method", method)
            .setTag(method)
            .setPriority(Priority.HIGH)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    layoutLoader.visibility = View.GONE
                    try {
                        if (response.getString("Response") == "FAILED") {
                            requireContext().showToastMsg("Profile details update failed.")
                        } else {
                            requireContext().showToastMsg("Profile details successfully updated.")
                            val gson = Gson()
                            val type = object : TypeToken<ArrayList<UserModel>>() {}.type
                            var user: ArrayList<UserModel>? =
                                gson.fromJson(response.getString("Response").toString(), type)

                            SharePreferenceManager.getInstance(requireActivity())
                                .save(Constants.ISLOGIN, true)
                            SharePreferenceManager.getInstance(requireActivity())
                                .saveUserLogin(Constants.USERDATA, user)
                        }
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

    override fun onSuccess(data: Any, tag: String) {
        if (data.equals("FAILED_UPDATE")) {
            requireActivity().showToastMsg("failed to update profile")
        } else {
            requireActivity().showToastMsg("Profile Successfully Updated")
            val gson = Gson()
            val type = object : TypeToken<ArrayList<UserModel>>() {}.type
            var user: ArrayList<UserModel>? = gson.fromJson(data.toString(), type)

            Log.d("user", "" + user)
            SharePreferenceManager.getInstance(requireActivity()).save(Constants.ISLOGIN, true)
            SharePreferenceManager.getInstance(requireActivity())
                .saveUserLogin(Constants.USERDATA, user)

            requireActivity().checkLogin()
//            requireActivity().openActivity(UserHomeActivity::class.java)
        }
        layoutLoader.visibility = View.GONE
    }

    override fun onFailure(message: String) {
        layoutLoader.visibility = View.GONE
        requireContext().showToastMsg("Failure "+message)
    }
}