package com.plasdor.app.view.fragment.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.plasdor.app.R
import com.plasdor.app.adapter.AvailableMerchantListAdapter
import com.plasdor.app.callbacks.ApiResponse
import com.plasdor.app.callbacks.MerchantSelectionClickListener
import com.plasdor.app.model.AvailableMerchantListItem
import com.plasdor.app.model.ProductListItems
import com.plasdor.app.session.SharePreferenceManager
import com.plasdor.app.utils.*
import com.plasdor.app.view.activity.MapsMerchantListActivity
import com.plasdor.app.view.activity.UserHomeActivity
import com.plasdor.app.viewModel.UserListViewModel
import org.json.JSONException
import org.json.JSONObject

class BazarOrderPlaceFragment : Fragment(), MerchantSelectionClickListener, ApiResponse {


    lateinit var listItem: ProductListItems
    var addedToCart = false

    lateinit var rootView: View
    lateinit var btnBuyNow: AppCompatTextView
    lateinit var ivProdImage: ThreeTwoImageView
    lateinit var tvProductName: AppCompatTextView
    lateinit var txtSelectMerchantLabel: AppCompatTextView
    lateinit var txtViewOnMap: AppCompatTextView
    lateinit var txtInsufficientPointNote: AppCompatTextView
    lateinit var txtDeliveryCharges: AppCompatTextView
    lateinit var txtTotalPayable: AppCompatTextView
    lateinit var txtPointRequired: AppCompatTextView
    lateinit var layoutLoader: RelativeLayout

    var productId = ""
    var selectedMerchantId = ""
    var userId = ""
    var userEmail = ""
    var userLat = ""
    var userLong = ""
    var userName = ""
    var isVerified = ""
    var userWalletPoint = ""

    var qty = 1
    var controllerQty = 1
    var controllerCharges = 0
    var totalPriceWithController = 0

    lateinit var selectedMerchantItem: AvailableMerchantListItem

    lateinit var viewModelUser: UserListViewModel
    lateinit var listAdapter: AvailableMerchantListAdapter
    var merchantListItems = ArrayList<AvailableMerchantListItem>()
    lateinit var mLayoutManager: LinearLayoutManager


    lateinit var radio_group: RadioGroup

    var priceToSell = 0
    var deliveryCharges = 0
    var deliveryType = Constants.deliveryByCompany
    var rentalType = Constants.Daily
    var deliveredBy = ""
    var merchantWillGet = 0f
    var adminWillGet = 0f

    var firstFreeOrder = ""
    var requiredPoints = ""
    var userCanBuy = false

    lateinit var radio_group_delivery: RadioGroup
    lateinit var redSelfPickup: RadioButton
    lateinit var rdDeliverByCompany: RadioButton

