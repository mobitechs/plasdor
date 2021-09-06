package com.plasdor.app.view.activity

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
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
import com.plasdor.app.viewModel.UserListViewModel
import java.lang.Exception
import android.telephony.TelephonyManager
import com.plasdor.app.callbacks.ApiResponse
import com.plasdor.app.utils.*
import org.json.JSONException
import org.json.JSONObject


class SplashActivity : AppCompatActivity(), ApiResponse {
    lateinit var viewModelUser: UserListViewModel

    var allProductListItems = ArrayList<ProductListItems>()
    var referredLinkToUserId =""
    var userId =""

    var isTokenSAveAPICalled = false
    var isTokenUpdate = false
    var token = ""
    var deviceId = ""



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        val window: Window = window
        setStatusColor(window, resources.getColor(R.color.black))

        val userDetails2 = SharePreferenceManager.getInstance(this)
            .getUserLogin(Constants.USERDATA)

        if (userDetails2?.get(0) != null) {
            userId = SharePreferenceManager.getInstance(this).getUserLogin(Constants.USERDATA)?.get(0)!!.userId.toString()
        }

        token =  SharePreferenceManager.getInstance(this).getValueString(Constants.TOKEN).toString()
        deviceId =  SharePreferenceManager.getInstance(this).getValueString(Constants.DEVICE_ID).toString()
        isTokenUpdate =  SharePreferenceManager.getInstance(this).getValueBoolean(Constants.IS_TOKEN_UPDATE)
        isTokenSAveAPICalled =  SharePreferenceManager.getInstance(this).getValueBoolean(Constants.IS_TOKEN_SAVE_API_CALLED)



        if(isTokenUpdate && !isTokenSAveAPICalled && !userId.equals("")){
            var method = "addDeviceDetails"
            val jsonObject = JSONObject()
            try {
                jsonObject.put("method", method)
                jsonObject.put("userId", userId)
                jsonObject.put("token", token)
                jsonObject.put("deviceId", deviceId)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            apiPostCall(Constants.BASE_URL, jsonObject, this, method)
        }

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

    override fun onSuccess(data: Any, tag: String) {
        SharePreferenceManager.getInstance(applicationContext).save(Constants.IS_TOKEN_SAVE_API_CALLED, true)
    }

    override fun onFailure(message: String) {

    }

}