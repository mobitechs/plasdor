package com.plasdor.app.view.fragment.merchant

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.plasdor.app.R
import com.plasdor.app.adapter.MerchantHomeMenuAdapter
import com.plasdor.app.callbacks.MerchantHomeMenuClickListener
import com.plasdor.app.model.HomeMenuItems
import com.plasdor.app.session.SharePreferenceManager
import com.plasdor.app.utils.Constants
import com.plasdor.app.view.activity.MerchantHomeActivity


class MerchantFragmentHome : Fragment(), MerchantHomeMenuClickListener {
    lateinit var listAdapter: MerchantHomeMenuAdapter
    var listItems = ArrayList<HomeMenuItems>()
    lateinit var mLayoutManager: GridLayoutManager


    var userId = ""
    var userType = ""

    lateinit var rootView: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.merchant_fragment_home, container, false)
        initView()
        return rootView
    }

    private fun initView() {

        userId =
            SharePreferenceManager.getInstance(requireContext()).getUserLogin(Constants.USERDATA)
                ?.get(0)?.userId.toString()

        userType =
            SharePreferenceManager.getInstance(requireContext()).getUserLogin(Constants.USERDATA)
                ?.get(0)?.userType.toString()

        setupRecyclerView()
        getMerchantMenuList()


    }

    private fun getMerchantMenuList() {
        listItems.add(HomeMenuItems("1","All Products",R.drawable.product))
        listItems.add(HomeMenuItems("2","My Products",R.drawable.my_product))
        listItems.add(HomeMenuItems("3","Orders",R.drawable.orders))
        listItems.add(HomeMenuItems("4","Profile",R.drawable.user))

        listAdapter.updateListItems(listItems)
    }

    private fun setupRecyclerView() {
        val recyclerView: RecyclerView = rootView.findViewById(R.id.recyclerView)!!
        val progressBar: ProgressBar = rootView.findViewById(R.id.progressBar)!!

        mLayoutManager = GridLayoutManager(requireActivity(), 2)
        recyclerView.layoutManager = mLayoutManager
        recyclerView.itemAnimator = DefaultItemAnimator()

        listAdapter = MerchantHomeMenuAdapter(
            requireActivity(),
            this
        )
        recyclerView.adapter = listAdapter
    }

    override fun selectProduct(item: HomeMenuItems, position: Int) {
        (context as MerchantHomeActivity).openPage(item.menuId)
    }


}