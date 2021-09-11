package com.plasdor.app.view.fragment

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.Fragment
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.bumptech.glide.Glide
import com.fxn.utility.PermUtil
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.plasdor.app.R
import com.plasdor.app.model.UserModel
import com.plasdor.app.session.SharePreferenceManager
import com.plasdor.app.utils.Constants
import com.plasdor.app.utils.checkLogin
import com.plasdor.app.utils.setImage
import com.plasdor.app.utils.showToastMsg
import org.json.JSONArray
import org.json.JSONObject
import java.io.File


class UpdateKycFragment : Fragment() {

    lateinit var rootView: View
    lateinit var btnUpdate: AppCompatButton
    lateinit var layoutLoader: RelativeLayout
    lateinit var imgElectricityBill: AppCompatImageView
    lateinit var imgAdhar: AppCompatImageView
    lateinit var imgPassbook: AppCompatImageView
    lateinit var layoutPassbook: RelativeLayout
    lateinit var btnUpdateAdhar: AppCompatButton
    lateinit var btnUpdateElectricityBill: AppCompatButton
    lateinit var btnUpdatePassbook: AppCompatButton
    var imageType = 0

    var passbookPath = ""
    var passbookFile: File? = null

    var adharPath = ""
    var adharFile: File? = null

    var electricityBillPath = ""
    var electricityBillFile: File? = null
    var userId = ""
    var userType = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_update_kyc, container, false)
        initView()
        return rootView
    }

    private fun initView() {

        userId =
            SharePreferenceManager.getInstance(requireActivity()).getUserLogin(Constants.USERDATA)
                ?.get(0)?.userId.toString()
        userType =
            SharePreferenceManager.getInstance(requireActivity()).getUserLogin(Constants.USERDATA)
                ?.get(0)?.userType.toString()

        layoutLoader = rootView.findViewById(R.id.layoutLoader)!!
        imgElectricityBill = rootView.findViewById(R.id.imgElectricityBill)!!
        imgAdhar = rootView.findViewById(R.id.imgAdhar)!!
        imgPassbook = rootView.findViewById(R.id.imgPassbook)!!
        layoutPassbook = rootView.findViewById(R.id.layoutPassbook)!!
        btnUpdate = rootView.findViewById(R.id.btnUpdate)!!
        btnUpdateAdhar = rootView.findViewById(R.id.btnUpdateAdhar)!!
        btnUpdatePassbook = rootView.findViewById(R.id.btnUpdatePassbook)!!
        btnUpdateElectricityBill = rootView.findViewById(R.id.btnUpdateElectricityBill)!!

        passbookPath =
            SharePreferenceManager.getInstance(requireActivity()).getUserLogin(Constants.USERDATA)
                ?.get(0)?.imgPathAvatar.toString()
        adharPath =
            SharePreferenceManager.getInstance(requireActivity()).getUserLogin(Constants.USERDATA)
                ?.get(0)?.imgPathAdhar.toString()
        electricityBillPath =
            SharePreferenceManager.getInstance(requireActivity()).getUserLogin(Constants.USERDATA)
                ?.get(0)?.imgPathEBill.toString()

        imgPassbook.setImage(adharPath,R.drawable.img_not_available)
        imgAdhar.setImage(adharPath,R.drawable.img_not_available)
        imgElectricityBill.setImage(electricityBillPath,R.drawable.img_not_available)

        if (userType.equals(Constants.MERCHANT)) {
            layoutPassbook.visibility = View.VISIBLE
        } else {
            layoutPassbook.visibility = View.GONE
        }
        btnUpdatePassbook.setOnClickListener {
            imageType = 0
            getImage()
        }
        btnUpdateAdhar.setOnClickListener {
            imageType = 1
            getImage()
        }
        btnUpdateElectricityBill.setOnClickListener {
            imageType = 2
            getImage()
        }

        btnUpdate.setOnClickListener {
            callUpdateProfileAPI()
        }
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

            if (imageType == 0) {
                passbookFile = null
                passbookFile = ImagePicker.getFile(data)!!
                passbookPath = ImagePicker.getFilePath(data)!!
//                imgPassbook.setImageURI(fileUri)

                Glide.with(this)
                    .load(fileUri)
                    .into(imgPassbook);
            } else if (imageType == 1) {
                adharFile = null
                adharFile = ImagePicker.getFile(data)!!
                adharPath = ImagePicker.getFilePath(data)!!
//                imgAdhar.setImageURI(fileUri)
                Glide.with(this)
                    .load(fileUri)
                    .into(imgAdhar);

            } else if (imageType == 2) {
                electricityBillFile = null
                electricityBillFile = ImagePicker.getFile(data)!!
                electricityBillPath = ImagePicker.getFilePath(data)!!
                //imgElectricityBill.setImageURI(fileUri)
                Glide.with(this)
                    .load(fileUri)
                    .into(imgElectricityBill);
            }

        }
    }


    private fun callUpdateProfileAPI() {
        layoutLoader.visibility = View.VISIBLE
        val method = "updateKyc"
        val androidNetworking = AndroidNetworking.upload(Constants.BASE_URL)
        if (passbookFile?.isFile == true) {
            androidNetworking.addMultipartFile("passbookFile", passbookFile)
        }
        if (adharFile?.isFile == true) {
            androidNetworking.addMultipartFile("adharFile", adharFile)
        }
        if (electricityBillFile?.isFile == true) {
            androidNetworking.addMultipartFile("electricityBillFile", electricityBillFile)
        }

        androidNetworking.addMultipartParameter("userId", userId)
        androidNetworking.addMultipartParameter("passbookUrl", passbookPath)
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
                            requireActivity().showToastMsg("KYC Successfully Updated")

                            if (data.get("Response") is JSONArray) {
                                val userData = data.getJSONArray("Response")

                                val gson = Gson()
                                val type = object : TypeToken<ArrayList<UserModel>>() {}.type
                                var user: ArrayList<UserModel>? =
                                    gson.fromJson(userData.toString(), type)

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