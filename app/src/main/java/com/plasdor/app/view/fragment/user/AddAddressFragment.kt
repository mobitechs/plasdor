package com.plasdor.app.view.fragment.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import com.plasdor.app.R
import com.plasdor.app.callbacks.ApiResponse
import com.plasdor.app.model.AddressListItems
import com.plasdor.app.session.SharePreferenceManager
import com.plasdor.app.utils.Constants
import com.plasdor.app.utils.apiPostCall
import com.plasdor.app.utils.showToastMsg
import com.plasdor.app.view.activity.UserHomeActivity
import org.json.JSONException
import org.json.JSONObject

class AddAddressFragment : Fragment(), ApiResponse {

    lateinit var listItem: AddressListItems
    lateinit var rootView: View
    var userId = ""
    lateinit var etAddress: TextInputEditText
    lateinit var etLandmark: TextInputEditText
    lateinit var etArea: TextInputEditText
    lateinit var etCity: TextInputEditText
    lateinit var etPincode: TextInputEditText
    lateinit var layoutLoader: RelativeLayout

    var imFor = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_add_address, container, false)
        initView()
        return rootView
    }

    private fun initView() {
        imFor = arguments?.getString("IamFor")!!

        if (SharePreferenceManager.getInstance(requireContext()).getUserLogin(Constants.USERDATA)!!
                .get(0).userId != null
        ) {
            userId = SharePreferenceManager.getInstance(requireContext())
                .getUserLogin(Constants.USERDATA)!!.get(0).userId
        }

        val btnSubmit: Button = rootView.findViewById(R.id.btnSubmit)!!
        etAddress = rootView.findViewById(R.id.etAddress)!!
        etLandmark = rootView.findViewById(R.id.etLandmark)
        etArea = rootView.findViewById(R.id.etArea)
        etCity = rootView.findViewById(R.id.etCity)
        etPincode = rootView.findViewById(R.id.etPincode)
        layoutLoader = rootView.findViewById(R.id.layoutLoader)

        if (imFor.equals("Edit")) {
            listItem = arguments?.getParcelable("addressDetailsItem")!!
            setAddressForEdit()
        }

        btnSubmit.setOnClickListener {

            if (etAddress.text.toString().equals("")) {
                requireActivity().showToastMsg("Enter Name")
            } else if (etArea.text.toString().equals("")) {
                requireActivity().showToastMsg("Enter Mobile No")
            } else if (etCity.text.toString().equals("")) {
                requireActivity().showToastMsg("Enter Password")
            } else if (etPincode.text.toString().equals("")) {
                requireActivity().showToastMsg("Enter Confirm Password ")
            } else {
                layoutLoader.visibility = View.VISIBLE
                val jsonObject = JSONObject()
                var method = ""
                if (imFor.equals("Add")) {
                    method = "AddAddress"
                } else {
                    method = "UpdateAddress"
                    jsonObject.put("addressId", listItem.addressId)
                }


                try {
                    jsonObject.put("method", method)
                    jsonObject.put("address", etAddress.text.toString())
                    jsonObject.put("landMark", etLandmark.text.toString())
                    jsonObject.put("area", etArea.text.toString())
                    jsonObject.put("city", etCity.text.toString())
                    jsonObject.put("pincode", etPincode.text.toString())
                    jsonObject.put("userId", userId)
                    jsonObject.put("clientBusinessId", Constants.clientBusinessId)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                apiPostCall(Constants.BASE_URL, jsonObject, this, method)


            }
        }
    }

    private fun setAddressForEdit() {
        etAddress.setText(listItem.address)
        etLandmark.setText(listItem.landMark)
        etArea.setText(listItem.area)
        etCity.setText(listItem.city)
        etPincode.setText(listItem.pincode)

    }

    override fun onSuccess(data: Any, tag: String) {

        if (data.equals("Failed to add address")) {
            requireActivity().showToastMsg("Failed to add address")
        } else {
            if (imFor.equals("Add")) {
                requireActivity().showToastMsg("Address successfully added")
            } else {
                requireActivity().showToastMsg("Address successfully updated")
            }
            layoutLoader.visibility = View.GONE
            (activity as UserHomeActivity)!!.OpenAddressList()
        }

    }

    override fun onFailure(message: String) {
        layoutLoader.visibility = View.GONE
        requireActivity().showToastMsg(message)
    }
}
