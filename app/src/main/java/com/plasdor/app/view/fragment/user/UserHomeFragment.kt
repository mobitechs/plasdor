package com.plasdor.app.view.fragment.user

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.plasdor.app.R
import com.plasdor.app.adapter.ProductListAdapter
import com.plasdor.app.callbacks.AddOrRemoveListener
import com.plasdor.app.callbacks.ApiResponse
import com.plasdor.app.model.ProductListItems
import com.plasdor.app.session.SharePreferenceManager
import com.plasdor.app.utils.*
import com.plasdor.app.view.activity.UserHomeActivity
import com.plasdor.app.viewModel.UserListViewModel
import org.json.JSONException
import org.json.JSONObject

class UserHomeFragment : Fragment(), AddOrRemoveListener, ApiResponse {

    lateinit var rootView: View
    lateinit var viewModelUser: UserListViewModel
    lateinit var listAdapter: ProductListAdapter
    var listItems = ArrayList<ProductListItems>()
    var cartListItems = ArrayList<ProductListItems>()
    var allProductListItems = ArrayList<ProductListItems>()
    lateinit var mLayoutManager: GridLayoutManager


    var searchText = ""
    var userId = ""
    var userType = ""
    var position = 0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_home_user, container, false)
        intView()
        return rootView
    }

    private fun intView() {
        userId =
            SharePreferenceManager.getInstance(requireContext()).getUserLogin(Constants.USERDATA)
                ?.get(0)?.userId.toString()

        userType =
            SharePreferenceManager.getInstance(requireContext()).getUserLogin(Constants.USERDATA)
                ?.get(0)?.userType.toString()

        if (SharePreferenceManager.getInstance(requireContext())
                .getCartListItems(Constants.CartList) != null
        ) {
            cartListItems = SharePreferenceManager.getInstance(requireContext())
                .getCartListItems(Constants.CartList) as ArrayList<ProductListItems>
        }

        allProductListItems = SharePreferenceManager.getInstance(requireContext())
            .getCartListItems(Constants.AllProductList) as ArrayList<ProductListItems>
        listItems = SharePreferenceManager.getInstance(requireContext())
            .getCartListItems(Constants.AllProductList) as ArrayList<ProductListItems>
        setupRecyclerView()

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
    }

    private fun setupRecyclerView() {
        viewModelUser = ViewModelProvider(this).get(UserListViewModel::class.java)
        val recyclerView: RecyclerView = rootView.findViewById(R.id.recyclerView)!!
        val progressBar: ProgressBar = rootView.findViewById(R.id.progressBar)!!

        mLayoutManager = GridLayoutManager(requireActivity(), 2)
        recyclerView.layoutManager = mLayoutManager
        recyclerView.itemAnimator = DefaultItemAnimator()

        listAdapter = ProductListAdapter(
            requireActivity(),
            this,
            cartListItems,
            allProductListItems,
            userType
        )
        recyclerView.adapter = listAdapter


//        viewModelUser.showProgressBar.observe(requireActivity(), Observer {
//            if (it) {
//                progressBar.visibility = View.VISIBLE
//            } else {
//                progressBar.visibility = View.GONE
//            }
//        })
//
//        viewModelUser.getAllProduct()
//
//
//        viewModelUser.listItems.observe(requireActivity(), Observer {
//            allProductListItems = it
//            SharePreferenceManager.getInstance(requireContext())
//                .saveCartListItems(Constants.AllProductList, allProductListItems)
//            listAdapter.updateListItems(it)
//        })

        listAdapter.updateListItems(listItems)
    }


    override fun addToCart(item: ProductListItems, position: Int) {
//        requireContext().addToCart(item)
    }
    override fun selectProduct(item: ProductListItems, position: Int) {
//        requireActivity().showToastMsg("id:"+item.pId)
        var bundle = Bundle()
        bundle.putParcelable("item", item)
        (context as UserHomeActivity?)!!.OpenProductDetailsFragment(bundle)
    }

    override fun removeFromCart(item: ProductListItems, position: Int) {
//        requireContext().removeToCart(item)
    }

    override fun editProduct(item: ProductListItems, position: Int) {
        var bundle = Bundle()
        bundle.putParcelable("item", item)
        (context as UserHomeActivity?)!!.OpenEditProductFragment(bundle)
    }

    override fun deleteProduct(item: ProductListItems, pos: Int) {
        position = pos
        //call an api to delete product
        val method = "DeleteProduct"
        val jsonObject = JSONObject()
        try {
            jsonObject.put("method", method)
            jsonObject.put("productId", item.pId)
            jsonObject.put("userId", userId)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        apiPostCall(Constants.BASE_URL, jsonObject, this, method)
    }

    override fun onSuccess(data: Any, tag: String) {
        if (data.equals("SUCCESS")) {
            requireContext().showToastMsg("Product deleted successfully.")

            listItems!!.removeAt(position)
            listAdapter.notifyItemRemoved(position)
            listAdapter.notifyItemRangeChanged(position, listItems!!.size)
            listAdapter.notifyDataSetChanged()

        } else {
            requireContext().showToastMsg("Failed to product delete.")
        }
    }

    override fun onFailure(message: String) {
        requireContext().showToastMsg(message)
    }

    override fun onResume() {
        super.onResume()
//        requireActivity().showToastMsg("fruit resumed called")
        intView()
    }

    override fun onPause() {
        super.onPause()
    }


}