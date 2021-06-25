package com.plasdor.app.callbacks

import com.plasdor.app.model.MerchantProductListItems
import com.plasdor.app.model.ProductListItems

interface AddOrRemoveListener {
    fun addToCart(item: ProductListItems, position: Int)
    fun removeFromCart(item: ProductListItems, position: Int)
    fun editProduct(item: ProductListItems, position: Int)
    fun deleteProduct(item: ProductListItems, position: Int)
    fun selectProduct(item: ProductListItems, position: Int)
}



