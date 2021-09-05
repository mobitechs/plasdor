package com.plasdor.app.view.fragment

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
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
import com.plasdor.app.utils.*
import com.plasdor.app.view.activity.UserHomeActivity
import com.plasdor.app.viewModel.UserListViewModel
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


class BazarListFragment : Fragment(), ApiResponse, AddOrRemoveListener {

    lateinit var rootView: View
    lateinit var viewModelUser: UserListViewModel
    lateinit var listAdapter: BazarListAdapter
    var listItems = ArrayList<ProductListItems>()
    lateinit var mLayoutManager: LinearLayoutManager
    lateinit var btnGetRewards: Button
    lateinit var txtWalletPoints: TextView
    lateinit var nextRewardTime: TextView
    var userId = ""
    var rewardPoints = ""
    var userWalletPoint = ""
    var getRewardCounter = 0
    var todaysDate = ""
    var rewardDate = ""
    var lastRewardTime = ""
    var canIncreament = false
    var isAddAvailable = false
    var firstFreeOrder = ""

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
        todaysDate = getTodaysDate()
        getRewardCounter = SharePreferenceManager.getInstance(requireActivity())
            .getValueInt(Constants.GET_REWARD_COUNTER)
        rewardDate = SharePreferenceManager.getInstance(requireActivity())
            .getValueString(Constants.GET_REWARD_DATE).toString()
        lastRewardTime = SharePreferenceManager.getInstance(requireActivity())
            .getValueString(Constants.LAST_REWARD_TIME).toString()

       // rewardDate = "31-08-2021"

        userId = SharePreferenceManager.getInstance(requireActivity()).getUserLogin(Constants.USERDATA)?.get(0)?.userId.toString()
        firstFreeOrder = SharePreferenceManager.getInstance(requireContext()).getValueString(Constants.FIRST_FREE_ORDER_COMPLETE).toString()
        userWalletPoint = SharePreferenceManager.getInstance(requireContext()).getValueString(Constants.EARNED_POINTS).toString()

        txtWalletPoints = rootView.findViewById(R.id.txtWalletPoints)!!
        nextRewardTime = rootView.findViewById(R.id.nextRewardTime)!!
        btnGetRewards = rootView.findViewById(R.id.btnGetRewards)!!

        if (!todaysDate.equals(rewardDate)) {
            getRewardCounter = 0
            rewardDate = getTodaysDate()
            SharePreferenceManager.getInstance(requireContext())
                .save(Constants.GET_REWARD_COUNTER, getRewardCounter)
            SharePreferenceManager.getInstance(requireContext())
                .save(Constants.GET_REWARD_DATE, rewardDate)

            canIncreament = true
        }

        checkRewardCountAndBtnVisiblity()

        if (userWalletPoint.equals("null") || userWalletPoint.equals("")) {
            userWalletPoint = "0"
        }

        txtWalletPoints.text = userWalletPoint

        setupRecyclerView()

        MobileAds.initialize(requireContext())
//        RequestConfiguration.Builder().setTestDeviceIds(Arrays.asList("E3EBB20286FEE729C04269FCFBA201EE"))
        MobileAds.initialize(requireContext(),
            OnInitializationCompleteListener {
            })

