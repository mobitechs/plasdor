package com.plasdor.app.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.fragment.app.Fragment
import com.plasdor.app.R
import com.plasdor.app.utils.DatePickerHelper
import com.plasdor.app.utils.showDatePickerDialog
import com.plasdor.app.utils.showToastMsg
import com.plasdor.app.view.activity.AuthActivity
import java.util.*

class AddDobFragment : Fragment() {
    lateinit var rootView: View

    lateinit var datePicker: DatePickerHelper
    lateinit var txtDob: AppCompatEditText
    lateinit var btnSignUp: AppCompatButton
    var dd = ""
    var mm = ""
    var yyyy = ""
    var dob = ""
    var age = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        rootView = inflater.inflate(R.layout.fragment_auth_add_dob, container, false)
        initView()
        return rootView
    }

    private fun initView() {
        txtDob = rootView.findViewById(R.id.txtDob)
        btnSignUp = rootView.findViewById(R.id.btnSignUp)
        btnSignUp.setOnClickListener {
            if(txtDob.text.toString().equals("")){
                requireActivity().showToastMsg("Please select your DOB")
            }else{
                val bundle = Bundle()
                bundle.putString("userType", arguments?.getString("userType").toString())
                bundle.putString("name", arguments?.getString("name").toString())
                bundle.putString("mobile", arguments?.getString("mobile").toString())
                bundle.putString("email", arguments?.getString("email").toString())
                bundle.putString("password", arguments?.getString("password").toString())
                bundle.putString("dob", txtDob.text.toString())
                (context as AuthActivity).openFullAddress(bundle)
            }

        }

        txtDob.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                when (event?.action) {
                    MotionEvent.ACTION_DOWN -> requireActivity().showDatePickerDialog(v!!,txtDob)
                    //Do Something
                }
                return v?.onTouchEvent(event) ?: true
            }
        })
    }

    private fun showDatePickerDialog2() {
        val cal = Calendar.getInstance()
        val d = cal.get(Calendar.DAY_OF_MONTH)
        val m = cal.get(Calendar.MONTH)
        val y = cal.get(Calendar.YEAR)
        datePicker.showDialog(d, m, y, object : DatePickerHelper.Callback {

            override fun onDateSelected(dayofMonth: Int, month: Int, year: Int) {
                val dayStr = if (dayofMonth < 10) "0${dayofMonth}" else "${dayofMonth}"
                val mon = month + 1
                val monthStr = if (mon < 10) "0${mon}" else "${mon}"
//                age =  getAge(year,mon,dayofMonth).toInt()
                txtDob.setText("${dayStr}-${monthStr}-${year}".toString())

            }
        })
        datePicker.setMaxDate(Calendar.getInstance().timeInMillis)
    }

}