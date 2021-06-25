package com.plasdor.app.view.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatSpinner
import androidx.fragment.app.Fragment
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.textfield.TextInputEditText
import com.plasdor.app.R
import com.plasdor.app.model.ProductListItems
import com.plasdor.app.utils.Constants
import com.plasdor.app.utils.setImage
import com.plasdor.app.utils.showToastMsg
import com.plasdor.app.view.activity.UserHomeActivity
import org.json.JSONObject
import java.io.File

class AdminProductEditFragment : Fragment() {

    lateinit var rootView: View
    var userId = ""
    var userType = ""
    var clientBusinessId = ""
    var productId = ""
    var categoryId = ""
    var categoryName = ""
    var productName = ""
    var description = ""
    var amount = ""
    var finalAmount = ""
    var uniteType = ""
    var weight = ""
    var productImgPath = ""
    var productImgFile: File? = null

    lateinit var productImg: AppCompatImageView
    lateinit var etProductName: TextInputEditText
    lateinit var etDescription: TextInputEditText
    lateinit var etAmount: TextInputEditText
    lateinit var etFinalAmount: TextInputEditText
    lateinit var spinnerUniteType: AppCompatSpinner
    lateinit var spinnerWeight: AppCompatSpinner
    lateinit var spinnerCategory: AppCompatSpinner
    lateinit var btnSubmit: Button
    lateinit var btnUpdateImg: Button
    lateinit var layoutLoader: RelativeLayout
    var spinnerItemArray = Constants.weightArray

    lateinit var listItem: ProductListItems

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        rootView = inflater.inflate(R.layout.fragment_admin_product_edit, container, false)
        initView()
        return rootView
    }

    private fun initView() {
        listItem = arguments?.getParcelable("item")!!

        etProductName = rootView.findViewById(R.id.etProductName)!!
        etDescription = rootView.findViewById(R.id.etDescription)!!
        etAmount = rootView.findViewById(R.id.etAmount)!!
        etFinalAmount = rootView.findViewById(R.id.etFinalAmount)!!
        spinnerUniteType = rootView.findViewById(R.id.spinnerUniteType)!!
        spinnerWeight = rootView.findViewById(R.id.spinnerWeight)!!
        spinnerCategory = rootView.findViewById(R.id.spinnerCategory)!!
        btnSubmit = rootView.findViewById(R.id.btnSubmit)!!
        btnUpdateImg = rootView.findViewById(R.id.btnUpdateImg)!!
        productImg = rootView.findViewById(R.id.productImg)!!
        layoutLoader = rootView.findViewById(R.id.layoutLoader)!!



        productImgPath = listItem.img
        if (productImgPath == null || productImgPath == "") {
            productImg!!.background =
                requireContext().resources.getDrawable(R.drawable.img_not_available)
        } else {
            productImg!!.setImage(productImgPath)
        }

        setupSpinnerUniteType()
        setupSpinnerCategory()

        btnUpdateImg.setOnClickListener {
            getImage()
        }

        btnSubmit.setOnClickListener {
            productName = etProductName.text.toString()
            amount = etAmount.text.toString()
            finalAmount = etFinalAmount.text.toString()

            if (productName.equals("")) {
                requireContext().showToastMsg("Product name cannot be empty")
            } else if (amount.equals("")) {
                requireContext().showToastMsg("Amount cannot be empty")
            } else if (finalAmount.equals("")) {
                requireContext().showToastMsg("Discounted amount cannot be empty")
            } else {
                layoutLoader.visibility = View.VISIBLE
                callUpdateProductAPI()
            }
        }
    }

    private fun setupSpinnerCategory() {
        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.spinner_layout,
            Constants.categoryArray
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.setAdapter(adapter)

        spinnerCategory.setSelection(Constants.categoryArray.indexOf(categoryName))
        spinnerCategory.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                categoryId = (p2 + 1).toString()
            }
        })
    }

    private fun setupSpinnerWeight() {
        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.spinner_layout,
            spinnerItemArray
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerWeight.setAdapter(adapter)

        spinnerWeight.setSelection(spinnerItemArray.indexOf(weight))
        spinnerWeight.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                weight = Constants.unitTypeArray[p2]

            }
        })
    }

    private fun setupSpinnerUniteType() {
        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.spinner_layout,
            Constants.unitTypeArray

        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerUniteType.setAdapter(adapter)

        spinnerUniteType.setSelection(Constants.unitTypeArray.indexOf(uniteType))
        spinnerUniteType.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                uniteType = Constants.unitTypeArray[p2]

                if (uniteType.equals("Grams")) {
                    spinnerItemArray = Constants.weightArray
                } else {
                    spinnerItemArray = Constants.pieceArray
                }
                setupSpinnerWeight()

            }
        })
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

            productImgFile = ImagePicker.getFile(data)!!
            val fileUri = data?.data
            productImg.setImageURI(null)
            productImg.setImageURI(fileUri)
            productImgPath = ImagePicker.getFilePath(data)!!
        }
    }

    private fun callUpdateProductAPI() {

        val method = "UpdateProduct"
        AndroidNetworking.upload(Constants.BASE_URL)
            .addMultipartFile("img", productImgFile)
            .addMultipartParameter("productImgPath", productImgPath)
            .addMultipartParameter("clientBusinessId", Constants.clientBusinessId)
            .addMultipartParameter("userId", userId)
            .addMultipartParameter("categoryId", categoryId)
            .addMultipartParameter("productId", productId)
            .addMultipartParameter("productName", productName)
            .addMultipartParameter("description", description)
            .addMultipartParameter("amount", amount)
            .addMultipartParameter("finalPrice", finalAmount)
            .addMultipartParameter("uniteType", uniteType)
            .addMultipartParameter("weight", weight)
            .addMultipartParameter("method", method)
            .setTag(method)
            .setPriority(Priority.HIGH)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {

                    try {
                        if (response.getString("Response") == "SUCCESS") {
                            requireContext().showToastMsg("Product details successfully updated.")
                            (activity as UserHomeActivity).displayView(1)

                        } else {
                            requireContext().showToastMsg("Product details update failed.")
                        }
                    } catch (e: java.lang.Exception) {
                        e.message
                        requireContext().showToastMsg("Exception: " + e.message)
                    }
                    layoutLoader.visibility = View.GONE
                }

                override fun onError(error: ANError) {
                    layoutLoader.visibility = View.GONE
                    error.errorDetail
                    requireContext().showToastMsg("Error: " + error.errorDetail)
                }
            })
    }

}