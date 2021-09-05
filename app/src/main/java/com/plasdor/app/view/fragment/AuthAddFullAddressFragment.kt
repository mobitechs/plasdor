package com.plasdor.app.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import com.plasdor.app.R
import com.plasdor.app.utils.showToastMsg
import com.plasdor.app.view.activity.AuthActivity


class AddFullAddressFragment : Fragment() {

    lateinit var rootView:View
    lateinit var etAddress: AppCompatEditText
    lateinit var btnSignUp: AppCompatButton
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        rootView= inflater.inflate(R.layout.fragment_auth_add_full_address, container, false)
        initView()
        return rootView
    }

    private fun initView() {

        etAddress = rootView.findViewById(R.id.etAddress)
        btnSignUp = rootView.findViewById(R.id.btnSignUp)
        btnSignUp.setOnClickListener {
            if(etAddress.text.toString().equals("")){
                requireActivity().showToastMsg("Please enter your address")
            }else{
                val bundle = Bundle()
                bundle.putString("userType", arguments?.getString("userType").toString())
                bundle.putString("name", arguments?.getString("name").toString())
                bundle.putString("mobile", arguments?.getString("mobile").toString())
                bundle.putString("email", arguments?.getString("email").toString())
                bundle.putString("password", arguments?.getString("password").toString())
                bundle.putString("dob", arguments?.getString("dob").toString())
                bundle.putString("address", etAddress.text.toString())
                (context as AuthActivity).openCityPincodeFragment(bundle)
            }

        }
    }


}