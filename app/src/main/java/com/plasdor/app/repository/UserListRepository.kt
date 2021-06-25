package com.plasdor.app.repository

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.plasdor.app.callbacks.ApiResponse
import com.plasdor.app.model.AddressListItems
import com.plasdor.app.model.MyOrderListItems
import com.plasdor.app.model.ProductListItems
import com.plasdor.app.model.UserModel
import com.plasdor.app.utils.Constants
import com.plasdor.app.utils.apiGetCall
import com.plasdor.app.utils.showToastMsg


class UserListRepository(val application: Application) : ApiResponse {

    val showProgressBar = MutableLiveData<Boolean>()
    val isResponseHaveData = MutableLiveData<Int>()
    val allProductListItems = MutableLiveData<ArrayList<ProductListItems>>()
    val myOrderListItems = MutableLiveData<ArrayList<MyOrderListItems>>()
    val merchantListItems = MutableLiveData<ArrayList<UserModel>>()
    val addressListItems = MutableLiveData<ArrayList<AddressListItems>>()

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

    fun getAddressList() {
        showProgressBar.value = true
        method = "GetAllAddress"
        var url = Constants.BASE_URL + "?method=$method&userId=$userId&clientBusinessId=${Constants.clientBusinessId}"
        apiGetCall(url, this, method)
    }

    override fun onSuccess(data: Any, tag: String) {
        showProgressBar.value = false

        if (data.equals("List not available")) {
            application.showToastMsg(data.toString())
            isResponseHaveData.value = 1
        } else {
            val gson = Gson()

            if (method == "getAllProductList") {
                val type = object : TypeToken<ArrayList<ProductListItems>>() {}.type
                var productListItems: ArrayList<ProductListItems>? =
                    gson.fromJson(data.toString(), type)
                allProductListItems.value = productListItems
            }
            else if (method == "productWiseAvailableMerchant") {
                isResponseHaveData.value = 2
                val type = object : TypeToken<ArrayList<UserModel>>() {}.type
                var listItems: ArrayList<UserModel>? =
                    gson.fromJson(data.toString(), type)
                merchantListItems.value = listItems
            }
            else if (method == "getUserWiseOrders") {
                showProgressBar.value = false
                val type = object : TypeToken<ArrayList<MyOrderListItems>>() {}.type
                var listItems: ArrayList<MyOrderListItems>? =
                    gson.fromJson(data.toString(), type)
                myOrderListItems.value = listItems
            }
        }
    }

    override fun onFailure(message: String) {
        showProgressBar.value = false
    }


}