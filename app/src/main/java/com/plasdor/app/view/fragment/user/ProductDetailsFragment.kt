package com.plasdor.app.view.fragment.user

import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
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
import com.plasdor.app.view.activity.MapsMerchantListActivity
import com.plasdor.app.view.activity.PlaceOrderActivity
import com.plasdor.app.viewModel.UserListViewModel


class ProductDetailsFragment : Fragment(), MerchantSelectionClickListener {



    var addedToCart = false

    lateinit var rootView: View
    lateinit var btnBuyNow: AppCompatTextView
    lateinit var ivProdImage: ThreeTwoImageView
    lateinit var tvProductName: AppCompatTextView
    lateinit var txtType: AppCompatTextView
    lateinit var txtPriceToSell: AppCompatTextView
    lateinit var txtPriceToShow: AppCompatTextView
    lateinit var txtControllerCharges: AppCompatTextView
    lateinit var txtTotalPayableWithDelivery: AppCompatTextView
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

    var spinnerItemArray = Constants.daysArray
    var spinnerControllerQtyArray = Constants.controllerQtyArray
    var discountedPrice = ""
    var qty = 1
    var qtyPos = 0
    var controllerQty = 1
    var controllerQtyPos = 0
    var totalPriceWithController = 0
    var controllerCharges = 0

    lateinit var selectedMerchantItem: AvailableMerchantListItem
    lateinit var listItem: ProductListItems

    lateinit var viewModelUser: UserListViewModel
    lateinit var listAdapter: AvailableMerchantListAdapter
    var merchantListItems = ArrayList<AvailableMerchantListItem>()
    lateinit var mLayoutManager: LinearLayoutManager

    lateinit var radio_group: RadioGroup
    lateinit var rdHourly: RadioButton
    lateinit var rdDaily: RadioButton

    lateinit var radio_group_delivery: RadioGroup
    lateinit var redSelfPickup: RadioButton
    lateinit var rdDeliverByCompany: RadioButton

    lateinit var labelNoOf: AppCompatTextView
    lateinit var txtDeliveryNote: AppCompatTextView
    var rentalType = Constants.Daily
    var deliveryCharges = 0
    var deliveryType = Constants.deliveryByCompany
    var priceToShow = 0
    var priceToSell = 0
    var totalPayable = 0

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

        userId =
            SharePreferenceManager.getInstance(requireContext()).getUserLogin(Constants.USERDATA)
                ?.get(0)?.userId.toString()
        userLat =
            SharePreferenceManager.getInstance(requireContext()).getUserLogin(Constants.USERDATA)
                ?.get(0)?.latitude.toString()
        userLong =
            SharePreferenceManager.getInstance(requireContext()).getUserLogin(Constants.USERDATA)
                ?.get(0)?.longitude.toString()
        userName =
            SharePreferenceManager.getInstance(requireContext()).getUserLogin(Constants.USERDATA)
                ?.get(0)?.name.toString()

        ivProdImage = rootView.findViewById(R.id.ivProdImage)
        tvProductName = rootView.findViewById(R.id.tvProductName)
        txtType = rootView.findViewById(R.id.txtType)
        txtPriceToSell = rootView.findViewById(R.id.txtPriceToSell)
        txtPriceToShow = rootView.findViewById(R.id.txtPriceToShow)
        txtDescription = rootView.findViewById(R.id.txtDescription)
        btnBuyNow = rootView.findViewById(R.id.btnBuyNow)
        layoutDescription = rootView.findViewById(R.id.layoutDescription)
        txtViewOnMap = rootView.findViewById(R.id.txtViewOnMap)
        txtSelectMerchantLabel = rootView.findViewById(R.id.txtSelectMerchantLabel)
        spinner = rootView.findViewById(R.id.spinner)
        spinnerControllerQty = rootView.findViewById(R.id.spinnerControllerQty)
        txtControllerCharges = rootView.findViewById(R.id.txtControllerCharges)
        txtTotalPayableWithDelivery = rootView.findViewById(R.id.txtTotalPayableWithDelivery)

