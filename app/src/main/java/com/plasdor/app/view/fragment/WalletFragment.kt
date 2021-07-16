package com.plasdor.app.view.fragment

import android.os.Bundle
import android.util.Log
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
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.plasdor.app.R
import com.plasdor.app.adapter.UserOrderListAdapter
import com.plasdor.app.callbacks.ApiResponse
import com.plasdor.app.model.MyOrderListItems
import com.plasdor.app.model.UserModel
import com.plasdor.app.model.WalletPoint
import com.plasdor.app.model.WalletResponse
import com.plasdor.app.session.SharePreferenceManager
import com.plasdor.app.utils.Constants
import com.plasdor.app.utils.apiPostCall
import com.plasdor.app.utils.checkLogin
import com.plasdor.app.utils.showToastMsg
import com.plasdor.app.viewModel.UserListViewModel
import org.json.JSONException
import org.json.JSONObject
import org.w3c.dom.Text

class WalletFragment : Fragment(), ApiResponse {

    lateinit var  rootView:View
    lateinit var  txtRewardPoints:TextView

    lateinit var viewModelUser: UserListViewModel
    lateinit var listAdapter: UserOrderListAdapter
    var redeemHistoryListItems = ArrayList<MyOrderListItems>()
    lateinit var mLayoutManager: LinearLayoutManager
    lateinit var recyclerView: RecyclerView

    var userId=""
    var rewardPoints=""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootView =  inflater.inflate(R.layout.fragment_wallet, container, false)
        initView()
        return rootView
    }

    private fun initView() {
        userId = SharePreferenceManager.getInstance(requireActivity()).getUserLogin(Constants.USERDATA)?.get(0)?.userId.toString()
        rewardPoints = SharePreferenceManager.getInstance(requireActivity()).getValueString(Constants.EARNED_POINTS).toString()
        txtRewardPoints = rootView.findViewById(R.id.txtRewardPoints)
        recyclerView = rootView.findViewById(R.id.recyclerView)!!
        txtRewardPoints.text = rewardPoints
        getWalletPoints()
        setupRecyclerView()
    }

    private fun getWalletPoints() {
        val method = "getWalletPoints"
        val jsonObject = JSONObject()
        try {
            jsonObject.put("method", method)
            jsonObject.put("userId", userId)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        apiPostCall(Constants.BASE_URL, jsonObject, this, method)
    }

    private fun setupRecyclerView() {
        viewModelUser = ViewModelProvider(this).get(UserListViewModel::class.java)


        mLayoutManager = LinearLayoutManager(requireActivity())
        recyclerView.layoutManager = mLayoutManager
        recyclerView.itemAnimator = DefaultItemAnimator()

        listAdapter = UserOrderListAdapter(requireActivity())
        recyclerView.adapter = listAdapter
        listAdapter.updateListItems(redeemHistoryListItems)

        viewModelUser.getMyRedeemHistory(userId)

        viewModelUser.redeemHistoryListItems.observe(requireActivity(), Observer {
            listAdapter.updateListItems(it)
        })

    }

    override fun onSuccess(data: Any, tag: String) {
        if (data.equals("FAILED")) {
            requireContext().showToastMsg("Please try again letter")
        } else {

            val gson = Gson()
            val type = object : TypeToken<ArrayList<WalletPoint>>() {}.type
            var walletPoint: ArrayList<WalletPoint>? = gson.fromJson(data.toString(), type)


            rewardPoints = walletPoint?.get(0)?.wallet.toString()

            SharePreferenceManager.getInstance(requireContext()).save(Constants.EARNED_POINTS, rewardPoints)
            txtRewardPoints.text = rewardPoints

        }
    }

    override fun onFailure(message: String) {
        requireContext().showToastMsg(message)
    }


}