        btnGetRewards.setOnClickListener {
            playAd()
        }
    }



    private fun playAd() {

        if (isAddAvailable) {
            val cb = object : OnUserEarnedRewardListener {
                override fun onUserEarnedReward(rewardItem: RewardItem) {
                    rewardPoints = rewardItem.amount.toString()
                    var rewardType = rewardItem.getType()
//                requireContext().showToastMsg("ads success: " + rewardPoints)
                    addRewardPoint()
                    Log.d(TAG, "User earned the reward.")
                }

            }
            mRewardedAd?.show(requireActivity(), cb)

            mRewardedAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdShowedFullScreenContent() {
                    // Called when ad is shown.
                    Log.d(TAG, "Ad was shown.")
//                requireContext().showToastMsg("Ad was shown")
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError?) {
                    // Called when ad fails to show.
                    Log.d(TAG, "Ad failed to show.")
//                requireContext().showToastMsg("Ad failed to show.")
                }

                override fun onAdDismissedFullScreenContent() {
                    // Called when ad is dismissed.
                    // Set the ad reference to null so you don't show the ad a second time.
                    Log.d(TAG, "Ad was dismissed.")
//                requireContext().showToastMsg("Ad was dismissed.")
                    mRewardedAd = null
                }
            }
        } else {
            rewardPoints = "2"
            addRewardPoint()
        }
    }


    private fun getRewardAd() {
        var adRequest = AdRequest.Builder().build()
        RewardedAd.load(
            requireContext(),
            "ca-app-pub-5799348942632742/6098022101",
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d(TAG, adError?.message)
//                    requireContext().showToastMsg("FailedToLoad:"+adError?.message)
                    mRewardedAd = null
                    //this is added here because for now google added limitations on ads
                    isAddAvailable = false
//                    btnGetRewards.visibility = View.GONE
                }

                override fun onAdLoaded(rewardedAd: RewardedAd) {
                    Log.d(TAG, "Ad was loaded.")
//                    requireContext().showToastMsg("onAdLoaded:")
                    isAddAvailable = true
                    mRewardedAd = rewardedAd
                    btnGetRewards.visibility = View.VISIBLE

                }
            })

    }

    private fun setupRecyclerView() {
        viewModelUser = ViewModelProvider(this).get(UserListViewModel::class.java)
        val recyclerView: RecyclerView = rootView.findViewById(R.id.recyclerView)!!
        val progressBar: ProgressBar = rootView.findViewById(R.id.progressBar)!!


        mLayoutManager = LinearLayoutManager(requireActivity())
        recyclerView.layoutManager = mLayoutManager
        recyclerView.itemAnimator = DefaultItemAnimator()

        listAdapter = BazarListAdapter(requireActivity(), this,firstFreeOrder)
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

        userWalletPoint = (userWalletPoint.toInt() + rewardPoints.toInt()).toString()
        txtWalletPoints.text = userWalletPoint
        SharePreferenceManager.getInstance(requireContext())
            .save(Constants.EARNED_POINTS, userWalletPoint)

        getRewardCounter = SharePreferenceManager.getInstance(requireActivity())
            .getValueInt(Constants.GET_REWARD_COUNTER)

        getRewardCounter = getRewardCounter + 1
        SharePreferenceManager.getInstance(requireContext())
            .save(Constants.GET_REWARD_COUNTER, getRewardCounter)

        SharePreferenceManager.getInstance(requireContext())
            .save(Constants.GET_REWARD_DATE, todaysDate)

        lastRewardTime = getTodaysDateTime()
        SharePreferenceManager.getInstance(requireContext())
            .save(Constants.LAST_REWARD_TIME, lastRewardTime)

       checkRewardCountAndBtnVisiblity()
    }

    private fun checkRewardCountAndBtnVisiblity() {
        if(getRewardCounter > Constants.REWARD_LIMIT){
            btnGetRewards.visibility = View.GONE
            nextRewardTime.visibility = View.VISIBLE
            nextRewardTime.setText("Today's limit reached")
        }
        else{

            if(getRewardCounter == 0){
                //btnGetRewards.visibility = View.VISIBLE
                nextRewardTime.visibility = View.GONE
            }else{
                btnGetRewards.visibility = View.GONE
                nextRewardTime.visibility = View.VISIBLE
                nextRewardTime.setText("Next Rewards in 5 min")
                calculateTimeDifferance()
            }

            getRewardAd()
        }
    }

    private fun calculateTimeDifferance() {
        lastRewardTime =   SharePreferenceManager.getInstance(requireActivity())
            .getValueString(Constants.LAST_REWARD_TIME).toString()
        var currentDateTime = getTodaysDateTime()


        val sdf = SimpleDateFormat("dd-MM-yyyy hh:mm:ss")
        val d1: Date = sdf.parse(lastRewardTime)
        val d2: Date = sdf.parse(currentDateTime)

        val difference_In_Time = d2.time - d1.time
        val difference_In_Seconds = (difference_In_Time / 1000)
        val tenMinInSeconds = (Constants.NEXT_REWARD_TIME * 60)
        if(difference_In_Seconds > tenMinInSeconds){
            btnGetRewards.visibility = View.VISIBLE
            nextRewardTime.visibility = View.GONE
        }else{
            val timeToRunTimer = tenMinInSeconds - difference_In_Seconds
            startTimetimerCounter(timeToRunTimer)
        }



    }

    private fun startTimetimerCounter(differenceInSeconds: Long) {
        var timerCounter = differenceInSeconds
        var timeInMiliSeconds = differenceInSeconds * 1000

        object : CountDownTimer(timeInMiliSeconds, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                var remainTime = String.format(
                    "%d min, %d sec",
                    TimeUnit.MILLISECONDS.toMinutes(timerCounter*1000),
                    TimeUnit.MILLISECONDS.toSeconds(timerCounter*1000) -
                            TimeUnit.MINUTES.toSeconds(
                                TimeUnit.MILLISECONDS.toMinutes(
                                    timerCounter*1000
                                )
                            )
                );

//                nextRewardTime.text = "Next reward in :" + timerCounter.toString()
                nextRewardTime.text = "Next reward in :" + remainTime
                timerCounter--
            }

            override fun onFinish() {
                nextRewardTime.visibility = View.GONE
                btnGetRewards.visibility = View.VISIBLE
            }
        }.start()
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