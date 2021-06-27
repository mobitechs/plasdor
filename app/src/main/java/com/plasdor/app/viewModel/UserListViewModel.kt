package com.plasdor.app.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.plasdor.app.model.AddressListItems
import com.plasdor.app.model.MyOrderListItems
import com.plasdor.app.model.ProductListItems
import com.plasdor.app.model.UserModel
import com.plasdor.app.repository.UserListRepository

class UserListViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = UserListRepository(application)
    val showProgressBar: LiveData<Boolean>
    val allProductListItems: LiveData<ArrayList<ProductListItems>>
    val addressListItems: LiveData<ArrayList<AddressListItems>>
    val myOrderListItems: LiveData<ArrayList<MyOrderListItems>>
    val merchantListItems: LiveData<ArrayList<UserModel>>


    init {
        this.showProgressBar = repository.showProgressBar
        this.addressListItems = repository.addressListItems
        this.myOrderListItems = repository.myOrderListItems
        this.allProductListItems = repository.allProductListItems
        this.merchantListItems = repository.merchantListItems
    }

    fun changeState() {
        repository.changeState()
    }


    fun getMyOrderList(userId: String) {
        repository.getMyOrderList(userId)
    }

    fun getAddressList() {
        repository.getAddressList()
    }

    fun getAllProduct() {
        repository.getAllProduct()
    }

    fun getAvailableMerchant(productId:String) {
        repository.getAvailableMerchant(productId)
    }


}