package com.plasdor.app.view.fragment.deliveryAgent

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.google.android.material.textfield.TextInputEditText
import com.plasdor.app.R
import com.plasdor.app.adapter.AdminAllOrderAdapter
import com.plasdor.app.adapter.DeliveryAgentOrderAdapter
import com.plasdor.app.model.AdminAllOrderListItems
import com.plasdor.app.session.SharePreferenceManager
import com.plasdor.app.utils.Constants
import com.plasdor.app.viewModel.AdminViewModel

class DeliveryAgentOrderListFragment : Fragment() {
    lateinit var rootView: View
    lateinit var viewModelUser: AdminViewModel
    lateinit var listAdapter: DeliveryAgentOrderAdapter
    var merchantOrderListItems = ArrayList<AdminAllOrderListItems>()
    lateinit var mLayoutManager: LinearLayoutManager
    var userId = ""
    var userType = ""
    lateinit var txtTotal: TextView
    var searchText = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_delivery_agent_order_list, container, false)
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
        searchFilter()
    }

    private fun searchFilter() {
        val edSearch: TextInputEditText = rootView.findViewById(R.id.edSearch)!!
        edSearch.hint= "Search by Order No,status,date address ect"
        edSearch.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                searchText = s.toString()
                listAdapter.getFilter()!!.filter(searchText)

            }

            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }
        })
    }
    private fun setupRecyclerView() {
        viewModelUser = ViewModelProvider(this).get(AdminViewModel::class.java)
        val recyclerView: RecyclerView = rootView.findViewById(R.id.recyclerView)!!
        val progressBar: ProgressBar = rootView.findViewById(R.id.progressBar)!!

        mLayoutManager = LinearLayoutManager(requireActivity())
        recyclerView.layoutManager = mLayoutManager
        recyclerView.itemAnimator = DefaultItemAnimator()

        listAdapter = DeliveryAgentOrderAdapter(requireActivity())
        recyclerView.adapter = listAdapter
        listAdapter.updateListItems(merchantOrderListItems)

        viewModelUser.showProgressBar.observe(requireActivity(), Observer {
            if (it) {
                progressBar.visibility = View.VISIBLE
            } else {
                progressBar.visibility = View.GONE
            }
        })

        viewModelUser.getOrderListForDeliveryAgent()

        viewModelUser.allOrderListItems.observe(requireActivity(), Observer {
            listAdapter.updateListItems(it)
        })

    }

}