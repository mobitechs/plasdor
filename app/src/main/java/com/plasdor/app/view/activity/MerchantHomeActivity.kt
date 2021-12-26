package com.plasdor.app.view.activity

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.bumptech.glide.Glide
import com.fxn.utility.PermUtil
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.plasdor.app.R
import com.plasdor.app.callbacks.AlertDialogBtnClickedCallBack
import com.plasdor.app.callbacks.ApiResponse
import com.plasdor.app.model.UserModel
import com.plasdor.app.session.SharePreferenceManager
import com.plasdor.app.utils.*
import com.plasdor.app.view.fragment.*
import com.plasdor.app.view.fragment.merchant.MerchantAllProductListFragment
import com.plasdor.app.view.fragment.merchant.MerchantFragmentProductList
import com.plasdor.app.view.fragment.merchant.MerchantOrderDetailsFragment
import com.plasdor.app.view.fragment.merchant.MerchantOrderListFragment
import kotlinx.android.synthetic.main.activity_merchant_home.*
import kotlinx.android.synthetic.main.contenair.*
import kotlinx.android.synthetic.main.drawer_layout_merchant.*

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.File

class   MerchantHomeActivity : AppCompatActivity(), View.OnClickListener,
    AlertDialogBtnClickedCallBack, ApiResponse {
    private var doubleBackToExitPressedOnce = false
    var userType = ""
    var senderUserId = ""
    var userId = ""
    lateinit var bottomNavigation: BottomNavigationView
    lateinit var bottomNavigationMerchant: BottomNavigationView
    var proPicUrlPath = ""
    var proPicFile: File? = null
    var isProfilePicNeedToChange = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_merchant_home)

        setStatusColor(window, resources.getColor(R.color.colorAccent))

        bottomNavigation = findViewById(R.id.bottom_navigation)
        bottomNavigationMerchant = findViewById(R.id.merchant_bottom_navigation)

        bottomNavigation.visibility = View.GONE
        bottomNavigationMerchant.visibility = View.VISIBLE
        bottomNavigationMerchant.setOnNavigationItemSelectedListener(navigationItemSelectedListener)

        if (intent.getStringExtra("ImFrom").equals("OrderGenerate")) {
            displayView(3)
        } else {
            displayView(1)
        }


        drawerInit()
        setupDrawer()
        getReferralLinkDetails()

    }

    var navigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menuHome -> {
                    displayView(1)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.menuMyProduct -> {
                    displayView(4)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.menuMyOrder -> {
                    displayView(3)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.menuProfile -> {
                    displayView(2)
                    return@OnNavigationItemSelectedListener true
                }

            }
            false
        }

    private fun getReferralLinkDetails() {
        FirebaseDynamicLinks.getInstance()
            .getDynamicLink(intent)
            .addOnSuccessListener(
                this
            ) { pendingDynamicLinkData ->
                // Get deep link from result (may be null if no link is found)
                var deepLink: Uri? = null
                if (pendingDynamicLinkData != null) {
                    deepLink = pendingDynamicLinkData.link

//                    showToastMsg(deepLink.toString())
                    var referredLink = deepLink.toString()
                    Log.e("Received Referral", "ptkk Link " + deepLink)

                    try {
                        senderUserId = referredLink.substring(referredLink.lastIndexOf("=") + 1)
//                        showToastMsg(senderUserId)
                    } catch (e: Exception) {

                    }

                    storeReferralDetails()
                }

            }
            .addOnFailureListener(
                this
            ) { e ->
                Log.w("Referred Link ", "getDynamicLink:onFailure", e)
            }
    }

    private fun storeReferralDetails() {
        val method = "addReferralPoints"
        val jsonObject = JSONObject()
        try {
            jsonObject.put("method", method)
            jsonObject.put("senderUserId", senderUserId)
            jsonObject.put("installerUserId", userId)
            jsonObject.put("referralPoints", Constants.POINTS_ON_SHARE)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        apiPostCall(Constants.BASE_URL, jsonObject, this, method)
    }

    fun drawerInit() {
        ivMenu.setOnClickListener(this)
        llHome.setOnClickListener(this)
        llProfile.setOnClickListener(this)
        llMyOrder.setOnClickListener(this)
        lMyProduct.setOnClickListener(this)
        llShare.setOnClickListener(this)
        ivClose.setOnClickListener(this)
        llFeedback.setOnClickListener(this)
        llSupport.setOnClickListener(this)
        llLogout.setOnClickListener(this)
        menuWallet.setOnClickListener(this)
        imgUserPic.setOnClickListener(this)
        imgUserPic2.setOnClickListener(this)
        llUpdateKyc.setOnClickListener(this)
        llMyReferral.setOnClickListener(this)

    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.ivMenu -> {
                drawer.openDrawer(Gravity.LEFT)
            }
            R.id.ivClose -> {
                drawerOpenorClose()
            }
            R.id.llHome -> {
                displayView(1)
            }
            R.id.llProfile -> {
                displayView(2)
            }
            R.id.llMyOrder -> {
                displayView(3)
            }

            R.id.lMyProduct -> {
                displayView(4)
            }
            R.id.menuWallet -> {
                displayView(8)
            }
            R.id.llFeedback -> {
                displayView(5)
            }
            R.id.llSupport -> {
                displayView(6)
            }
            R.id.llUpdateKyc -> {
                displayView(10)
            }
            R.id.llMyReferral -> {
                displayView(11)
            }

            R.id.imgUserPic2 -> {
                isProfilePicNeedToChange = true
                getImage()
            }
            R.id.imgUserPic -> {
                isProfilePicNeedToChange = true
                getImage()
            }
            R.id.llShare -> {
                ShareApp()
            }
            R.id.llLogout -> {
                drawerOpenorClose()
                showAlertDialog("Confirmation", "Do you really want to logout?", "Yes", "NO", this)
            }
        }
//        drawerOpenorClose()
    }

    fun drawerOpenorClose() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
    }

    fun getImage() {
        ImagePicker.with(this)
            .crop()                    // Crop image(Optional), Check Customization for more option
            .saveDir(this.filesDir.path + File.separator + "Images/")

            .compress(1024)            //Final image size will be less than 1 MB(Optional)
            .maxResultSize(
                1080,
                1080
            )    //Final image resolution will be less than 1080 x 1080(Optional)
            .start()

//        Pix.start(this, Options.init().setRequestCode(100))
    }

    private fun setupDrawer() {
        val userDetails = SharePreferenceManager.getInstance(this).getUserLogin(Constants.USERDATA)

        if (userDetails?.get(0)?.name != null) {
            userType = userDetails!![0].userType
            userId = userDetails!![0].userId
            txtUserName.setText(userDetails!![0].name)
            txtMobile.setText(userDetails!![0].mobile)
            txtEmail.setText(userDetails!![0].email)

            imgUserPic.setImage(userDetails!![0].userProfilePic,R.drawable.user)
        }

    }

    private fun ShareApp() {
//        val sendIntent = Intent()
//        sendIntent.action = Intent.ACTION_SEND
//        sendIntent.putExtra(
//            Intent.EXTRA_TEXT,
//            "Download the app from given url. \n\n "
//        )
//        sendIntent.type = "text/plain"
//        startActivity(sendIntent)

        val dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
            .setLink(Uri.parse("https://www.plasdorservice.com/"))
            .setDomainUriPrefix("https://plasdorservicemobi.page.link/") // Open links with this app on Android
            .setAndroidParameters(
                DynamicLink.AndroidParameters.Builder().build()
            ) // Open links with com.example.ios on iOS
            .setIosParameters(DynamicLink.IosParameters.Builder("com.example.ios").build())
            .buildDynamicLink()

        val dynamicLinkUri: Uri = dynamicLink.uri
        Log.e("main", "Long Refer " + dynamicLinkUri)

        ///manual Url Link Text
        val manualUrlLinkText = "https://plasdorservicemobi.page.link//?" +
                "link=http://www.plasdorservice.com/" +
                "&apn=" + packageName +
                "&st=" + "My Refer Link" +
                "&sd=" + "Reward Coins 15" +
                "&si=" + "https://plasdorservice.com/images/logo_.png"

        val shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
//            .setLongLink(Uri.parse(dynamicLink.uri.toString()))
            .setLongLink(manualUrlLinkText.toUri()) // this is for manual url
            .buildShortDynamicLink()
            .addOnCompleteListener(
                this
            ) { task ->
                if (task.isSuccessful) {
                    // Short link created
                    val shortLink = task.result.shortLink
                    val flowchartLink = task.result.previewLink
                    Log.e("main", "short Refer " + shortLink)

                    val sendIntent = Intent()
                    sendIntent.action = Intent.ACTION_SEND
                    sendIntent.putExtra(
                        Intent.EXTRA_TEXT,
                        shortLink.toString()
                    )
                    sendIntent.type = "text/plain"
                    startActivity(sendIntent)
                } else {
                    // Error
                    // ...
                    Log.e("main", "Error " + task.exception)
                }
            }
    }

