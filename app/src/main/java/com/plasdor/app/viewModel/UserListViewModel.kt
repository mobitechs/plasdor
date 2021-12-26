package com.plasdor.app.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.plasdor.app.model.*
import com.plasdor.app.repository.UserListRepository

class UserListViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = UserListRepository(application)
    val showProgressBar: LiveData<Boolean>
    val allProductListItems: LiveData<ArrayList<ProductListItems>>
    val addressListItems: LiveData<ArrayList<AddressListItems>>
    val myOrderListItems: LiveData<ArrayList<MyOrderListItems>>
    val redeemHistoryListItems: LiveData<ArrayList<MyOrderListItems>>
    val merchantListItems: LiveData<ArrayList<AvailableMerchantListItem>>
    val bazarListItems: LiveData<ArrayList<ProductListItems>>
    val referralList: LiveData<ArrayList<ReferralList>>


    init {
        this.showProgressBar = repository.showProgressBar
        this.addressListItems = repository.addressListItems
        this.myOrderListItems = repository.myOrderListItems
        this.redeemHistoryListItems = repository.redeemHistoryListItems
        this.allProductListItems = repository.allProductListItems
        this.merchantListItems = repository.merchantListItems
        this.bazarListItems = repository.bazarListItems
        this.referralList = repository.referralList
    }

    fun changeState() {
        repository.changeState()
    }


    fun getMyReferralList(userId: String) {
        repository.getMyReferralList(userId)
    }
    fun getMyOrderList(userId: String) {
        repository.getMyOrderList(userId)
    }
    fun getMyRedeemHistory(userId: String) {
        repository.getMyRedeemHistory(userId)
    }

    fun getAddressList() {
        repository.getAddressList()
    }
    fun getBazarList() {
        repository.getBazarList()
    }

    fun getAllProduct() {
        repository.getAllProduct()
    }

    fun getAvailableMerchant(productId:String) {
        merchantListItems.value?.clear()
        repository.getAvailableMerchant(productId)
    }


}