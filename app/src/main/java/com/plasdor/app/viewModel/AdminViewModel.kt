package com.plasdor.app.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.plasdor.app.model.*
import com.plasdor.app.repository.AdminRepository

class AdminViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AdminRepository(application)
    val showProgressBar: LiveData<Boolean>
    val allOrderListItems: LiveData<ArrayList<AdminAllOrderListItems>>
    val allProductListItems: LiveData<ArrayList<ProductListItems>>
    val allMerchantListItems: LiveData<ArrayList<UserModel>>
    val allUserListItems: LiveData<ArrayList<UserModel>>


    init {
        this.showProgressBar = repository.showProgressBar
        this.allOrderListItems = repository.allOrderListItems
        this.allProductListItems = repository.allProductListItems
        this.allMerchantListItems = repository.allMerchantListItems
        this.allUserListItems = repository.allUserListItems
    }

    fun changeState() {
        repository.changeState()
    }


    fun adminAllProductList() {
        repository.adminAllProductList()
    }
    fun adminAllOrderList() {
        repository.adminAllOrderList()
    }
    fun adminAllMerchantList() {
        repository.adminAllMerchantList()
    }
    fun adminAllUserList() {
        repository.adminAllUserList()
    }




}