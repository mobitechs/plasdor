package com.plasdor.app.view.fragment

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.widget.AppCompatEditText
import androidx.fragment.app.Fragment
import com.plasdor.app.R
import com.plasdor.app.utils.showToastMsg
import com.plasdor.app.view.activity.AuthActivity

class EnterEmailFragment : Fragment() {

    lateinit var rootView: View
    lateinit var txtEmail: AppCompatEditText
    lateinit var btnSubmit: Button
    var email = ""

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
        txtEmail = rootView.findViewById(R.id.txtEmail)
        btnSubmit = rootView.findViewById(R.id.btnSubmit)

        btnSubmit.setOnClickListener {
            gotoNext()
        }

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

    private fun gotoNext() {
        email = txtEmail.text.toString()
        if (email.equals("") ) {
            requireContext().showToastMsg("Please Enter Email.")
        } else {
            (context as AuthActivity).openOTPPage(email)
        }
    }
}