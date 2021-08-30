package com.plasdor.app.view.activity

import android.content.Intent
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
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.plasdor.app.R
import com.plasdor.app.callbacks.AlertDialogBtnClickedCallBack
import com.plasdor.app.callbacks.ApiResponse
import com.plasdor.app.session.SharePreferenceManager
import com.plasdor.app.utils.*
import com.plasdor.app.view.fragment.FullScreenImageFragment
import com.plasdor.app.view.fragment.ProfileFragment
import com.plasdor.app.view.fragment.admin.*
import kotlinx.android.synthetic.main.activity_admin_home.*
import kotlinx.android.synthetic.main.contenair.*
import kotlinx.android.synthetic.main.drawer_layout_admin.*
import org.json.JSONException
import org.json.JSONObject

class AdminHomeActivity : AppCompatActivity(), View.OnClickListener, AlertDialogBtnClickedCallBack,
    ApiResponse {
    private var doubleBackToExitPressedOnce = false

    var userType = ""
    var userId = ""
    var deviceId = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_home)

        setStatusColor(window, resources.getColor(R.color.colorAccent))

        if(intent.getStringExtra("ImFrom").equals("UserApproval")){
            displayView(9)
        }
        else if(intent.getStringExtra("ImFrom").equals("OrderGenerate")){
            displayView(3)
        }
        else{
            displayView(1)
        }
        drawerInit()
        setupDrawer()

    }

    fun drawerInit() {
        ivMenu.setOnClickListener(this)
        llHome.setOnClickListener(this)
        llProfile.setOnClickListener(this)
        lAllOrder.setOnClickListener(this)
        lAllUsers.setOnClickListener(this)
        lAllMerchant.setOnClickListener(this)
        llShare.setOnClickListener(this)
        ivClose.setOnClickListener(this)
        llLogout.setOnClickListener(this)
        lUserVerification.setOnClickListener(this)

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
            R.id.lAllOrder -> {
                displayView(3)
            }
            R.id.lAllUsers -> {
                displayView(4)
            }
            R.id.lAllMerchant -> {
                displayView(5)
            }
            R.id.lUserVerification -> {
                displayView(9)
            }
            R.id.llShare -> {
                ShareApp()
            }
            R.id.llLogout -> {
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
            .setDomainUriPrefix("https://plasdorservice.page.link/") // Open links with this app on Android
            .setAndroidParameters(
                DynamicLink.AndroidParameters.Builder().build()
            ) // Open links with com.example.ios on iOS
            .setIosParameters(DynamicLink.IosParameters.Builder("com.example.ios").build())
            .buildDynamicLink()

        val dynamicLinkUri: Uri = dynamicLink.uri
        Log.e("main", "Long Refer " + dynamicLinkUri)

        ///manual Url Link Text
        val manualUrlLinkText = "https://plasdorservice.page.link//?" +
                "link=http://www.plasdorservice.com/" +
                "&apn=" + packageName +
                "&st=" + "My Refer Link" +
                "&sd=" + "Reward Coins 20" +
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
        when (pos) {
            1 -> {
                toolbarTitle("All Products")
                replaceFragment(
                    AdminAllProductFragment(),
                    false,
                    R.id.nav_host_fragment,
                    "AdminAllProductFragment"
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
                toolbarTitle("All Orders")
                addFragment(
                    AdminAllOrdersListFragment(),
                    false,
                    R.id.nav_host_fragment,
                    "AdminAllOrdersListFragment"
                )
            }
            4 -> {
                toolbarTitle("All User")
                addFragment(
                    AdminAllUserListFragment(),
                    false,
                    R.id.nav_host_fragment,
                    "AdminAllUserListFragment"
                )
            }
            5 -> {
                toolbarTitle("All Merchant")
                addFragment(
                    AdminAllMerchantListFragment(),
                    false,
                    R.id.nav_host_fragment,
                    "AdminAllMerchantListFragment"
                )
            }
            9 -> {
                toolbarTitle("Pending Verification")
                openUserVerificationPendingList()
            }

        }
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
            AdminOrderDetailsFragment(),
            false,
            R.id.nav_host_fragment,
            "AdminOrderDetailsFragment",
            bundle
        )
    }
    fun OpenPendingUserDetails(bundle: Bundle) {
        addFragmentWithData(
            AdminPendingUserDetailsFragment(),
            false,
            R.id.nav_host_fragment,
            "AdminPendingUserDetailsFragment",
            bundle
        )
    }
    fun OpenFullScreenImageFragment(bundle: Bundle) {
        addFragmentWithData(
            FullScreenImageFragment(),
            false,
            R.id.nav_host_fragment,
            "FullScreenImageFragment",
            bundle
        )
    }
    fun openUserVerificationPendingList() {
        addFragment(
            AdminPendingUserListFragment(),
            false,
            R.id.nav_host_fragment,
            "AdminPendingUserListFragment"
        )
    }

    override fun onBackPressed() {
        toolbarTitle("Home")
        val fragment: Fragment? = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)


        if (fragment != null && ((fragment is AdminAllProductFragment))) {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed()
                return
            }

            this.doubleBackToExitPressedOnce = true
            Toast.makeText(this, getString(R.string.double_tap), Toast.LENGTH_SHORT).show()

            Handler().postDelayed(Runnable { doubleBackToExitPressedOnce = false }, 2000)
        } else {
            super.onBackPressed()
            displayView(0)
        }
    }

    private fun logout() {
        deviceId = SharePreferenceManager.getInstance(this).getValueString(Constants.DEVICE_ID).toString()
        // call api to remove to token from server
        val method = "removeAdminToken"
        val jsonObject = JSONObject()
        try {
            jsonObject.put("method", method)
            jsonObject.put("userId", userId)
            jsonObject.put("deviceId", deviceId)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        apiPostCall(Constants.BASE_URL, jsonObject, this, method)

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
}