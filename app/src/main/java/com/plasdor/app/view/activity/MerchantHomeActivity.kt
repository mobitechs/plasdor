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
import com.plasdor.app.session.SharePreferenceManager
import com.plasdor.app.utils.*
import com.plasdor.app.view.fragment.ProfileFragment
import com.plasdor.app.view.fragment.merchant.*
import kotlinx.android.synthetic.main.activity_merchant_home.*
import kotlinx.android.synthetic.main.contenair.*
import kotlinx.android.synthetic.main.drawer_layout.ivClose
import kotlinx.android.synthetic.main.drawer_layout.llHome
import kotlinx.android.synthetic.main.drawer_layout.llLogout
import kotlinx.android.synthetic.main.drawer_layout.llProfile
import kotlinx.android.synthetic.main.drawer_layout.llShare
import kotlinx.android.synthetic.main.drawer_layout.txtEmail
import kotlinx.android.synthetic.main.drawer_layout.txtMobile
import kotlinx.android.synthetic.main.drawer_layout.txtUserName
import kotlinx.android.synthetic.main.drawer_layout_admin.*
import kotlinx.android.synthetic.main.drawer_layout_admin.lAllOrder
import kotlinx.android.synthetic.main.drawer_layout_admin.lAllUsers
import kotlinx.android.synthetic.main.drawer_layout_merchant.*

class   MerchantHomeActivity : AppCompatActivity(), View.OnClickListener,
    AlertDialogBtnClickedCallBack {
    private var doubleBackToExitPressedOnce = false
    var userType = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_merchant_home)

        setStatusColor(window, resources.getColor(R.color.colorPrimaryDark))
        displayView(1)
        drawerInit()
        setupDrawer()

    }

    fun drawerInit() {
        ivMenu.setOnClickListener(this)
        llHome.setOnClickListener(this)
        llProfile.setOnClickListener(this)
        llMyOrder.setOnClickListener(this)
        lMyProduct.setOnClickListener(this)
        llShare.setOnClickListener(this)
        ivClose.setOnClickListener(this)
        llLogout.setOnClickListener(this)

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

            R.id.lMyProduct -> {
                displayView(4)
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
            .setLink(Uri.parse("https://www.mobitechs.in/"))
            .setDomainUriPrefix("https://plasdor.page.link") // Open links with this app on Android
            .setAndroidParameters(
                DynamicLink.AndroidParameters.Builder().build()
            ) // Open links with com.example.ios on iOS
            .setIosParameters(DynamicLink.IosParameters.Builder("com.example.ios").build())
            .buildDynamicLink()

        val dynamicLinkUri: Uri = dynamicLink.uri
        Log.e("main", "Long Refer " + dynamicLinkUri)

        ///manual Url Link Text
        val manualUrlLinkText = "https://plasdor.page.link/?" +
                "link=http://www.mobitechs.in/" +
                "&apn=" + packageName +
                "&st=" + "My Refer Link" +
                "&sd=" + "Reward Coins 20" +
                "&si=" + "https://mobitechs.in/assets/images/mobitechsmain.webp"

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
            super.onBackPressed()
            displayView(0)
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
}