package com.plasdor.app.view.fragment.merchant

import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatSpinner
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.plasdor.app.R
import com.plasdor.app.adapter.MerchantProductListAdapter
import com.plasdor.app.callbacks.AlertDialogBtnClickedCallBack
import com.plasdor.app.callbacks.ApiResponse
import com.plasdor.app.callbacks.MerchantProductClickListener
import com.plasdor.app.model.MerchantProductListItems
import com.plasdor.app.session.SharePreferenceManager
import com.plasdor.app.utils.Constants
import com.plasdor.app.utils.apiPostCall
import com.plasdor.app.utils.showAlertDialog
import com.plasdor.app.utils.showToastMsg
import com.plasdor.app.viewModel.MerchantListViewModel
import org.json.JSONException
import org.json.JSONObject


class MerchantFragmentProductList : Fragment(), MerchantProductClickListener, ApiResponse,
    AlertDialogBtnClickedCallBack {

    lateinit var viewModelUser: MerchantListViewModel
    lateinit var listAdapter: MerchantProductListAdapter
    var listItems = ArrayList<MerchantProductListItems>()
    lateinit var mLayoutManager: GridLayoutManager

    var searchText = ""
    var userId = ""
    var userType = ""
    var itemPos = 0
    var method =""
    var productId = ""
    var totalQty = ""
    var totalControllerQty = ""
    var willDeliver = "No"

    var spinnerControllerQtyArray = Constants.controllerQtyArray
    lateinit var spinnerControllerQty: AppCompatSpinner

        lateinit var rootView: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.merchant_fragment_product_list, container, false)
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


        val edSearch: TextInputEditText = rootView.findViewById(R.id.edSearch)!!
        edSearch.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                searchText = s.toString()
                listAdapter.getFilter()!!.filter(searchText)

            }

            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // TODO Auto-generated method stub
            }
        })
        setupRecyclerView()

    }

    private fun setupRecyclerView() {
        viewModelUser = ViewModelProvider(this).get(MerchantListViewModel::class.java)
        val recyclerView: RecyclerView = rootView.findViewById(R.id.recyclerView)!!
        val progressBar: ProgressBar = rootView.findViewById(R.id.progressBar)!!

        mLayoutManager = GridLayoutManager(requireActivity(), 2)
        recyclerView.layoutManager = mLayoutManager
        recyclerView.itemAnimator = DefaultItemAnimator()

        viewModelUser.showProgressBar.observe(requireActivity(), Observer {
            if (it) {
                progressBar.visibility = View.VISIBLE
            } else {
                progressBar.visibility = View.GONE
            }
        })


        viewModelUser.getMerchantWiseProductList(userId)

        viewModelUser.listItems.observe(requireActivity(), Observer {
            listItems = it
            listAdapter.updateListItems(it)
        })


        listAdapter = MerchantProductListAdapter(
            requireActivity(),
            this
        )
        recyclerView.adapter = listAdapter
    }

    override fun addMyProduct(item: MerchantProductListItems, position: Int) {
    }

    override fun removeMyProduct(item: MerchantProductListItems, position: Int) {
        itemPos = position
        method = "deleteMerchantsProduct"
        val jsonObject = JSONObject()
        try {
            jsonObject.put("method", method)
            jsonObject.put("merchantId", userId)
            jsonObject.put("productId", item.pId)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        apiPostCall(Constants.BASE_URL, jsonObject, this, method)
        listItems[itemPos].isAdded = "0"
        listItems[itemPos].soldQty = "0"
    }

    override fun selectProduct(item: MerchantProductListItems, position: Int) {
    }

    override fun addToAvailable(item: MerchantProductListItems, position: Int) {
        itemPos = position
        requireContext().showAlertDialog(
            "Confirmation",
            "Do you want to make this product available?",
            "Yes",
            "NO",
            this
        )

    }

    override fun editProduct(item: MerchantProductListItems, position: Int) {
        itemPos = position
        productId = item.pId
        totalQty = item.totalQty
        totalControllerQty = item.totalControllerQty
        showAlertDialogButtonClicked()
    }

    override fun onSuccess(data: Any, tag: String) {

        if (data.equals("SUCCESS")) {
            requireContext().showToastMsg("Success")

            if(method == "deleteMerchantsProduct"){
                listItems.removeAt(itemPos);
                listAdapter.notifyItemRemoved(itemPos);
                listAdapter.notifyItemRangeChanged(itemPos, listItems.size);
            }
            listAdapter.updateListItems(listItems)
        } else {
            requireContext().showToastMsg("Failed")
        }

    }

    override fun onFailure(message: String) {
        requireContext().showToastMsg(message)
    }

    override fun positiveBtnClicked() {

        method = "merchantSetProductAvailable"
        val jsonObject = JSONObject()
        try {
            jsonObject.put("method", method)
            jsonObject.put("merchantId", userId)
            jsonObject.put("productId", listItems[itemPos].pId)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        apiPostCall(Constants.BASE_URL, jsonObject, this, method)
        listItems[itemPos].isSold = "0"
        listItems[itemPos].soldQty = "0"
    }

    override fun negativeBtnClicked() {

    }

    fun showAlertDialogButtonClicked() {

        // Create an alert builder
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Total number of console (s) you are renting & controller Qty")

        // set the custom layout
        val customLayout: View = layoutInflater.inflate(R.layout.add_qty_dialog_layout, null)
        builder.setView(customLayout)
        val editText:EditText = customLayout.findViewById(R.id.etQty)
        val etControllerQty:EditText = customLayout.findViewById(R.id.etControllerQty)
        val txtDeliverNote: AppCompatTextView = customLayout.findViewById(R.id.txtDeliverNote)
        val radio_group: RadioGroup = customLayout.findViewById(R.id.radio_group)
        spinnerControllerQty = customLayout.findViewById(R.id.spinnerControllerQty)
//        setupControllerQtySpinner()
        val rdYes: RadioButton = customLayout.findViewById(R.id.rdYes)
        val rdNo: RadioButton = customLayout.findViewById(R.id.rdNo)
//        radio_group.setOnCheckedChangeListener(
//            RadioGroup.OnCheckedChangeListener { group, checkedId ->
//                val radio: RadioButton = customLayout.findViewById(checkedId)
//                willDeliver = radio.text.toString()
//                if(willDeliver.equals("Yes")){
//                    txtDeliverNote.visibility=View.GONE
//                }else
//                {
//                    txtDeliverNote.visibility=View.VISIBLE
//                }
//            })
//        if(willDeliver.equals("Yes")){
//            radio_group.check(R.id.rdYes)
//            txtDeliverNote.visibility=View.GONE
//        }else{
//            radio_group.check(R.id.rdNo)
//            txtDeliverNote.visibility=View.VISIBLE
//        }
        editText.setText(totalQty)
        etControllerQty.setText(totalControllerQty)

//        builder.setCancelable(false)
        // add a button
        builder.setPositiveButton(
            "Update",
            DialogInterface.OnClickListener { dialog, which -> // send data from the
                // AlertDialog to the Activity

                totalQty = editText.text.toString()
                totalControllerQty = etControllerQty.text.toString() //cz we are use=ing spinner for this
                if(totalQty.equals("")){
                    requireContext().showToastMsg("Enter Qty")
                }else if(totalControllerQty.equals("")){
                    requireContext().showToastMsg("Select Controller Qty")
                }else{
                    updateProductWithQty()
                }
            })
        // create and show
        // the alert dialog
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun updateProductWithQty() {
        willDeliver = "No"  //default no bcz company will deliver all product
        val method = "updateMerchantsProduct"
        val jsonObject = JSONObject()
        try {
            jsonObject.put("method", method)
            jsonObject.put("merchantId", userId)
            jsonObject.put("productId",productId)
            jsonObject.put("totalQty", totalQty)
            jsonObject.put("totalControllerQty", totalControllerQty)
            jsonObject.put("willDeliver", willDeliver)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        apiPostCall(Constants.BASE_URL, jsonObject, this, method)
        listItems[itemPos].isAdded = "1"
        listItems[itemPos].totalQty = totalQty
        listItems[itemPos].totalControllerQty = totalControllerQty
    }

    private fun setupControllerQtySpinner() {
        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.spinner_layout,
            spinnerControllerQtyArray
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerControllerQty.setAdapter(adapter)
        spinnerControllerQty.setSelection(spinnerControllerQtyArray.indexOf(totalControllerQty))
        spinnerControllerQty.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                totalControllerQty = spinnerControllerQtyArray[p2]
            }
        })
    }
}
