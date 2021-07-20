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

class AdminRepository(val application: Application) : ApiResponse {

    val showProgressBar = MutableLiveData<Boolean>()
    val allOrderListItems = MutableLiveData<ArrayList<AdminAllOrderListItems>>()
    val allProductListItems = MutableLiveData<ArrayList<ProductListItems>>()
    val allMerchantListItems = MutableLiveData<ArrayList<UserModel>>()
    val allUserListItems = MutableLiveData<ArrayList<UserModel>>()
    val allPendingUserList = MutableLiveData<ArrayList<UserModel>>()
    var method = ""
    var userId = ""


    fun changeState() {
        showProgressBar.value = !(showProgressBar != null && showProgressBar.value!!)
    }

    fun adminAllProductList() {
        showProgressBar.value = true
        method = "AdminAllProduct"
        var url = Constants.BASE_URL + "?method=$method"
        apiGetCall(url, this, method)
    }

    fun adminAllOrderList() {
        showProgressBar.value = true
        method = "AdminAllOrder"
        var url = Constants.BASE_URL + "?method=$method"
        apiGetCall(url, this, method)
    }

    fun adminAllMerchantList() {
        showProgressBar.value = true
        method = "AdminAllMerchant"
        var url = Constants.BASE_URL + "?method=$method"
        apiGetCall(url, this, method)
    }

    fun adminAllUserList() {
        showProgressBar.value = true
        method = "AdminAllUsers"
        var url = Constants.BASE_URL + "?method=$method"
        apiGetCall(url, this, method)
    }
    fun getAllPendingUserList() {
        showProgressBar.value = true
        method = "getAllPendingUserList"
        var url = Constants.BASE_URL + "?method=$method"
        apiGetCall(url, this, method)
    }


    override fun onSuccess(data: Any, tag: String) {
        showProgressBar.value = false

        if (data.equals("List not available")) {
            application.showToastMsg(data.toString())
        } else {
            val gson = Gson()

            if (method == "AdminAllProduct") {
                val type = object : TypeToken<ArrayList<ProductListItems>>() {}.type
                var productListItems: ArrayList<ProductListItems>? =
                    gson.fromJson(data.toString(), type)
                allProductListItems.value = productListItems
            } else if (method == "AdminAllOrder") {
                val type = object : TypeToken<ArrayList<AdminAllOrderListItems>>() {}.type
                var productListItems: ArrayList<AdminAllOrderListItems>? =
                    gson.fromJson(data.toString(), type)
                allOrderListItems.value = productListItems
            } else if (method == "AdminAllMerchant") {
                val type = object : TypeToken<ArrayList<UserModel>>() {}.type
                var listItems: ArrayList<UserModel>? =
                    gson.fromJson(data.toString(), type)
                allMerchantListItems.value = listItems
            } else if (method == "AdminAllUsers") {
                val type = object : TypeToken<ArrayList<UserModel>>() {}.type
                var listItems: ArrayList<UserModel>? =
                    gson.fromJson(data.toString(), type)
                allUserListItems.value = listItems
            }
            else if (method == "getAllPendingUserList") {
                val type = object : TypeToken<ArrayList<UserModel>>() {}.type
                var listItems: ArrayList<UserModel>? =
                    gson.fromJson(data.toString(), type)
                allPendingUserList.value = listItems
            }

        }


    }

    override fun onFailure(message: String) {
        showProgressBar.value = false
    }


}