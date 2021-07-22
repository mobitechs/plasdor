package com.plasdor.app.view.fragment.user

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import com.plasdor.app.R
import com.plasdor.app.view.activity.UserHomeActivity


class RentOnPlasdorFragment : Fragment() {


    lateinit var  rootView:View
    lateinit var  btnStartRentOn: AppCompatTextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootView= inflater.inflate(R.layout.fragment_rent_on_plasdor, container, false)
        initView()
        return rootView
    }

    private fun initView() {
        btnStartRentOn = rootView.findViewById(R.id.btnStartRentOn)

        btnStartRentOn.setOnClickListener {
            var bundle = Bundle()
            bundle.putString("IamFor", "RentOn")
            (context as UserHomeActivity).OpenRegisterFragmentForRentOn(bundle)

        }
    }


}