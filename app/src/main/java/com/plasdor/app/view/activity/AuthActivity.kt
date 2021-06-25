package com.plasdor.app.view.activity

import android.os.Bundle
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import com.plasdor.app.R
import com.plasdor.app.utils.addFragmentWithData
import com.plasdor.app.utils.replaceFragment
import com.plasdor.app.utils.setStatusColor
import com.plasdor.app.view.fragment.*

class AuthActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        val window: Window = window
        setStatusColor(window, resources.getColor(R.color.colorPrimaryDark))

        displayView(1)
    }


    fun displayView(pos: Int) {
        when (pos) {
            1 -> {
                replaceFragment(
                    EnterEmailFragment(),
                    false,
                    R.id.nav_host_fragment,
                    "EnterEmailFragment"
                )
            }
            2 -> {
                replaceFragment(
                    AuthLoginFragment(),
                    false,
                    R.id.nav_host_fragment,
                    "AuthLoginFragment"
                )
            }
            3 -> {
                replaceFragment(
                    AuthRegisterFragment(),
                    false,
                    R.id.nav_host_fragment,
                    "AuthRegisterFragment"
                )
            }
            4 -> {
                replaceFragment(
                    AuthForgotPasswordFragment(),
                    false,
                    R.id.nav_host_fragment,
                    "AuthForgotPasswordFragment"
                )
            }
        }
    }

    fun openOTPPage(email: String) {
        val bundle = Bundle()
        bundle.putString("email", email)
        addFragmentWithData(
            EnterOTPFragment(),
            false,
            R.id.nav_host_fragment,
            "EnterOTPFragment", bundle
        )
    }

    fun openLoginPage() {
        displayView(2)
    }
    fun openRegisterPage() {
        displayView(3)
    }
    fun openForgotPasswordPage() {
        displayView(4)
    }

    fun openSetPassword(email: String) {
        val bundle = Bundle()
        bundle.putString("email", email)
        addFragmentWithData(
            AuthSetPasswordFragment(),
            false,
            R.id.nav_host_fragment,
            "AuthSetPasswordFragment", bundle
        )
    }
    fun openRegistrationFrag(email: String) {
        val bundle = Bundle()
        bundle.putString("email", email)
        addFragmentWithData(
            AuthRegisterFragment(),
            false,
            R.id.nav_host_fragment,
            "AuthRegisterFragment", bundle
        )
    }


}