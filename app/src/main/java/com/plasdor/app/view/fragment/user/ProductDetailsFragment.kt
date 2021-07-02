package com.plasdor.app.view.fragment.user

import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatSpinner
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.plasdor.app.R
import com.plasdor.app.adapter.AvailableMerchantListAdapter
import com.plasdor.app.callbacks.MerchantSelectionClickListener
import com.plasdor.app.model.AvailableMerchantListItem
import com.plasdor.app.model.ProductListItems
import com.plasdor.app.session.SharePreferenceManager
import com.plasdor.app.utils.Constants
import com.plasdor.app.utils.ThreeTwoImageView
import com.plasdor.app.utils.openActivity
import com.plasdor.app.utils.setImage
import com.plasdor.app.view.activity.MapsActivity
import com.plasdor.app.view.activity.PlaceOrderActivity
import com.plasdor.app.viewModel.UserListViewModel


class ProductDetailsFragment : Fragment(), MerchantSelectionClickListener {


    lateinit var listItem: ProductListItems
    var addedToCart = false

    lateinit var rootView: View
    lateinit var btnBuyNow: AppCompatTextView
    lateinit var ivProdImage: ThreeTwoImageView
    lateinit var tvProductName: AppCompatTextView
    lateinit var txtType: AppCompatTextView
    lateinit var tvPrice: AppCompatTextView
    lateinit var tvLabelPrice: AppCompatTextView
    lateinit var txtControllerCharges: AppCompatTextView
    lateinit var txtTotalPayable: AppCompatTextView
    lateinit var txtDescription: AppCompatTextView
    lateinit var txtViewOnMap: AppCompatTextView
    lateinit var txtSelectMerchantLabel: AppCompatTextView
    lateinit var layoutDescription: LinearLayout
    lateinit var spinner: AppCompatSpinner
    lateinit var spinnerControllerQty: AppCompatSpinner
    var productId = ""
    var selectedMerchantId = ""
    var userId = ""
    var userLat = ""
    var userLong = ""
    var userName = ""
    var priceToPay = ""

    var spinnerItemArray = Constants.pieceArray
    var spinnerControllerQtyArray = Constants.controllerQtyArray
    var discountedPrice = ""
    var qty = 1
    var qtyPos = 0
    var controllerQty = 1
    var controllerQtyPos = 0
    var totalPriceWithController = 0
    var controllerCharges = 0

    lateinit var selectedMerchantItem: AvailableMerchantListItem

