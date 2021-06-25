package com.plasdor.app.repository

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.plasdor.app.callbacks.ApiResponse
import com.plasdor.app.model.MerchantProductListItems
import com.plasdor.app.model.MyOrderListItems
import com.plasdor.app.utils.Constants
import com.plasdor.app.utils.apiGetCall
import com.plasdor.app.utils.showToastMsg

class MerchantListRepository(val application: Application) : ApiResponse {

    val showProgressBar = MutableLiveData<Boolean>()
    val listItems = MutableLiveData<ArrayList<MerchantProductListItems>>()
    val allProductListItems = MutableLiveData<ArrayList<MerchantProductListItems>>()
    val merchantOrderListItems = MutableLiveData<ArrayList<MyOrderListItems>>()
    var method = ""
    var userId = ""


    fun changeState() {
        showProgressBar.value = !(showProgressBar != null && showProgressBar.value!!)
    }

    fun getAllProductListForMerchant(merchantId: String) {
        showProgressBar.value = true

        method = "getAllProductListForMerchant"
        var url = Constants.BASE_URL + "?method=$method&merchantId=$merchantId"

        apiGetCall(url, this, method)

    }

    fun getMerchantWiseProductList(merchantId: String) {
        showProgressBar.value = true

        method = "getMerchantWiseProductList"
        var url = Constants.BASE_URL + "?method=$method&merchantId=$merchantId"

        apiGetCall(url, this, method)

    }


    fun getMerchantOrderList(userId: String) {
        showProgressBar.value = true

        method = "getMerchantWiseOrders"
        var url = Constants.BASE_URL + "?method=$method&merchantId=$userId"
        apiGetCall(url, this, method)

    }


    override fun onSuccess(data: Any, tag: String) {
        showProgressBar.value = false

        if (data.equals("List not available")) {
            application.showToastMsg(data.toString())
        } else {
            val gson = Gson()

            if (method == "getAllProductListForMerchant") {
                val type = object : TypeToken<ArrayList<MerchantProductListItems>>() {}.type
                var productListItems: ArrayList<MerchantProductListItems>? =
                    gson.fromJson(data.toString(), type)
                allProductListItems.value = productListItems
            }
            else if (method == "getMerchantWiseProductList") {
                val type = object : TypeToken<ArrayList<MerchantProductListItems>>() {}.type
                var productListItems: ArrayList<MerchantProductListItems>? =
                    gson.fromJson(data.toString(), type)
                listItems.value = productListItems
            }
            else if (method == "getMerchantWiseOrders") {
                val type = object : TypeToken<ArrayList<MyOrderListItems>>() {}.type
                var listItems: ArrayList<MyOrderListItems>? =
                    gson.fromJson(data.toString(), type)
                merchantOrderListItems.value = listItems
            }

        }


    }

    override fun onFailure(message: String) {
        showProgressBar.value = false
    }


}