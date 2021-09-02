package com.plasdor.app.view.fragment

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import com.gne.www.lib.OnPinCompletedListener
import com.gne.www.lib.PinView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.plasdor.app.R
import com.plasdor.app.callbacks.ApiResponse
import com.plasdor.app.model.UserModel
import com.plasdor.app.session.SharePreferenceManager
import com.plasdor.app.utils.*
import com.plasdor.app.view.activity.AuthActivity
import org.json.JSONException
import org.json.JSONObject
import java.lang.String
import java.util.*

class EnterOTPFragment : Fragment(), ApiResponse {

    lateinit var rootView: View
    var actualOTP = "1234"
    var otp = ""
    lateinit var pinView: PinView

    lateinit var txtUserEmailMobile: AppCompatTextView
    lateinit var btnResend: AppCompatTextView
    lateinit var txtTimer: AppCompatTextView
    lateinit var btnSubmit: Button
    var timerCounter = 30
    var otpCounter = 0
    var email = ""
    var serverResponse = ""
    var userType = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_enter_otp, container, false)
        initView()
        return rootView
    }

    private fun initView() {
        email = arguments?.getString("email").toString()
        pinView = rootView.findViewById(R.id.pinview)

        pinView.setPinBackground(getResources().getDrawable(R.drawable.bg_rect_stroke_idle))

        txtUserEmailMobile = rootView.findViewById(R.id.txtUserEmailMobile)!!
        btnResend = rootView.findViewById(R.id.btnResend)!!
        btnSubmit = rootView.findViewById(R.id.btnSubmit)!!

        txtTimer = rootView.findViewById(R.id.txtTimer)!!


        //var replaced = email.replace(".(?=.{4})".toRegex(), "*");
        val replaced = email.replace("(?<=.{2}).(?=[^@]*?.@)".toRegex(), "*")

        txtUserEmailMobile.text = "your email " + replaced

        getOtpAPI()
        btnResend.setOnClickListener {
            if (otpCounter < 4) {
                otpCounter++
                getOtpAPI()
                startTimetimerCounter()
            } else {
                requireContext().showToastMsg("You have reached the limit pls try after some time.")
            }
        }


        pinView.setOnPinCompletionListener(OnPinCompletedListener {
            otp = pinView.text
            if (otp.equals(actualOTP)) {
                //success
                pinView.setPinBackground(getResources().getDrawable(R.drawable.bg_rect_stroke_success))
                gotoNext()
            } else {
                //error
                pinView.setPinBackground(getResources().getDrawable(R.drawable.bg_rect_stroke_error))
            }
        })

        btnSubmit.setOnClickListener {
            gotoNext()
        }
    }

    private fun gotoNext() {
        otp = pinView.text
        if (!otp.equals(actualOTP)) {
            requireContext().showToastMsg("Please Enter Valid OTP.")
        } else {
            if (serverResponse.equals("NEW_USER")) {
                (context as AuthActivity).openRegistrationFrag(email)

            } else {
                SharePreferenceManager.getInstance(requireContext()).save(Constants.ISLOGIN, true)
                requireContext().checkLogin()
            }
        }
    }


    fun startTimetimerCounter() {
        timerCounter = 30
        txtTimer.visibility = View.VISIBLE
        btnResend.visibility = View.GONE
        object : CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                txtTimer.text = "00:" + timerCounter.toString()
                timerCounter--
            }

            override fun onFinish() {
                txtTimer.visibility = View.GONE
                btnResend.visibility = View.VISIBLE
            }
        }.start()
    }

    private fun getOtpAPI() {
        val rand = Random()
        actualOTP = String.format("%04d", rand.nextInt(10000))
        requireContext().showToastMsgLong("Your OTP is: " + actualOTP)

        //call get otp api
        val method = "GetOTPForEmailLogin"
        val jsonObject = JSONObject()
        try {
            jsonObject.put("method", method)
            jsonObject.put("email", email)
            jsonObject.put("otp", actualOTP)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        apiPostCall(Constants.BASE_URL, jsonObject, this, method)
    }

    override fun onSuccess(data: Any, tag: kotlin.String) {
        if (data.equals("NEW_USER")) {
            serverResponse = "NEW_USER"
        } else {
            serverResponse = "User already exist"

            val gson = Gson()
            val type = object : TypeToken<ArrayList<UserModel>>() {}.type
            var user: ArrayList<UserModel>? = gson.fromJson(data.toString(), type)

            Log.d("user", "" + user)
            SharePreferenceManager.getInstance(requireContext()).saveUserLogin(Constants.USERDATA, user)
            userType = SharePreferenceManager.getInstance(requireContext()).getUserLogin(Constants.USERDATA)?.get(0)!!.userType

            SharePreferenceManager.getInstance(requireContext()).save(Constants.EARNED_POINTS, user!![0].wallet)
            SharePreferenceManager.getInstance(requireContext()).save(Constants.FIRST_FREE_ORDER_COMPLETE, user!![0].firstFreeOrder)
        }
    }

    override fun onFailure(message: kotlin.String) {

    }


}