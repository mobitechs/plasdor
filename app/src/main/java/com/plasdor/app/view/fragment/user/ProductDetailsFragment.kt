package com.plasdor.app.view.fragment.user

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
import com.plasdor.app.model.ProductListItems
import com.plasdor.app.model.UserModel
import com.plasdor.app.session.SharePreferenceManager
import com.plasdor.app.utils.*
import com.plasdor.app.view.activity.MapsActivity
import com.plasdor.app.view.activity.UserHomeActivity
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
    lateinit var txtDescription: AppCompatTextView
    lateinit var txtViewOnMap: AppCompatTextView
    lateinit var layoutDescription: LinearLayout
    lateinit var spinner: AppCompatSpinner
    var productId = ""
    var selectedMerchantId = ""
    var userId = ""
    var userLat = ""
    var userLong = ""
    var userName = ""

    var spinnerItemArray = Constants.pieceArray
    var qty = 1

    lateinit var selectedMerchantItem:UserModel

    lateinit var viewModelUser: UserListViewModel
    lateinit var listAdapter: AvailableMerchantListAdapter
    var merchantListItems = ArrayList<UserModel>()
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
        txtDescription = rootView.findViewById(R.id.txtDescription)
        btnBuyNow = rootView.findViewById(R.id.btnBuyNow)
        layoutDescription = rootView.findViewById(R.id.layoutDescription)
        txtViewOnMap = rootView.findViewById(R.id.txtViewOnMap)
        spinner = rootView.findViewById(R.id.spinner)

        listItem = arguments?.getParcelable("item")!!

        productId = listItem.pId
        ivProdImage.setImage(listItem.img!!)
        tvProductName.text = listItem.productName
        tvPrice.text = "Rs. " + listItem.priceToSell
        txtType.text = "Type " + listItem.type
        txtDescription.text = listItem.description

        if (listItem.description.equals("")) {
            layoutDescription.visibility = View.GONE
        }


        btnBuyNow.setOnClickListener {
            var bundle = Bundle()
            bundle.putParcelable("productItem", listItem)
            bundle.putParcelable("merchantItem", selectedMerchantItem)
            bundle.putInt("noOfDays", qty)
            (context as UserHomeActivity?)!!.OpenPlaceOrderFragment(bundle)
        }
        txtViewOnMap.setOnClickListener {
            requireContext().openActivity(MapsActivity::class.java){
                putParcelableArrayList("merchantListItems", merchantListItems)
                putString("userLat", userLat)
                putString("userLong", userLong)
                putString("userName", userName)
            }
        }


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

                var totalPrice = qty * (listItem.price).toInt()
                var discountedPrice = qty * (listItem.priceToSell).toInt()

                tvPrice.text = "Rs. " +discountedPrice.toString()


            }
        })

        setupRecyclerView()

    }


    private fun setupRecyclerView() {
        viewModelUser = ViewModelProvider(requireActivity()).get(UserListViewModel::class.java)
        val recyclerView: RecyclerView = rootView.findViewById(R.id.recyclerView)!!
//        val progressBar: ProgressBar = rootView.findViewById(R.id.progressBar)!!

        mLayoutManager = LinearLayoutManager(requireActivity())
        recyclerView.layoutManager = mLayoutManager
        recyclerView.itemAnimator = DefaultItemAnimator()

        viewModelUser.getAvailableMerchant(productId)

        viewModelUser.merchantListItems.observe(requireActivity(), Observer {
            merchantListItems = it
            listAdapter.updateListItems(it)
        })

        viewModelUser.isResponseHaveData.observe(requireActivity(), Observer {
            if (it == 2) {
                showUI()
            } else if (it == 1) {
                hideUI()
            }
        })

        listAdapter = AvailableMerchantListAdapter(
            requireActivity(),
            this
        )
        recyclerView.adapter = listAdapter
    }

    private fun hideUI() {
        txtViewOnMap.visibility = View.GONE
        btnBuyNow.visibility = View.GONE

        requireActivity().showToastMsg("we do not have any merchant for this product")
    }

    private fun showUI() {
        txtViewOnMap.visibility = View.VISIBLE

    }

    override fun selectMerchant(item: UserModel, position: Int) {

        selectedMerchantItem = item
        btnBuyNow.visibility = View.VISIBLE
        selectedMerchantId = item.userId
//        requireActivity().showToastMsg("id:" + selectedMerchantId)
    }

}