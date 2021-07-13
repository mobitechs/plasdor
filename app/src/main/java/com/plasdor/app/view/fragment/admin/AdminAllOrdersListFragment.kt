package com.plasdor.app.view.fragment.admin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.plasdor.app.R
import com.plasdor.app.adapter.AdminAllOrderAdapter
import com.plasdor.app.model.AdminAllOrderListItems
import com.plasdor.app.session.SharePreferenceManager
import com.plasdor.app.utils.Constants
import com.plasdor.app.viewModel.AdminViewModel

class AdminAllOrdersListFragment : Fragment() {
    lateinit var rootView: View
    lateinit var viewModelUser: AdminViewModel
    lateinit var listAdapter: AdminAllOrderAdapter
    var merchantOrderListItems = ArrayList<AdminAllOrderListItems>()
    lateinit var mLayoutManager: LinearLayoutManager
    var userId = ""
    var userType = ""
    lateinit var txtTotal: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_admin_all_orders_list, container, false)
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
        viewModelUser = ViewModelProvider(this).get(AdminViewModel::class.java)
        val recyclerView: RecyclerView = rootView.findViewById(R.id.recyclerView)!!
        val progressBar: ProgressBar = rootView.findViewById(R.id.progressBar)!!
        txtTotal = rootView.findViewById(R.id.txtTotal)!!

        mLayoutManager = LinearLayoutManager(requireActivity())
        recyclerView.layoutManager = mLayoutManager
        recyclerView.itemAnimator = DefaultItemAnimator()

        listAdapter = AdminAllOrderAdapter(requireActivity(),txtTotal)
        recyclerView.adapter = listAdapter
        listAdapter.updateListItems(merchantOrderListItems)

        viewModelUser.showProgressBar.observe(requireActivity(), Observer {
            if (it) {
                progressBar.visibility = View.VISIBLE
            } else {
                progressBar.visibility = View.GONE
            }
        })

        viewModelUser.adminAllOrderList()

        viewModelUser.allOrderListItems.observe(requireActivity(), Observer {
            listAdapter.updateListItems(it)
        })

    }

}