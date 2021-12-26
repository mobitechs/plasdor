package com.plasdor.app.view.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.google.android.material.textfield.TextInputEditText
import com.plasdor.app.R
import com.plasdor.app.adapter.AdminAllUserAdapter
import com.plasdor.app.adapter.MyReferralListAdapter
import com.plasdor.app.model.ReferralList
import com.plasdor.app.model.UserModel
import com.plasdor.app.session.SharePreferenceManager
import com.plasdor.app.utils.Constants
import com.plasdor.app.viewModel.AdminViewModel
import com.plasdor.app.viewModel.UserListViewModel

class MyRefferalListFragment : Fragment() {

    lateinit var viewModelUser: UserListViewModel
    lateinit var listAdapter: MyReferralListAdapter
    var listItems = ArrayList<ReferralList>()
    lateinit var mLayoutManager: LinearLayoutManager

    var userId = ""
    var userType = ""

    lateinit var rootView: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_my_refferal_list, container, false)
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

    }

    private fun setupRecyclerView() {
        viewModelUser = ViewModelProvider(this).get(UserListViewModel::class.java)
        val recyclerView: RecyclerView = rootView.findViewById(R.id.recyclerView)!!
        val progressBar: ProgressBar = rootView.findViewById(R.id.progressBar)!!

        mLayoutManager = LinearLayoutManager(requireActivity())
        recyclerView.layoutManager = mLayoutManager
        recyclerView.itemAnimator = DefaultItemAnimator()


        viewModelUser.showProgressBar.observe(requireActivity(), Observer {
            if (it) {
                progressBar.visibility = View.VISIBLE
            } else {
                progressBar.visibility = View.GONE
            }
        })

        viewModelUser.getMyReferralList(userId)

        viewModelUser.referralList.observe(requireActivity(), Observer {
            listItems = it
            listAdapter.updateListItems(it)
        })

        listAdapter = MyReferralListAdapter(requireActivity())
        recyclerView.adapter = listAdapter
    }

}