        radio_group = rootView.findViewById(R.id.radio_group)!!
        rdHourly = rootView.findViewById(R.id.rdHourly)!!
        rdDaily = rootView.findViewById(R.id.rdDaily)!!
        labelNoOf = rootView.findViewById(R.id.labelNoOf)!!
        radio_group_delivery = rootView.findViewById(R.id.radio_group_delivery)!!
        redSelfPickup = rootView.findViewById(R.id.redSelfPickup)!!
        txtDeliveryNote = rootView.findViewById(R.id.txtDeliveryNote)!!

        radio_group.setOnCheckedChangeListener(
            RadioGroup.OnCheckedChangeListener { group, checkedId ->
                val radio: RadioButton = rootView.findViewById(checkedId)
                if (radio.text.toString().equals(Constants.Hourly)) {
                    rentalType = Constants.Hourly
                    labelNoOf.text = "No Of Hours:"
                    spinnerItemArray = Constants.hourArray
//                    if(qty == 1){
//                        deliveryCharges = Constants.delCharges1Hour
//                        txtDeliveryNote.text = requireActivity().getString(R.string.deliveryNote80)
//                    }else{
//                        deliveryCharges = Constants.delChargesNormal
//                        txtDeliveryNote.text = requireActivity().getString(R.string.deliveryNote40)
//                    }

                } else if (radio.text.toString().equals(Constants.Daily)) {
                    rentalType = Constants.Daily
                    labelNoOf.text = "No Of Days:"
                    spinnerItemArray = Constants.daysArray
//                    deliveryCharges = Constants.delChargesNormal
//                    txtDeliveryNote.text = requireActivity().getString(R.string.deliveryNote40)
                }
                spinnerControllerQty.setSelection(0)
                setupNoOfDaysHoursSpinner()
//                spinner.setSelection(0)

               // setupPrice()
            })
//        radio_group_delivery.setOnCheckedChangeListener(
//            RadioGroup.OnCheckedChangeListener { group, checkedId ->
//                val radio: RadioButton = rootView.findViewById(checkedId)
//                if (radio.text.toString().equals(Constants.selfPickup)) {
//                    deliveryType = Constants.selfPickup
//                    deliveryCharges = 0
//                    txtDeliveryNote.visibility = View.GONE
//                }
//                else if (radio.text.toString().equals(Constants.deliveryByCompany)) {
//                    deliveryType = Constants.deliveryByCompany
//                    deliveryCharges = Constants.delChargesNormal
//                    txtDeliveryNote.visibility = View.VISIBLE
//                }
//                setupPrice()
//            })

        listItem = arguments?.getParcelable("item")!!

        productId = listItem.pId
        ivProdImage.setImage(listItem.img!!)
        tvProductName.text = listItem.productName

        priceToShow = listItem.priceToShowDaily.toInt()
        priceToSell = listItem.priceToSellDaily.toInt()


        setLabelPrice()
//        txtType.text = "Type " + listItem.type
        txtDescription.text = listItem.description

        if (listItem.description.equals("")) {
            layoutDescription.visibility = View.GONE
        }

        hideUI()
        btnBuyNow.setOnClickListener {
//            var bundle = Bundle()
//            bundle.putParcelable("productItem", listItem)
//            bundle.putParcelable("merchantItem", selectedMerchantItem)
//            bundle.putString("rentalType", rentalType)
//            bundle.putString("deliveryType", deliveryType)
//            bundle.putString("deliveryType", deliveryType)

            requireActivity().openActivity(PlaceOrderActivity::class.java) {
                putParcelable("productItem", listItem)
                putParcelable("merchantItem", selectedMerchantItem)
                putString("rentalType", rentalType)
                putString("deliveryType", deliveryType)
                putInt("deliveryCharges", deliveryCharges)
            }
        }
        txtViewOnMap.setOnClickListener {
            requireContext().openActivity(MapsMerchantListActivity::class.java) {
                putParcelableArrayList("merchantListItems", merchantListItems)
                putString("userLat", userLat)
                putString("userLong", userLong)
                putString("userName", userName)
            }
        }


        setupNoOfDaysHoursSpinner()
        setupControllerQtySpinner()

