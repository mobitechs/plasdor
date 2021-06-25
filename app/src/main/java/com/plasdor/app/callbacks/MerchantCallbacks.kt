package com.plasdor.app.callbacks

import com.plasdor.app.model.HomeMenuItems
import com.plasdor.app.model.MerchantProductListItems
import com.plasdor.app.model.ProductListItems
import com.plasdor.app.model.UserModel

interface MerchantProductClickListener {
    fun addMyProduct(item: MerchantProductListItems, position: Int)
    fun removeMyProduct(item: MerchantProductListItems, position: Int)
    fun selectProduct(item: MerchantProductListItems, position: Int)
    fun addToAvailable(item: MerchantProductListItems, position: Int)
    fun editProduct(item: MerchantProductListItems, position: Int)
}

interface MerchantHomeMenuClickListener {
    fun selectProduct(item: HomeMenuItems, position: Int)
}

interface AllProductClickListener {
    fun addMyProduct(item: ProductListItems, position: Int)
    fun removeMyProduct(item: ProductListItems, position: Int)
    fun selectProduct(item: ProductListItems, position: Int)
}
interface MerchantSelectionClickListener {
    fun selectMerchant(item: UserModel, position: Int)
}