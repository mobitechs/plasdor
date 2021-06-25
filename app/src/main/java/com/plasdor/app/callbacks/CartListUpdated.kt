package com.plasdor.app.callbacks

import com.plasdor.app.model.ProductListItems

interface CartListUpdated {
    fun cartListUpdated(cartListItems: ArrayList<ProductListItems>)
}