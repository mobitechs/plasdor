package com.plasdor.app.view.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.*
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.plasdor.app.R
import com.plasdor.app.adapter.BazarListAdapter
import com.plasdor.app.callbacks.AddOrRemoveListener
import com.plasdor.app.callbacks.ApiResponse
import com.plasdor.app.model.ProductListItems
import com.plasdor.app.session.SharePreferenceManager
import com.plasdor.app.utils.Constants
import com.plasdor.app.utils.apiPostCall
import com.plasdor.app.utils.showToastMsg
import com.plasdor.app.view.activity.UserHomeActivity
import com.plasdor.app.viewModel.UserListViewModel
import kotlinx.android.synthetic.main.spinner_layout.view.*
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList


class BazarListFragment : Fragment(), ApiResponse, AddOrRemoveListener {

    lateinit var rootView: View
    lateinit var viewModelUser: UserListViewModel
    lateinit var listAdapter: BazarListAdapter
    var listItems = ArrayList<ProductListItems>()
    lateinit var mLayoutManager: LinearLayoutManager
    var userId=""
    var rewardPoints=""
    var userWalletPoint=""

    private var mRewardedAd: RewardedAd? = null
    private final var TAG = "BazarListFragment"


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_bazar_list, container, false)
        intView()
        return rootView
    }

    private fun intView() {
        userId = SharePreferenceManager.getInstance(requireActivity()).getUserLogin(Constants.USERDATA)?.get(0)?.userId.toString()
        setupRecyclerView()

        MobileAds.initialize(requireContext())
        RequestConfiguration.Builder().setTestDeviceIds(Arrays.asList("E3EBB20286FEE729C04269FCFBA201EE"))
        MobileAds.initialize(requireContext(),
            OnInitializationCompleteListener { getRewardAd() })
//        getRewardAd()
    }

    private fun setupRecyclerView() {
        viewModelUser = ViewModelProvider(this).get(UserListViewModel::class.java)
        val recyclerView: RecyclerView = rootView.findViewById(R.id.recyclerView)!!
        val progressBar: ProgressBar = rootView.findViewById(R.id.progressBar)!!
        val txtWalletPoints: TextView = rootView.findViewById(R.id.txtWalletPoints)!!

        userWalletPoint = SharePreferenceManager.getInstance(requireContext()).getValueString(Constants.EARNED_POINTS).toString()

        if (userWalletPoint.equals("null") || userWalletPoint.equals("")) {
            userWalletPoint = "0"
        }

        txtWalletPoints.text = userWalletPoint

        mLayoutManager = LinearLayoutManager(requireActivity())
        recyclerView.layoutManager = mLayoutManager
        recyclerView.itemAnimator = DefaultItemAnimator()

        listAdapter = BazarListAdapter(requireActivity(),this)
        recyclerView.adapter = listAdapter
        listAdapter.updateListItems(listItems)

        viewModelUser.showProgressBar.observe(requireActivity(), Observer {
            if (it) {
                progressBar.visibility = View.VISIBLE
            } else {
                progressBar.visibility = View.GONE
            }
        })

        viewModelUser.getBazarList()

        viewModelUser.bazarListItems.observe(requireActivity(), Observer {
            listItems = it
            listAdapter.updateListItems(listItems)
        })
    }
    private fun getRewardAd() {
        var adRequest = AdRequest.Builder().build()
        RewardedAd.load(
            requireContext(),
            "ca-app-pub-6867366913789478/9973123288",
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d(TAG, adError?.message)
                    mRewardedAd = null
                }

                override fun onAdLoaded(rewardedAd: RewardedAd) {
                    Log.d(TAG, "Ad was loaded.")
                    mRewardedAd = rewardedAd

                    mRewardedAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                        override fun onAdShowedFullScreenContent() {
                            // Called when ad is shown.
                            Log.d(TAG, "Ad was shown.")
                            requireContext().showToastMsg("Ad was shown")
                        }

                        override fun onAdFailedToShowFullScreenContent(adError: AdError?) {
                            // Called when ad fails to show.
                            Log.d(TAG, "Ad failed to show.")
                            requireContext().showToastMsg("Ad failed to show.")
                        }

                        override fun onAdDismissedFullScreenContent() {
                            // Called when ad is dismissed.
                            // Set the ad reference to null so you don't show the ad a second time.
                            Log.d(TAG, "Ad was dismissed.")
                            requireContext().showToastMsg("Ad was dismissed.")
                            mRewardedAd = null
                        }
                    }

                    if (mRewardedAd != null) {
                        mRewardedAd?.show(requireActivity(), OnUserEarnedRewardListener() {
                            fun onUserEarnedReward(rewardItem: RewardItem) {
                                rewardPoints = rewardItem.amount.toString()
                                var rewardType = rewardItem.getType()
                                requireContext().showToastMsg("ads success: " + rewardPoints)
                                addRewardPoint()
                                Log.d(TAG, "User earned the reward.")
                            }
                        })
                    } else {
                        Log.d(TAG, "The rewarded ad wasn't ready yet.")
                        requireContext().showToastMsg("The rewarded ad wasn't ready yet.")
                    }
                }
            })

    }
    fun addRewardPoint() {
        val method = "addRewardPoints"
        val jsonObject = JSONObject()
        try {
            jsonObject.put("method", method)
            jsonObject.put("userId", userId)
            jsonObject.put("rewardPoints", rewardPoints)
            jsonObject.put("referralType", "Ads")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        apiPostCall(Constants.BASE_URL, jsonObject, this, method)
    }

    override fun onSuccess(data: Any, tag: String) {
        requireContext().showToastMsg("You have earned $rewardPoints points.")
    }

    override fun onFailure(message: String) {
        requireContext().showToastMsg(message)
    }

    override fun selectProduct(item: ProductListItems, position: Int) {
        var bundle = Bundle()
        bundle.putParcelable("item", item)
        (context as UserHomeActivity?)!!.OpenBazarOrderPlaceFragment(bundle)
    }
    override fun addToCart(item: ProductListItems, position: Int) {

    }

    override fun removeFromCart(item: ProductListItems, position: Int) {

    }

    override fun editProduct(item: ProductListItems, position: Int) {

    }

    override fun deleteProduct(item: ProductListItems, position: Int) {

    }




}