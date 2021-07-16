package com.plasdor.app.repository

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.plasdor.app.callbacks.ApiResponse
import com.plasdor.app.model.*
import com.plasdor.app.utils.Constants
import com.plasdor.app.utils.apiGetCall
import com.plasdor.app.utils.showToastMsg


class UserListRepository(val application: Application) : ApiResponse {

    val showProgressBar = MutableLiveData<Boolean>()
    val allProductListItems = MutableLiveData<ArrayList<ProductListItems>>()
    val myOrderListItems = MutableLiveData<ArrayList<MyOrderListItems>>()
    val redeemHistoryListItems = MutableLiveData<ArrayList<MyOrderListItems>>()
    val merchantListItems = MutableLiveData<ArrayList<AvailableMerchantListItem>>()
    val addressListItems = MutableLiveData<ArrayList<AddressListItems>>()
    val bazarListItems = MutableLiveData<ArrayList<ProductListItems>>()

    var method = ""
    var userId = ""

    fun changeState() {
        showProgressBar.value = !(showProgressBar != null && showProgressBar.value!!)
    }

    fun getAllProduct() {
        method = "getAllProductList"
        var url = Constants.BASE_URL + "?method=$method"
        apiGetCall(url, this, method)
    }

    fun getAvailableMerchant(productId: String) {

        method = "productWiseAvailableMerchant"
        var url = Constants.BASE_URL + "?method=$method&productId=$productId"
        apiGetCall(url, this, method)
    }

    fun getMyOrderList(userId: String) {
        showProgressBar.value = true
        method = "getUserWiseOrders"
        var url = Constants.BASE_URL + "?method=$method&userId=$userId"
        apiGetCall(url, this, method)
    }
    fun getMyRedeemHistory(userId: String) {
        method = "getRedeemHistory"
        var url = Constants.BASE_URL + "?method=$method&userId=$userId"
        apiGetCall(url, this, method)
    }

    fun getAddressList() {
        showProgressBar.value = true
        method = "GetAllAddress"
        var url = Constants.BASE_URL + "?method=$method&userId=$userId"
        apiGetCall(url, this, method)
    }
    fun getBazarList() {
        showProgressBar.value = true
        method = "getBazarProductList"
        var url = Constants.BASE_URL + "?method=$method"
        apiGetCall(url, this, method)
    }

    override fun onSuccess(data: Any, tag: String) {
        showProgressBar.value = false

        if (data.equals("List not available")) {
            application.showToastMsg(data.toString())
        } else {
            val gson = Gson()

            if (method == "getAllProductList") {
                val type = object : TypeToken<ArrayList<ProductListItems>>() {}.type
                var productListItems: ArrayList<ProductListItems>? =
                    gson.fromJson(data.toString(), type)
                allProductListItems.value = productListItems
            }
            else if (method == "productWiseAvailableMerchant") {
                merchantListItems.value?.clear()
                val type = object : TypeToken<ArrayList<AvailableMerchantListItem>>() {}.type
                var listItems: ArrayList<AvailableMerchantListItem>? =
                    gson.fromJson(data.toString(), type)
                merchantListItems.value = listItems
            }
            else if (method == "getUserWiseOrders") {
                showProgressBar.value = false
                val type = object : TypeToken<ArrayList<MyOrderListItems>>() {}.type
                var listItems: ArrayList<MyOrderListItems>? =
                    gson.fromJson(data.toString(), type)
                myOrderListItems.value = listItems
            }else if (method == "getRedeemHistory") {
                val type = object : TypeToken<ArrayList<MyOrderListItems>>() {}.type
                var listItems: ArrayList<MyOrderListItems>? =
                    gson.fromJson(data.toString(), type)
                redeemHistoryListItems.value = listItems
            }
            else if (method == "getBazarProductList") {
                showProgressBar.value = false
                val type = object : TypeToken<ArrayList<ProductListItems>>() {}.type
                var listItems: ArrayList<ProductListItems>? =
                    gson.fromJson(data.toString(), type)
                bazarListItems.value = listItems
            }
        }
    }

    override fun onFailure(message: String) {
        showProgressBar.value = false
    }


}