package com.plasdor.app.callbacks

import com.plasdor.app.model.AddressListItems

interface AddressSelectCallback {
    fun selectedAddress(item: AddressListItems)
    fun selectedAddressToEdit(item: AddressListItems)
    fun selectedAddressToDelete(item: AddressListItems, position: Int)
}