    lateinit var labelNoOf: AppCompatTextView
    lateinit var txtDeliveryNote: AppCompatTextView
    lateinit var layoutPaymentDetails: LinearLayout
    lateinit var rdFirstFreeOrder: RadioButton
    lateinit var rd1Day: RadioButton
    lateinit var rd3day: RadioButton
    lateinit var rd5Day: RadioButton
    lateinit var rdFree: RadioButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_bazar_order_place, container, false)
        initView()
        return rootView
    }

    private fun initView() {
        firstFreeOrder =SharePreferenceManager.getInstance(requireContext()).getValueString(Constants.FIRST_FREE_ORDER_COMPLETE).toString()

        userId =
            SharePreferenceManager.getInstance(requireContext()).getUserLogin(Constants.USERDATA)
                ?.get(0)?.userId.toString()
        userEmail =
            SharePreferenceManager.getInstance(requireContext()).getUserLogin(Constants.USERDATA)
                ?.get(0)?.email.toString()
        userLat =
            SharePreferenceManager.getInstance(requireContext()).getUserLogin(Constants.USERDATA)
                ?.get(0)?.latitude.toString()
        userLong =
            SharePreferenceManager.getInstance(requireContext()).getUserLogin(Constants.USERDATA)
                ?.get(0)?.longitude.toString()
        userName =
            SharePreferenceManager.getInstance(requireContext()).getUserLogin(Constants.USERDATA)
                ?.get(0)?.name.toString()
        isVerified =
            SharePreferenceManager.getInstance(requireContext()).getUserLogin(Constants.USERDATA)
                ?.get(0)?.isVerified.toString()
        userWalletPoint = SharePreferenceManager.getInstance(requireContext()).getValueString(Constants.EARNED_POINTS).toString()

        if (userWalletPoint.equals("null") || userWalletPoint.equals("")) {
            userWalletPoint = "0"
        }

        ivProdImage = rootView.findViewById(R.id.ivProdImage)
        tvProductName = rootView.findViewById(R.id.tvProductName)
        btnBuyNow = rootView.findViewById(R.id.btnBuyNow)
        txtViewOnMap = rootView.findViewById(R.id.txtViewOnMap)
        txtSelectMerchantLabel = rootView.findViewById(R.id.txtSelectMerchantLabel)
        txtInsufficientPointNote = rootView.findViewById(R.id.txtInsufficientPointNote)
        layoutLoader = rootView.findViewById(R.id.layoutLoader)

        radio_group = rootView.findViewById(R.id.radio_group)!!

        radio_group_delivery = rootView.findViewById(R.id.radio_group_delivery)!!
        redSelfPickup = rootView.findViewById(R.id.redSelfPickup)!!
        txtDeliveryNote = rootView.findViewById(R.id.txtDeliveryNote)!!
        txtDeliveryCharges = rootView.findViewById(R.id.txtDeliveryCharges)!!
        txtTotalPayable = rootView.findViewById(R.id.txtTotalPayable)!!
        txtPointRequired = rootView.findViewById(R.id.txtPointRequired)!!
        layoutPaymentDetails = rootView.findViewById(R.id.layoutPaymentDetails)!!
        rdFirstFreeOrder = rootView.findViewById(R.id.rdFirstFreeOrder)!!
        rd1Day = rootView.findViewById(R.id.rd1Day)!!
        rd3day = rootView.findViewById(R.id.rd3day)!!
        rd5Day = rootView.findViewById(R.id.rd5Day)!!
        rdFree = rootView.findViewById(R.id.rdFree)!!

        listItem = arguments?.getParcelable("item")!!

        if(firstFreeOrder.equals("0")){
            requiredPoints = listItem.firstOrderPoint.toString()
//            layoutPaymentDetails.visibility = View.GONE
            rentalType = Constants.Hourly
            rd1Day.visibility = View.GONE
            rd3day.visibility = View.GONE
            rd5Day.visibility = View.GONE
            rdFree.visibility = View.GONE
            rdFirstFreeOrder.isChecked = true
            rdFirstFreeOrder.visibility = View.VISIBLE
        }else{
            requiredPoints = listItem.oneDayPoints.toString()
//            layoutPaymentDetails.visibility = View.VISIBLE
            rentalType = Constants.Daily
            rd1Day.visibility = View.VISIBLE
            rd3day.visibility = View.VISIBLE
            rd5Day.visibility = View.VISIBLE
            rdFree.visibility = View.VISIBLE
            rd1Day.isChecked = true
            rdFirstFreeOrder.visibility = View.GONE
        }

        if(isVerified.equals("1")){
            btnBuyNow.visibility = View.VISIBLE
        }else{
            btnBuyNow.visibility = View.GONE
        }

        radio_group_delivery.setOnCheckedChangeListener(
            RadioGroup.OnCheckedChangeListener { group, checkedId ->
                val radio: RadioButton = rootView.findViewById(checkedId)
                if (radio.text.toString().equals(Constants.selfPickup)) {
                    deliveryType = Constants.selfPickup
                    deliveryCharges = 0
                    txtDeliveryNote.visibility = View.GONE
                } else if (radio.text.toString().equals(Constants.deliveryByCompany)) {
                    deliveryType = Constants.deliveryByCompany
                    deliveryCharges = Constants.delChargesNormal
                    txtDeliveryNote.visibility = View.VISIBLE
                }

                txtDeliveryCharges.text = deliveryCharges.toString()
                txtTotalPayable.text = requiredPoints+"Points & "+deliveryCharges+" Rs."

            })



        txtDeliveryCharges.text = deliveryCharges.toString()
//        txtTotalPayable.text = requiredPoints+" Points & "+deliveryCharges+" Rs."
        txtTotalPayable.text = requiredPoints+" Points"

        checkCanBuyOrNot()

        setupPrice()

        radio_group.setOnCheckedChangeListener(
            RadioGroup.OnCheckedChangeListener { group, checkedId ->
                val radio: RadioButton = rootView.findViewById(checkedId)
                if (checkedId == R.id.rd1Day) {
                    qty = 1
                    requireContext().showToastMsg("1day")
                    requiredPoints = listItem.oneDayPoints.toString()
                } else if (checkedId == R.id.rd3day) {
                    qty = 3
                    requireContext().showToastMsg("3day")
                    requiredPoints = listItem.threeDayPoints.toString()
                } else if (checkedId == R.id.rd5Day) {
                    qty = 5
                    requireContext().showToastMsg("5day")
                    requiredPoints = listItem.fiveDayPoints.toString()
                } else if (checkedId == R.id.rdFree) {
                    requireContext().showToastMsg("free")
                    requiredPoints = listItem.forFreePoints.toString()
                }
                else if (checkedId == R.id.rdFirstFreeOrder) {
                    requireContext().showToastMsg("free for 2 hours")
                    requiredPoints = listItem.firstOrderPoint.toString()
                }

//                txtTotalPayable.text = requiredPoints+"Points & "+deliveryCharges+" Rs."
                txtTotalPayable.text = requiredPoints+"Points"
                txtPointRequired.text = requiredPoints
                checkCanBuyOrNot()
                setupPrice()
            })

        productId = listItem.pId
        ivProdImage.setImage(listItem.img!!)
        tvProductName.text = listItem.productName


        hideUI()
        btnBuyNow.setOnClickListener {
            callAPIToSaveOrder()
        }
        txtViewOnMap.setOnClickListener {
            requireContext().openActivity(MapsMerchantListActivity::class.java) {
                putParcelableArrayList("merchantListItems", merchantListItems)
                putString("userLat", userLat)
                putString("userLong", userLong)
                putString("userName", userName)
            }
        }
        setupRecyclerView()
    }

    private fun checkCanBuyOrNot() {
        if (userWalletPoint.toInt() >= requiredPoints.toInt()) {
            if(!selectedMerchantId.equals("")){
                btnBuyNow.visibility = View.VISIBLE
            }
            userCanBuy = true
            txtInsufficientPointNote.visibility = View.GONE
        }
        else {
            userCanBuy = false
            btnBuyNow.visibility = View.GONE
            txtInsufficientPointNote.visibility = View.VISIBLE
        }

//        if(firstFreeOrder.equals("0")){
//            userCanBuy = true
//            if(!selectedMerchantId.equals("")){
//                btnBuyNow.visibility = View.VISIBLE
//            }
//        }


    }


    private fun setupPrice() {

        if(firstFreeOrder.equals("0")){
            qty = 1
            if ((listItem.productName.equals("PS5") || listItem.productName.equals("XBOX Series X") || listItem.productName.equals("XBOX Series S"))) {
                priceToSell = Constants.ps5NdXSeriesXNdSPriceArrayHr[qty-1].toInt()
            } else if ((listItem.productName.equals("PS4") || listItem.productName.equals("XBOX One X") || listItem.productName.equals("XBOX One S"))) {
                priceToSell = Constants.ps4NdXOneXNdSPriceArrayHr[qty-1].toInt()
            }
        }else{
            if ((listItem.productName.equals("PS5") || listItem.productName.equals("XBOX Series X"))) {
                priceToSell = Constants.ps5NdXSeriesXPriceArray[qty - 1].toInt()
            } else if ((listItem.productName.equals("PS4") || listItem.productName.equals("XBOX One X"))) {
                priceToSell = Constants.ps4NdXOneXPriceArray[qty - 1].toInt()
            } else if (listItem.productName.equals("XBOX One S")) {
                priceToSell = Constants.XOneSPriceArray[qty - 1].toInt()
            } else if (listItem.productName.equals("XBOX Series S")) {
                priceToSell = Constants.XSeriesSPriceArray[qty - 1].toInt()
            }
        }


        totalPriceWithController = priceToSell.toInt() + controllerCharges

        merchantWillGet = (priceToSell * (Constants.percentBetweenMerchantNPlasdor / 100.0f))
        adminWillGet = (priceToSell - merchantWillGet)

    }

    private fun setupRecyclerView() {
        viewModelUser = ViewModelProvider(requireActivity()).get(UserListViewModel::class.java)
        val recyclerView: RecyclerView = rootView.findViewById(R.id.recyclerView)!!
//        val progressBar: ProgressBar = rootView.findViewById(R.id.progressBar)!!

        mLayoutManager = LinearLayoutManager(requireActivity())
        recyclerView.layoutManager = mLayoutManager
        recyclerView.itemAnimator = DefaultItemAnimator()

        listAdapter = AvailableMerchantListAdapter(
            requireActivity(),
            this,
            userLat,
            userLong
        )
        recyclerView.adapter = listAdapter

        viewModelUser.getAvailableMerchant(productId)

        viewModelUser.merchantListItems.observe(requireActivity(), Observer {
            merchantListItems = it

            if (merchantListItems.size > 0) {
                showUI()
            } else {
                hideUI()
            }
            listAdapter.updateListItems(it)
        })
    }

    private fun hideUI() {
        txtViewOnMap.visibility = View.GONE
        btnBuyNow.visibility = View.GONE
        txtSelectMerchantLabel.text = "we do not have any merchant for this product"
        // requireActivity().showToastMsg("We do not have any merchant for this product")
    }

    private fun showUI() {
//        txtViewOnMap.visibility = View.VISIBLE
        txtSelectMerchantLabel.text = "Select Merchant"
    }

    override fun selectMerchant(item: AvailableMerchantListItem, position: Int) {
        if (userCanBuy == true) {
            selectedMerchantItem = item
            btnBuyNow.visibility = View.VISIBLE
            selectedMerchantId = item.userId
        }

    }


    private fun callAPIToSaveOrder() {
        layoutLoader.visibility = View.VISIBLE
        var transactionNo = "TID" + System.currentTimeMillis()



        if (selectedMerchantItem.willDeliver.equals("Yes")) {
            deliveredBy = Constants.MERCHANT
        } else {
            deliveredBy = Constants.COMPANY
            merchantWillGet = merchantWillGet - Constants.deliveryCharges
            adminWillGet = adminWillGet + Constants.deliveryCharges
        }

        val method = "placeOrder"
        val jsonObject = JSONObject()
        try {
            jsonObject.put("method", method)
            jsonObject.put("userId", userId)
            jsonObject.put("userEmail", userEmail)
            jsonObject.put("pId", productId)
            jsonObject.put("merchantId", selectedMerchantId)
            jsonObject.put("rentalType", rentalType)
            jsonObject.put("noOfDaysHours", qty)
            jsonObject.put("productPriceDaily", listItem.priceToSellDaily)
            jsonObject.put("productPriceHourly", listItem.priceToSellHourly)
            jsonObject.put("noOfController", controllerQty)
            jsonObject.put("controllerCharges", controllerCharges)
            jsonObject.put("discountedPrice", priceToSell)
            jsonObject.put("deliveryCharges", deliveryCharges)
            jsonObject.put("totalPrice", totalPriceWithController)
            jsonObject.put("transactionNo", transactionNo)
            jsonObject.put("paymentType", "Redeem")
            jsonObject.put("paymentStatus", "Success")
            jsonObject.put("redeemPointsUsed", requiredPoints)
            jsonObject.put("adminWillGet", adminWillGet)
            jsonObject.put("merchantWillGet", merchantWillGet)
            jsonObject.put("deliveredBy", deliveredBy)
            jsonObject.put("isFirstFreeOrder", firstFreeOrder)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        apiPostCall(Constants.BASE_URL, jsonObject, this, method)
    }

    override fun onSuccess(data: Any, tag: String) {
        layoutLoader.visibility = View.GONE
        if (data.equals("SUCCESS")) {
            requireContext().showToastMsg("Order Successfully Placed")
            requireContext().openClearActivity(UserHomeActivity::class.java)

            userWalletPoint =  (userWalletPoint.toInt() - requiredPoints.toInt()).toString()
            SharePreferenceManager.getInstance(requireContext()).save(Constants.EARNED_POINTS, userWalletPoint)
            firstFreeOrder = "1"
            SharePreferenceManager.getInstance(requireContext()).save(Constants.FIRST_FREE_ORDER_COMPLETE, firstFreeOrder)

        } else {
            requireContext().showToastMsg("Order failed try again latter")
        }
    }

    override fun onFailure(message: String) {
        layoutLoader.visibility = View.GONE
        requireContext().showToastMsg("Error: $message")
    }


}