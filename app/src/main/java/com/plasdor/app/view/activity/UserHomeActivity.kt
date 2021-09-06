package com.plasdor.app.view.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.google.firebase.dynamiclinks.DynamicLink.AndroidParameters
import com.google.firebase.dynamiclinks.DynamicLink.IosParameters
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.plasdor.app.R
import com.plasdor.app.callbacks.AlertDialogBtnClickedCallBack
import com.plasdor.app.callbacks.ApiResponse
import com.plasdor.app.model.ProductListItems
import com.plasdor.app.session.SharePreferenceManager
import com.plasdor.app.utils.*
import com.plasdor.app.view.fragment.*
import com.plasdor.app.view.fragment.admin.AdminHomeFragment
import com.plasdor.app.view.fragment.user.*
import kotlinx.android.synthetic.main.activity_home_user.*
import kotlinx.android.synthetic.main.contenair.*
import kotlinx.android.synthetic.main.drawer_layout_user.*
import org.json.JSONException
import org.json.JSONObject
import com.google.android.material.bottomnavigation.BottomNavigationView

import androidx.annotation.NonNull








class UserHomeActivity : AppCompatActivity(), View.OnClickListener, AlertDialogBtnClickedCallBack,
    ApiResponse {

    lateinit var bottomNavigation: BottomNavigationView
    lateinit var bottomNavigationMerchant: BottomNavigationView
    private var doubleBackToExitPressedOnce = false
    var cartCount = 0
    var userType = ""
    var userId = ""
    var senderUserId = ""
    var alertFor = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_user)
        val window: Window = window
        setStatusColor(window, resources.getColor(R.color.colorAccent))

        bottomNavigation = findViewById(R.id.bottom_navigation)
        bottomNavigationMerchant = findViewById(R.id.merchant_bottom_navigation)

        bottomNavigation.visibility = View.VISIBLE
        bottomNavigationMerchant.visibility = View.GONE
        bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener)
        drawerInit()
        setupDrawer()
        getReferralLinkDetails()

        if(intent.getStringExtra("ImFrom").equals("Campaign")){
            displayView(7)
        }
        else if(intent.getStringExtra("ImFrom").equals("OrderGenerate")){
            displayView(3)
        }
        else{
            displayView(1)
        }

    }


    var navigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menuHome -> {
                    displayView(1)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.menuBazar -> {
                    displayView(7)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.menuMyOrder -> {
                    displayView(3)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.menuWallet -> {
                    displayView(8)
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
        layoutCart.setOnClickListener(this)
        llProfile.setOnClickListener(this)
        llMyOrder.setOnClickListener(this)
        llShare.setOnClickListener(this)
        ivClose.setOnClickListener(this)
        llBazar.setOnClickListener(this)
        llFeedback.setOnClickListener(this)
        llSupport.setOnClickListener(this)
        ivClose.setOnClickListener(this)
        llLogout.setOnClickListener(this)
        menuWallet.setOnClickListener(this)
        llRentOnPlasdor.setOnClickListener(this)
        llUpdateKyc.setOnClickListener(this)

    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.ivMenu -> {
                drawer.openDrawer(Gravity.LEFT)
            }
            R.id.ivClose -> {

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
            R.id.layoutCart -> {
                if (cartCount > 0) {
                    displayView(4)
                } else {
                    showToastMsg("Cart is empty.")
                }
            }
            R.id.llShare -> {
                ShareApp()
            }
            R.id.llFeedback -> {
                displayView(5)
            }
            R.id.llSupport -> {
                displayView(6)
            }
            R.id.llBazar -> {
                displayView(7)
            }
            R.id.menuWallet -> {
                displayView(8)
            }
            R.id.llRentOnPlasdor -> {
                displayView(9)
            }
            R.id.llUpdateKyc -> {
                displayView(10)
            }
            R.id.llLogout -> {
                //clear sesssion
                drawerOpenorClose()
                showAlertDialog("Confirmation", "Do you really want to logout?", "Yes", "NO", this)
            }
        }
        drawerOpenorClose()
    }

    fun drawerOpenorClose() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
    }

    private fun setupDrawer() {
        val userDetails = SharePreferenceManager.getInstance(this).getUserLogin(Constants.USERDATA)

        if (userDetails?.get(0)?.name != null) {
            userType = userDetails!![0].userType
            userId = userDetails!![0].userId

            txtUserName.setText(userDetails!![0].name)
            txtMobile.setText(userDetails!![0].mobile)
            txtEmail.setText(userDetails!![0].email)

        }

    }

    private fun ShareApp() {

        val dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
            .setLink(Uri.parse("http://www.plasdorservice.com/"))
            .setDomainUriPrefix("https://plasdorservicemobi.page.link") // Open links with this app on Android
            .setAndroidParameters(
                AndroidParameters.Builder().build()
            ) // Open links with com.example.ios on iOS
            .setIosParameters(IosParameters.Builder("com.example.ios").build())
            .buildDynamicLink()

        val dynamicLinkUri: Uri = dynamicLink.uri
        Log.e("main", "Long Refer " + dynamicLinkUri)

        ///manual Url Link Text
        val manualUrlLinkText = "https://plasdorservicemobi.page.link/?" +
                "link=http://www.mobitechs.in/plasdor/api/plasdor.php?referalUserId=$userId" +
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
//                    showToastMsg(shortLink.toString())
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
                    showToastMsg(task.exception.toString())
                }
            }
    }

    private fun logout() {
        SharePreferenceManager.getInstance(this).clearSharedPreference(this)
        finish()
    }

    fun displayView(pos: Int) {
        when (pos) {
            1 -> {
                toolbarTitle("Home")
                replaceFragment(
                    UserHomeFragment(),
                    false,
                    R.id.nav_host_fragment,
                    "UserHomeFragment"
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
                OpenOrderFragment()
            }
            4 -> {
                openCartPage()
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
            7 -> {
                openBazarFragment()
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
            9 -> {
                toolbarTitle("Rent On Plasdor")
                addFragment(
                    RentOnPlasdorFragment(),
                    false,
                    R.id.nav_host_fragment,
                    "RentOnPlasdorFragment"
                )
            }
            10 -> {
                OpenKYCUpdate()
            }
        }
        drawerOpenorClose()
    }

    fun openBazarFragment() {
        toolbarTitle("Bazar")
        addFragment(
            BazarListFragment(),
            false,
            R.id.nav_host_fragment,
            "BazarListFragment"
        )
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

    fun openCartPage() {
        toolbarTitle("Cart")
        addFragment(
            CartFragment(),
            false,
            R.id.nav_host_fragment,
            "CartFragment"
        )
    }

    private fun toolbarTitle(title: String) {
        tvToolbarTitle.text = title
//        txtSearch.visibility = View.VISIBLE
//        tvToolbarTitle.visibility = View.GONE
    }

    override fun onResume() {
        if (SharePreferenceManager.getInstance(this).getCartListItems(Constants.CartList) != null) {
            var cartListItems = SharePreferenceManager.getInstance(this)
                .getCartListItems(Constants.CartList) as ArrayList<ProductListItems>
            updateCartCount(cartListItems.size)
        }

        super.onResume()
    }

    fun updateCartCount(size: Int) {
        cartCount = size
        if (size == 0) {
            layoutCartCount.visibility = View.GONE
        } else {
            layoutCartCount.visibility = View.VISIBLE
            txtCartCount.text = size.toString()
        }
    }

    fun OpenOrderFragment() {

        addFragment(
            UserOrderFragment(),
            false,
            R.id.nav_host_fragment,
            "UserOrderFragment"
        )
    }

    fun OpenOrderDetails(bundle: Bundle) {
        addFragmentWithData(
            UserOrderDetailsFragment(),
            false,
            R.id.nav_host_fragment,
            "UserOrderDetailsFragment",
            bundle
        )
    }

    fun OpenAddressList() {
        addFragment(
            AddressListFragment(),
            false,
            R.id.nav_host_fragment,
            "AddressListFragment"
        )
    }

    fun OpenAddAddress(bundle: Bundle) {
        addFragmentWithData(
            AddAddressFragment(),
            false,
            R.id.nav_host_fragment,
            "AddAddressFragment",
            bundle
        )
    }

    fun OpenRegisterFragmentForRentOn(bundle: Bundle) {
        addFragmentWithData(
            AuthRegisterFragment(),
            false,
            R.id.nav_host_fragment,
            "AuthRegisterFragment",
            bundle
        )
    }

    fun OpenEditProductFragment(bundle: Bundle) {

    }

    fun OpenProductDetailsFragment(bundle: Bundle) {
        addFragmentWithData(
            ProductDetailsFragment(),
            false,
            R.id.nav_host_fragment,
            "ProductDetailsFragment",
            bundle
        )
    }
    fun OpenBazarOrderPlaceFragment(bundle: Bundle) {
        addFragmentWithData(
            BazarOrderPlaceFragment(),
            false,
            R.id.nav_host_fragment,
            "BazarOrderPlaceFragment",
            bundle
        )
    }

    fun OpenPlaceOrderFragment(bundle: Bundle) {
//        addFragmentWithData(
//            PlaceOrderFragment(),
//            false,
//            R.id.nav_host_fragment,
//            "PlaceOrderFragment",
//            bundle
//        )

//        openActivity(PlaceOrderFragment::class.java) {
//            putParcelableArrayList("merchantListItems", merchantListItems)
//            putString("userLat", userLat)
//            putString("userLong", userLong)
//            putString("userName", userName)
//        }
    }

    override fun positiveBtnClicked() {
        logout()
    }

    override fun negativeBtnClicked() {

    }

    override fun onBackPressed() {
        val fragment: Fragment? = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)


        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            if (fragment != null && ((fragment is UserHomeFragment) || (fragment is AdminHomeFragment))) {
                if (doubleBackToExitPressedOnce) {
                    super.onBackPressed()
                    return
                }

                this.doubleBackToExitPressedOnce = true
                Toast.makeText(this, getString(R.string.double_tap), Toast.LENGTH_SHORT).show()

                Handler().postDelayed(Runnable { doubleBackToExitPressedOnce = false }, 2000)
            } else {
                displayView(1)
                bottomNavigation.selectedItemId = R.id.menuHome
                super.onBackPressed()
            }
        }
    }

    override fun onSuccess(data: Any, tag: String) {

    }

    override fun onFailure(message: String) {

    }

}