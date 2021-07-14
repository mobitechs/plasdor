package com.plasdor.app.view.activity

import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.ads.MobileAds
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.plasdor.app.R
import com.plasdor.app.model.ProductListItems
import com.plasdor.app.session.SharePreferenceManager
import com.plasdor.app.utils.Constants
import com.plasdor.app.utils.checkLogin
import com.plasdor.app.utils.setStatusColor
import com.plasdor.app.utils.showToastMsg
import com.plasdor.app.viewModel.UserListViewModel
import java.lang.Exception


class SplashActivity : AppCompatActivity() {
    lateinit var viewModelUser: UserListViewModel

    var allProductListItems = ArrayList<ProductListItems>()
    var referredLinkToUserId =""
    var userId =""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        val window: Window = window
        setStatusColor(window, resources.getColor(R.color.colorPrimaryDark))

        MobileAds.initialize(this) {}

        val userDetails = SharePreferenceManager.getInstance(this).getUserLogin(Constants.USERDATA)
        if (userDetails?.get(0)?.userType == Constants.ADMIN || userDetails?.get(0)?.userType == Constants.MERCHANT) {
            Handler().postDelayed({ checkLogin() }, SPLASH_TIME_OUT.toLong())
        } else {
            getAllProducts()
        }
    }

    private fun getAllProducts() {
        viewModelUser = ViewModelProvider(this).get(UserListViewModel::class.java)
        viewModelUser.getAllProduct()

        viewModelUser.allProductListItems.observe(this, Observer {
            allProductListItems = it
            SharePreferenceManager.getInstance(this)
                .saveCartListItems(Constants.AllProductList, allProductListItems)
            checkLogin()
        })
    }

    companion object {
        private const val SPLASH_TIME_OUT = 2000
    }

}