package com.plasdor.app.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.plasdor.app.model.MerchantProductListItems
import com.plasdor.app.model.MyOrderListItems
import com.plasdor.app.repository.MerchantListRepository

class MerchantListViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = MerchantListRepository(application)
    val showProgressBar: LiveData<Boolean>
    val listItems: LiveData<ArrayList<MerchantProductListItems>>
    val allProductListItems: LiveData<ArrayList<MerchantProductListItems>>
    val merchantOrderListItems: LiveData<ArrayList<MyOrderListItems>>


    init {
        this.showProgressBar = repository.showProgressBar
        this.listItems = repository.listItems
        this.allProductListItems = repository.allProductListItems
        this.merchantOrderListItems = repository.merchantOrderListItems
    }

    fun changeState() {
        repository.changeState()
    }


    fun getAllProductListForMerchant(merchantId: String) {
        repository.getAllProductListForMerchant(merchantId)
    }
    fun getMerchantWiseProductList(merchantId: String) {
        repository.getMerchantWiseProductList(merchantId)
    }

    fun getMerchantOrderList(userId: String) {
        repository.getMerchantOrderList(userId)
    }


}