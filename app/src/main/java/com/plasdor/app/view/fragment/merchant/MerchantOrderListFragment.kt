package com.plasdor.app.view.fragment.merchant

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.plasdor.app.R
import com.plasdor.app.adapter.MerchantOrderListAdapter
import com.plasdor.app.adapter.UserOrderListAdapter
import com.plasdor.app.model.MyOrderListItems
import com.plasdor.app.session.SharePreferenceManager
import com.plasdor.app.utils.Constants
import com.plasdor.app.viewModel.MerchantListViewModel
import com.plasdor.app.viewModel.UserListViewModel


class MerchantOrderListFragment : Fragment() {

    lateinit var rootView: View
    lateinit var viewModelUser: MerchantListViewModel
    lateinit var listAdapter: MerchantOrderListAdapter
    var merchantOrderListItems = ArrayList<MyOrderListItems>()
    lateinit var mLayoutManager: LinearLayoutManager
    var userId = ""
    var userType = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_order_list_merchant, container, false)
        intView()
        return rootView

    }

    private fun intView() {
        userType =
            SharePreferenceManager.getInstance(requireContext()).getUserLogin(Constants.USERDATA)
                ?.get(0)?.userType.toString()
        userId =
            SharePreferenceManager.getInstance(requireContext()).getUserLogin(Constants.USERDATA)
                ?.get(0)?.userId.toString()

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        viewModelUser = ViewModelProvider(this).get(MerchantListViewModel::class.java)
        val recyclerView: RecyclerView = rootView.findViewById(R.id.recyclerView)!!
        val progressBar: ProgressBar = rootView.findViewById(R.id.progressBar)!!

        mLayoutManager = LinearLayoutManager(requireActivity())
        recyclerView.layoutManager = mLayoutManager
        recyclerView.itemAnimator = DefaultItemAnimator()

        listAdapter = MerchantOrderListAdapter(requireActivity(), userType)
        recyclerView.adapter = listAdapter
        listAdapter.updateListItems(merchantOrderListItems)

        viewModelUser.showProgressBar.observe(requireActivity(), Observer {
            if (it) {
                progressBar.visibility = View.VISIBLE
            } else {
                progressBar.visibility = View.GONE
            }
        })

        viewModelUser.getMerchantOrderList(userId)

        viewModelUser.merchantOrderListItems.observe(requireActivity(), Observer {
            listAdapter.updateListItems(it)
        })

    }


}