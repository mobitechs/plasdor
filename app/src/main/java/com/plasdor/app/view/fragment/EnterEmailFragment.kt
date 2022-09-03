package com.plasdor.app.view.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.plasdor.app.R
import com.plasdor.app.callbacks.ApiResponse
import com.plasdor.app.model.UserModel
import com.plasdor.app.session.SharePreferenceManager
import com.plasdor.app.utils.*
import com.plasdor.app.view.activity.AuthActivity
import kotlinx.android.synthetic.main.loader.*
import org.json.JSONException
import org.json.JSONObject
import java.lang.String
import java.util.*

class EnterEmailFragment : Fragment(),GoogleApiClient.OnConnectionFailedListener, ApiResponse {

    lateinit var rootView: View
    lateinit var txtEmail: AppCompatEditText
    lateinit var btnSubmit: Button
    lateinit var layoutLoader: RelativeLayout
    lateinit var btnEmailLogin: RelativeLayout
    lateinit var btnGmailLogin: RelativeLayout
    lateinit var loginViaEmailLayout: LinearLayout
    var email = ""

    lateinit var gso: GoogleSignInOptions
    lateinit var mGoogleSignInClient: GoogleSignInClient
    lateinit var googleApiClient: GoogleApiClient
    private val RC_SIGN_IN = 1

    var userName = ""
    var avtarUrl = ""
    var socialUserId = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_enter_email, container, false)
        initView()
        return rootView
    }

    private fun initView() {

        logOutFromAccounts()

        txtEmail = rootView.findViewById(R.id.txtEmail)
        btnSubmit = rootView.findViewById(R.id.btnSubmit)
        layoutLoader = rootView.findViewById(R.id.layoutLoader)
        btnEmailLogin = rootView.findViewById(R.id.btnEmailLogin)
        btnGmailLogin = rootView.findViewById(R.id.btnGmailLogin)
        loginViaEmailLayout = rootView.findViewById(R.id.loginViaEmailLayout)

        btnSubmit.setOnClickListener {
            gotoNext()
        }
        btnEmailLogin.setOnClickListener {
            loginViaEmailLayout.visibility = View.VISIBLE
        }

        setupGoogleSignIn()

        txtEmail.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent): Boolean {
                if (event.getAction() === KeyEvent.ACTION_DOWN) {
                    when (keyCode) {
                        KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_ENTER -> {
                            gotoNext()
                            return true
                        }
                        else -> {
                        }
                    }
                }
                return false
            }
        })
    }

    private fun setupGoogleSignIn() {
        googleApiClient = GoogleApiClient.Builder(requireActivity())
            .enableAutoManage(requireActivity(), this)
            .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
            .build()

        btnGmailLogin.setOnClickListener {

//            requireActivity().showToastMsg("Work in progress")
            layoutLoader.visibility = View.VISIBLE
            val intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient)
            startActivityForResult(intent, RC_SIGN_IN)
        }
    }
    private fun logOutFromAccounts() {

        //facebook Logout
//        LoginManager.getInstance().logOut()

        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        //google logout
        mGoogleSignInClient.signOut()
            .addOnCompleteListener(requireActivity(), object : OnCompleteListener<Void?> {
                override fun onComplete(task: Task<Void?>) {

                }
            })
    }

    private fun handleSignInResult(result: GoogleSignInResult) {
        layoutLoader.visibility = View.GONE
        if (result.isSuccess) {
            val account = result.signInAccount
            userName = account!!.displayName.toString()
            email = account.email.toString()
            socialUserId = account.id.toString()
            if(account.photoUrl != null){
                avtarUrl = account.photoUrl.toString()
            }
            // showToastMsg("Login Success $userName $email")
            socialMediaLoginSuccess()
        } else {
            Toast.makeText(requireContext(), "Sign in cancel", Toast.LENGTH_LONG).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

//        callbackManager.onActivityResult(requestCode, resultCode, data)
        if (requestCode === RC_SIGN_IN) {
            val result = data?.let { Auth.GoogleSignInApi.getSignInResultFromIntent(it) }
            if (result != null) {
                handleSignInResult(result)
            }
        }
    }

    override fun onConnectionFailed(p0: ConnectionResult) {

    }


    private fun socialMediaLoginSuccess() {
        //call get otp api
        val method = "checkIsUserExist"
        val jsonObject = JSONObject()
        try {
            jsonObject.put("method", method)
            jsonObject.put("email", email)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        apiPostCall(Constants.BASE_URL, jsonObject, this, method)
    }

    private fun gotoNext() {
        email = txtEmail.text.toString()
        if (email.equals("") ) {
            requireContext().showToastMsg("Please Enter Email.")
        } else {
            (context as AuthActivity).openOTPPage(email)
        }
    }

    override fun onSuccess(data: Any, tag: kotlin.String) {
        if (data.equals("NEW_USER")) {
            (context as AuthActivity).openRegistrationFrag(email)
        } else {
            val gson = Gson()
            val type = object : TypeToken<ArrayList<UserModel>>() {}.type
            var user: ArrayList<UserModel>? = gson.fromJson(data.toString(), type)

            Log.d("user", "" + user)
            SharePreferenceManager.getInstance(requireContext()).saveUserLogin(Constants.USERDATA, user)
//            userType = SharePreferenceManager.getInstance(requireContext()).getUserLogin(Constants.USERDATA)?.get(0)!!.userType

            SharePreferenceManager.getInstance(requireContext()).save(Constants.EARNED_POINTS, user!![0].wallet)
            SharePreferenceManager.getInstance(requireContext()).save(Constants.FIRST_FREE_ORDER_COMPLETE, user!![0].firstFreeOrder)

            SharePreferenceManager.getInstance(requireContext()).save(Constants.ISLOGIN, true)
            requireContext().checkLogin()
        }
    }

    override fun onFailure(message: kotlin.String) {

    }
}