        setupRecyclerView()

    }

    private fun setupNoOfDaysHoursSpinner() {
        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.spinner_layout,
            spinnerItemArray
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.setAdapter(adapter)
        spinner.setSelection(0)
        spinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
//            event.onNothingSelected()
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                qty = spinnerItemArray[p2].toInt()
//                if (rentalType.equals(Constants.Hourly) && qty == 1) {
//                    deliveryCharges = Constants.delCharges1Hour
//                    txtDeliveryNote.text = requireActivity().getString(R.string.deliveryNote80)
//                } else {
//                    deliveryCharges = Constants.delChargesNormal
//                    txtDeliveryNote.text = requireActivity().getString(R.string.deliveryNote40)
//                }
                qtyPos = p2
                setupPrice()
            }
        })
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
                controllerQty = spinnerControllerQtyArray[p2].toInt()
                controllerQtyPos = p2
                setupPrice()
            }
        })
    }

    private fun setupPrice() {
//        priceToPay = listItem.priceToShowDaily
//        var priceToSell = listItem.priceToSellDaily
        if ((listItem.productName.equals("PS5") || listItem.productName.equals("XBOX Series X")) && rentalType.equals(Constants.Daily)) {
            priceToSell = Constants.ps5NdXSeriesXPriceArray[qtyPos].toInt()
           } else if ((listItem.productName.equals("PS4") || listItem.productName.equals("XBOX One X")) && rentalType.equals(Constants.Daily)) {
            priceToSell = Constants.ps4NdXOneXPriceArray[qtyPos].toInt()
        } else if (listItem.productName.equals("XBOX One S") && rentalType.equals(Constants.Daily)) {
            priceToSell = Constants.XOneSPriceArray[qtyPos].toInt()
        } else if (listItem.productName.equals("XBOX Series S") && rentalType.equals(Constants.Daily)) {
            priceToSell = Constants.XSeriesSPriceArray[qtyPos].toInt()
        } else if ((listItem.productName.equals("PS5") || listItem.productName.equals("XBOX Series X") || listItem.productName.equals("XBOX Series S")) && rentalType.equals(Constants.Hourly)) {
            priceToSell = Constants.ps5NdXSeriesXNdSPriceArrayHr[qtyPos].toInt()
        } else if ((listItem.productName.equals("PS4") || listItem.productName.equals("XBOX One X") || listItem.productName.equals("XBOX One S")) && rentalType.equals(Constants.Hourly)) {
            priceToSell = Constants.ps4NdXOneXNdSPriceArrayHr[qtyPos].toInt()
        }
//        else if (listItem.productName.equals("XBOX One S") && rentalType.equals(Constants.Hourly)) {
//            priceToSell = Constants.XOneSPriceArrayHr[qtyPos].toInt()
//        }
//        else if (listItem.productName.equals("XBOX Series S") && rentalType.equals(Constants.Hourly)) {
//            priceToSell = Constants.XSeriesSPriceArrayHr[qtyPos].toInt()
//        }


        if (rentalType.equals(Constants.Hourly)) {
            priceToShow = (qty * listItem.priceToShowHourly.toInt())
        } else {
            priceToShow = (qty * listItem.priceToShowDaily.toInt())
        }


        if (controllerQty == 1) {
            controllerCharges = 50
        } else {
            controllerCharges = 50 * (controllerQty)
        }

        setLabelPrice()

    }


    private fun setLabelPrice() {
        txtPriceToSell.text = "Rs. " + priceToSell
        txtPriceToShow.text = "Rs. " + priceToShow
        txtPriceToShow.setPaintFlags(txtPriceToShow.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG)

        txtControllerCharges.text = "Controller Rs. " + controllerCharges.toString()
        totalPriceWithController = priceToSell.toInt() + controllerCharges
        var totalChargesWithDelivery = deliveryCharges + totalPriceWithController
//        txtTotalPayableWithDelivery.text = "Rs. " + totalPriceWithController.toString()
        txtTotalPayableWithDelivery.text = "Rs. " + totalChargesWithDelivery.toString()


        listItem.qtyWisePrice = priceToSell.toInt()
        listItem.qty = qty
        listItem.controllerQty = controllerQty.toInt()
        listItem.controllerCharges = controllerCharges.toString()
        listItem.totalPayable = totalPriceWithController
        listItem.discountedPrice = priceToSell.toString()

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
            this,userLat,userLong
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