package com.plasdor.app.view.fragment.merchant

import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.plasdor.app.R
import com.plasdor.app.adapter.MerchantAllProductListAdapter
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


class MerchantAllProductListFragment : Fragment(), MerchantProductClickListener, ApiResponse,
    AlertDialogBtnClickedCallBack {

    lateinit var viewModelUser: MerchantListViewModel
    lateinit var listAdapter: MerchantAllProductListAdapter
    var listItems = ArrayList<MerchantProductListItems>()
    lateinit var mLayoutManager: GridLayoutManager

    var searchText = ""

    var userId = ""
    var userType = ""
    var itemPos = 0
    var productId = ""
    var totalQty = ""
    var totalControllerQty = ""

    lateinit var rootView: View
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        rootView = inflater.inflate(R.layout.fragment_all_product_list, container, false)
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
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
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


        viewModelUser.getAllProductListForMerchant(userId)

        viewModelUser.allProductListItems.observe(requireActivity(), Observer {
            listItems = it
            listAdapter.updateListItems(it)
        })

        listAdapter = MerchantAllProductListAdapter(
            requireActivity(),
            this
        )
        recyclerView.adapter = listAdapter
    }


    override fun addMyProduct(item: MerchantProductListItems, position: Int) {
        showAlertDialogButtonClicked()
        itemPos = position
        productId = item.pId

    }

    override fun removeMyProduct(item: MerchantProductListItems, position: Int) {
        itemPos = position
        val method = "deleteMerchantsProduct"
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

    }


    override fun onSuccess(data: Any, tag: String) {

        if (data.equals("SUCCESS")) {
            requireContext().showToastMsg("Success")
            listAdapter.updateListItems(listItems)
        } else {
            requireContext().showToastMsg("Failed")
        }

    }

    override fun onFailure(message: String) {
        requireContext().showToastMsg(message)
    }

    override fun positiveBtnClicked() {

        val method = "merchantSetProductAvailable"
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
        // add a button
        builder.setPositiveButton(
                "Add",
                DialogInterface.OnClickListener { dialog, which -> // send data from the
                    // AlertDialog to the Activity
                    val editText = customLayout.findViewById<EditText>(R.id.etQty)
                    val etControllerQty = customLayout.findViewById<EditText>(R.id.etControllerQty)
                    totalQty = editText.text.toString()
                    totalControllerQty = etControllerQty.text.toString()
                    if(totalQty.equals("")){
                            requireContext().showToastMsg("Enter Qty")
                    }else if(totalControllerQty.equals("")){
                            requireContext().showToastMsg("Enter Controller Qty")
                    }else{
                        addProductWithQty()
                    }

                })
        // create and show
        // the alert dialog
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun addProductWithQty() {
        val method = "addMerchantsProduct"
        val jsonObject = JSONObject()
        try {
            jsonObject.put("method", method)
            jsonObject.put("merchantId", userId)
            jsonObject.put("productId",productId)
            jsonObject.put("totalQty", totalQty)
            jsonObject.put("totalControllerQty", totalControllerQty)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        apiPostCall(Constants.BASE_URL, jsonObject, this, method)
        listItems[itemPos].isAdded = "1"
        listAdapter.notifyDataSetChanged()
    }

}