package com.plasdor.app.view.fragment

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.Fragment
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.bumptech.glide.Glide
import com.fxn.utility.PermUtil
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.plasdor.app.R
import com.plasdor.app.callbacks.ApiResponse
import com.plasdor.app.model.UserModel
import com.plasdor.app.session.SharePreferenceManager
import com.plasdor.app.utils.*
import de.hdodenhof.circleimageview.CircleImageView
import org.json.JSONArray
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

    var adharPath = ""
    var adharFile: File? = null

    var electricityBillPath = ""
    var electricityBillFile: File? = null

    lateinit var imgAvatar: CircleImageView
    lateinit var tvName: AppCompatEditText
    lateinit var tvEmail: TextView
    lateinit var tvMobile: AppCompatEditText
    lateinit var txtAddress: AppCompatEditText
    lateinit var txtCity: AppCompatEditText
    lateinit var txtPinCode: AppCompatEditText

    lateinit var btnUpdate: Button
    lateinit var layoutLoader: RelativeLayout
    lateinit var imgElectricityBill: AppCompatImageView
    lateinit var imgAdhar: AppCompatImageView

    var imageType = 0

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
        imgElectricityBill = rootView.findViewById(R.id.imgElectricityBill)!!
        imgAdhar = rootView.findViewById(R.id.imgAdhar)!!

        updadteUI()

        imgAvatar.setOnClickListener {
            imageType = 0
            getImage()
        }
        imgAdhar.setOnClickListener {
            imageType = 1
            getImage()
        }
        imgElectricityBill.setOnClickListener {
            imageType = 2
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
                callUpdateProfileAPI()
//                profileUpdateAPI()
            }
        }
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

        avatarPath =
            SharePreferenceManager.getInstance(requireActivity()).getUserLogin(Constants.USERDATA)
                ?.get(0)?.imgPathAvatar.toString()
        adharPath =
            SharePreferenceManager.getInstance(requireActivity()).getUserLogin(Constants.USERDATA)
                ?.get(0)?.imgPathAdhar.toString()
        electricityBillPath =
            SharePreferenceManager.getInstance(requireActivity()).getUserLogin(Constants.USERDATA)
                ?.get(0)?.imgPathEBill.toString()

        if (avatarPath != null || avatarPath != "null") {
            //setImage()
        }
        if (adharPath != null || adharPath != "null" || adharPath != "") {
            imgAdhar.setImage(adharPath,R.drawable.img_not_available)
        }
        if (electricityBillPath != null || electricityBillPath != "null" || electricityBillPath != "") {
            imgElectricityBill.setImage(electricityBillPath,R.drawable.img_not_available)
        }

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
//            val returnValue = data!!.getStringArrayListExtra(Pix.IMAGE_RESULTS)
//            val abc = "file://" + returnValue!![0]
//            val fileUri = Uri.parse(abc)
            val fileUri = data?.data


            if (imageType == 0) {
                avatarFile = null
//                avatarFile = File(fileUri.path)
                avatarFile = ImagePicker.getFile(data)!!
                avatarPath = ImagePicker.getFilePath(data)!!
               // imgAvatar.setImageURI(null)
                imgAvatar.setImageURI(fileUri)
            }
            else if (imageType == 1) {
                adharFile = null
//                adharFile = File(fileUri.path)
                adharFile = ImagePicker.getFile(data)!!
                adharPath = ImagePicker.getFilePath(data)!!
               // imgAdhar.setImageURI(null)
                imgAdhar.setImageURI(fileUri)

            }
            else if (imageType == 2) {
                electricityBillFile = null
//                electricityBillFile = File(fileUri.path)
                electricityBillFile = ImagePicker.getFile(data)!!
                electricityBillPath = ImagePicker.getFilePath(data)!!
//                imgElectricityBill.setImageURI(null)
                imgElectricityBill.setImageURI(fileUri)

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

    private fun callUpdateProfileAPI() {
        var address =
            txtAddress.text.toString() + " " + txtCity.text.toString() + "" + txtPinCode.text.toString()
        var latlong: LatLng = getLocationFromAddress(requireContext(), address)!!

        val method = "editProfileWithImage"
        val androidNetworking = AndroidNetworking.upload(Constants.BASE_URL)
        if (avatarFile?.isFile == true) {
            androidNetworking.addMultipartFile("avatarFile", avatarFile)
        }
        if (adharFile?.isFile == true) {
            androidNetworking.addMultipartFile("adharFile", adharFile)
        }
        if (electricityBillFile?.isFile == true) {
            androidNetworking.addMultipartFile("electricityBillFile", electricityBillFile)
        }

        //AndroidNetworking.upload(Constants.BASE_URL)
//            .addMultipartFile("avatarFile", avatarFile)
//            .addMultipartFile("adharFile", adharFile)
//            .addMultipartFile("electricityBillFile", electricityBillFile)
        androidNetworking.addMultipartParameter("userId", userId)
        androidNetworking.addMultipartParameter("name", name)
        androidNetworking.addMultipartParameter("mobile", mobile)
        androidNetworking.addMultipartParameter("address", address)
        androidNetworking.addMultipartParameter("city", city)
        androidNetworking.addMultipartParameter("pinCode", pinCode)
        androidNetworking.addMultipartParameter("latitude", latlong.latitude.toString())
        androidNetworking.addMultipartParameter("longitude", latlong.longitude.toString())
        androidNetworking.addMultipartParameter("userImgUrl", avatarPath)
        androidNetworking.addMultipartParameter("adharImgUrl", adharPath)
        androidNetworking.addMultipartParameter("electricityBillUrl", electricityBillPath)
        androidNetworking.addMultipartParameter("method", method)
        androidNetworking.setTag(method)
        androidNetworking.setPriority(Priority.HIGH)
        androidNetworking.build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(data: JSONObject) {
                    layoutLoader.visibility = View.GONE
                    try {
                        if (data.equals("FAILED") || data.equals("FAILED_IMAGE")) {
                            requireActivity().showToastMsg("failed to update profile")
                        } else {
                            requireActivity().showToastMsg("Profile Successfully Updated")

                            if (data.get("Response") is JSONArray) {
                                val userData = data.getJSONArray("Response")

                                val gson = Gson()
                                val type = object : TypeToken<ArrayList<UserModel>>() {}.type
                                var user: ArrayList<UserModel>? = gson.fromJson(userData.toString(), type)

                                Log.d("user", "" + user)
                                SharePreferenceManager.getInstance(requireActivity())
                                    .save(Constants.ISLOGIN, true)
                                SharePreferenceManager.getInstance(requireActivity())
                                    .saveUserLogin(Constants.USERDATA, user)

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

    override fun onSuccess(data: Any, tag: String) {
        if (data.equals("FAILED") || data.equals("FAILED_IMAGE")) {
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