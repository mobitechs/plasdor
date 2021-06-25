package com.plasdor.app.view.fragment.merchant

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.plasdor.app.R

class MerchantFragmentOrderList : Fragment() {
    lateinit var rootView: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.merchant_fragment_order_list, container, false)
        initView()
        return rootView
    }

    private fun initView() {

    }


}