    lateinit var viewModelUser: UserListViewModel
    lateinit var listAdapter: AvailableMerchantListAdapter
    var merchantListItems = ArrayList<AvailableMerchantListItem>()
    lateinit var mLayoutManager: LinearLayoutManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.product_detail_layout, container, false)
        initView()
        return rootView
    }

    private fun initView() {

        userId = SharePreferenceManager.getInstance(requireContext()).getUserLogin(Constants.USERDATA)?.get(0)?.userId.toString()
        userLat = SharePreferenceManager.getInstance(requireContext()).getUserLogin(Constants.USERDATA)?.get(0)?.latitude.toString()
        userLong = SharePreferenceManager.getInstance(requireContext()).getUserLogin(Constants.USERDATA)?.get(0)?.longitude.toString()
        userName = SharePreferenceManager.getInstance(requireContext()).getUserLogin(Constants.USERDATA)?.get(0)?.name.toString()

        ivProdImage = rootView.findViewById(R.id.ivProdImage)
        tvProductName = rootView.findViewById(R.id.tvProductName)
        txtType = rootView.findViewById(R.id.txtType)
        tvPrice = rootView.findViewById(R.id.tvPrice)
        tvLabelPrice = rootView.findViewById(R.id.tvLabelPrice)
        txtDescription = rootView.findViewById(R.id.txtDescription)
        btnBuyNow = rootView.findViewById(R.id.btnBuyNow)
        layoutDescription = rootView.findViewById(R.id.layoutDescription)
        txtViewOnMap = rootView.findViewById(R.id.txtViewOnMap)
        txtSelectMerchantLabel = rootView.findViewById(R.id.txtSelectMerchantLabel)
        spinner = rootView.findViewById(R.id.spinner)
        spinnerControllerQty = rootView.findViewById(R.id.spinnerControllerQty)
        txtControllerCharges = rootView.findViewById(R.id.txtControllerCharges)
        txtTotalPayable = rootView.findViewById(R.id.txtTotalPayable)

        listItem = arguments?.getParcelable("item")!!

        productId = listItem.pId
        ivProdImage.setImage(listItem.img!!)
        tvProductName.text = listItem.productName
        tvPrice.text = "Rs. " + listItem.priceToSell
        setLabelPrice(listItem.price)
        txtType.text = "Type " + listItem.type
        txtDescription.text = listItem.description

        if (listItem.description.equals("")) {
            layoutDescription.visibility = View.GONE
        }

        hideUI()
        btnBuyNow.setOnClickListener {
            var bundle = Bundle()
            bundle.putParcelable("productItem", listItem)
            bundle.putParcelable("merchantItem", selectedMerchantItem)
            requireActivity().openActivity(PlaceOrderActivity::class.java) {
                putParcelable("productItem", listItem)
                putParcelable("merchantItem", selectedMerchantItem)
            }
//            (context as UserHomeActivity?)!!.OpenPlaceOrderFragment(bundle)
        }
        txtViewOnMap.setOnClickListener {
            requireContext().openActivity(MapsActivity::class.java) {
                putParcelableArrayList("merchantListItems", merchantListItems)
                putString("userLat", userLat)
                putString("userLong", userLong)
                putString("userName", userName)
            }
        }


        setupNoOfDaysSpinner()
        setupControllerQtySpinner()

        setupRecyclerView()

    }

    private fun setupControllerQtySpinner() {
        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.spinner_layout,
            spinnerControllerQtyArray
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerControllerQty.setAdapter(adapter)

        spinnerControllerQty.setSelection(listItem.controllerQty - 1)

        spinnerControllerQty.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {

            override fun onNothingSelected(p0: AdapterView<*>?) {
//            event.onNothingSelected()
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                controllerQty = spinnerItemArray[p2].toInt()
                controllerQtyPos = p2
                setupPrice()


            }
        })
    }

    private fun setupPrice() {
        priceToPay = listItem.price
        var priceToSell = listItem.priceToSell
        if (listItem.productName.equals("PS5") || listItem.productName.equals("XBOX Series X")) {
            priceToPay = Constants.ps5NdXSeriesXPriceArray[qtyPos]
        } else if (listItem.productName.equals("PS4") || listItem.productName.equals("XBOX One X")) {
            priceToPay = Constants.ps4NdXOneXPriceArray[qtyPos]
        } else if (listItem.productName.equals("XBOX One S")) {
            priceToPay = Constants.XOneSPriceArray[qtyPos]
        } else if (listItem.productName.equals("XBOX Series S")) {
            priceToPay = Constants.XSeriesSPriceArray[qtyPos]
        }

        discountedPrice = priceToPay

        if (controllerQty == 1) {
            controllerCharges = 0
        } else {
            controllerCharges = 50 * (controllerQty - 1)

        }
        totalPriceWithController = priceToPay.toInt() + controllerCharges
        txtTotalPayable.text = "Rs. " + totalPriceWithController.toString()
        txtControllerCharges.text = "Additional Rs. " + controllerCharges.toString()

        listItem._qtyWisePrice = priceToPay.toInt()
        listItem._qty = qty
        listItem.controllerQty = controllerQty.toInt()
        listItem.controllerCharges = controllerCharges.toString()
        listItem.totalPayable = totalPriceWithController
        listItem.discountedPrice = discountedPrice

        tvPrice.text = "Rs. " + priceToPay.toString()
        var totalPrice = qty * listItem.price.toInt()
        setLabelPrice(totalPrice.toString())

    }

    private fun setupNoOfDaysSpinner() {
        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.spinner_layout,
            spinnerItemArray
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.setAdapter(adapter)

        spinner.setSelection(listItem._qty - 1)

        spinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {

            override fun onNothingSelected(p0: AdapterView<*>?) {
//            event.onNothingSelected()
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                qty = spinnerItemArray[p2].toInt()
                qtyPos = p2
                setupPrice()
            }
        })
    }

    private fun setLabelPrice(totalPrice: String) {
        tvLabelPrice.text = "Rs. " + totalPrice
        tvLabelPrice.setPaintFlags(tvLabelPrice.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG)
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
            this
        )
        recyclerView.adapter = listAdapter

        viewModelUser.getAvailableMerchant(productId)

        viewModelUser.merchantListItems.observe(requireActivity(), Observer {
            merchantListItems = it

            if(merchantListItems.size > 0){
                showUI()
            }else{
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
        txtViewOnMap.visibility = View.VISIBLE
        txtSelectMerchantLabel.text = "Select Merchant"
    }

    override fun selectMerchant(item: AvailableMerchantListItem, position: Int) {

        selectedMerchantItem = item
        btnBuyNow.visibility = View.VISIBLE
        selectedMerchantId = item.userId
//        requireActivity().showToastMsg("id:" + selectedMerchantId)
    }

}