//    private fun toolbarTitle(title: String) {
////        tvToolbarTitle.text = title
//        var  actionBar = getSupportActionBar();
//        if(actionBar != null)
//        {
//            actionBar.setTitle(title)
//        }
//    }


    private fun toolbarTitle(title: String) {
        tvToolbarTitle.text = title
    }

    fun displayView(pos: Int) {
        drawerOpenorClose()
        when (pos) {
            1 -> {
                toolbarTitle("All Products")
                replaceFragment(
                    MerchantAllProductListFragment(),
                    false,
                    R.id.nav_host_fragment,
                    "MerchantAllProductListFragment"
                )
            }
            2 -> {
                toolbarTitle("Profile")
                addFragment(
                    ProfileFragment(),
                    false,
                    R.id.nav_host_fragment,
                    "ProfileFragment"
                )

            }
            3 -> {
                toolbarTitle("My Orders")
                addFragment(
                    MerchantOrderListFragment(),
                    false,
                    R.id.nav_host_fragment,
                    "MerchantOrderListFragment"
                )
            }
            4 -> {
                toolbarTitle("My Products")
                addFragment(
                    MerchantFragmentProductList(),
                    false,
                    R.id.nav_host_fragment,
                    "MerchantFragmentProductList"
                )
            }
            5 -> {
                toolbarTitle("Add Feedback")
                addFragment(
                    FeedbackFragment(),
                    false,
                    R.id.nav_host_fragment,
                    "FeedbackFragment"
                )
            }
            6 -> {
                toolbarTitle("Support")
                addFragment(
                    SupportFragment(),
                    false,
                    R.id.nav_host_fragment,
                    "SupportFragment"
                )
            }
            8 -> {
                toolbarTitle("Wallet")
                addFragment(
                    WalletFragment(),
                    false,
                    R.id.nav_host_fragment,
                    "WalletFragment"
                )
            }
            10 -> {
                OpenKYCUpdate()
            }

            11 -> {
                toolbarTitle("My Referral")
                addFragment(
                    MyRefferalListFragment(),
                    false,
                    R.id.nav_host_fragment,
                    "MyRefferalListFragment"
                )
            }

        }
    }


    fun OpenKYCUpdate() {
        toolbarTitle("KYC Update")
        addFragment(
            UpdateKycFragment(),
            false,
            R.id.nav_host_fragment,
            "UpdateKycFragment"
        )
    }

    fun openPage(menuId: String) {
        if (menuId == "1") {
            displayView(1)
        } else if (menuId == "2") {
            displayView(2)
        } else if (menuId == "3") {
            displayView(3)
        } else if (menuId == "4") {
            displayView(4)
        }

    }

    fun OpenOrderDetails(bundle: Bundle) {
        addFragmentWithData(
            MerchantOrderDetailsFragment(),
            false,
            R.id.nav_host_fragment,
            "MerchantOrderDetailsFragment",
            bundle
        )
    }

    override fun onBackPressed() {
        toolbarTitle("Home")
        val fragment: Fragment? = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)


        if (fragment != null && ((fragment is MerchantAllProductListFragment))) {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed()
                return
            }

            this.doubleBackToExitPressedOnce = true
            Toast.makeText(this, getString(R.string.double_tap), Toast.LENGTH_SHORT).show()

            Handler().postDelayed(Runnable { doubleBackToExitPressedOnce = false }, 2000)
        } else {

            displayView(1)
            bottomNavigationMerchant.selectedItemId = R.id.menuHome
            super.onBackPressed()
        }
    }

    private fun logout() {

        SharePreferenceManager.getInstance(this).clearSharedPreference(this)
        finish()
    }

    override fun positiveBtnClicked() {
        logout()
    }

    override fun negativeBtnClicked() {

    }

    override fun onSuccess(data: Any, tag: String) {

    }

    override fun onFailure(message: String) {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (isProfilePicNeedToChange) {
            if (resultCode == Activity.RESULT_OK) {
                val fileUri = data?.data
                proPicFile = null
                proPicFile = ImagePicker.getFile(data)!!
                proPicUrlPath = ImagePicker.getFilePath(data)!!
//                imgPassbook.setImageURI(fileUri)

                Glide.with(this)
                    .load(fileUri)
                    .into(imgUserPic)

                callUpdateProfileAPI()
            }
        }
    }
    private fun callUpdateProfileAPI() {
//        progressBar.visibility = View.VISIBLE
        val method = "updateProfilePic"
        val androidNetworking = AndroidNetworking.upload(Constants.BASE_URL)
        if (proPicFile?.isFile == true) {
            androidNetworking.addMultipartFile("proPicFile", proPicFile)
        }

        androidNetworking.addMultipartParameter("userId", userId)
        androidNetworking.addMultipartParameter("proPicUrl", proPicUrlPath)
        androidNetworking.addMultipartParameter("method", method)
        androidNetworking.setTag(method)
        androidNetworking.setPriority(Priority.HIGH)
        androidNetworking.build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(data: JSONObject) {
                    try {
                        if (data.equals("FAILED") || data.equals("FAILED_IMAGE")) {
                            showToastMsg("failed to update profile pic")
                        } else {
                            showToastMsg("Profile Pic Successfully Updated")

                            if (data.get("Response") is JSONArray) {
                                val userData = data.getJSONArray("Response")

                                val gson = Gson()
                                val type = object : TypeToken<ArrayList<UserModel>>() {}.type
                                var user: ArrayList<UserModel>? =
                                    gson.fromJson(userData.toString(), type)

                                Log.d("user", "" + user)
                                SharePreferenceManager.getInstance(this@MerchantHomeActivity)
                                    .saveUserLogin(Constants.USERDATA, user)

                            }
                        }
                    } catch (e: java.lang.Exception) {
                        e.message
                        showToastMsg("Exception: " + e.message)
                    }
                }

                override fun onError(error: ANError) {
                    error.errorDetail
                    showToastMsg("Error: " + error.errorDetail)
                }
            })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PermUtil.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getImage()
                } else {
                    showToastMsg("Approve permissions to open Pix ImagePicker.")
                }
                return
            }
        }
    }
}