package com.plasdor.app.callbacks

import com.plasdor.app.model.*

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
    fun selectMerchant(item: AvailableMerchantListItem, position: Int)
}