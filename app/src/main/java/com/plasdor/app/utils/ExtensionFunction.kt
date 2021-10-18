package com.plasdor.app.utils

import android.app.DatePickerDialog
import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.IdRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.snackbar.Snackbar
import com.plasdor.app.R
import com.plasdor.app.callbacks.AlertDialogBtnClickedCallBack
import com.plasdor.app.callbacks.ApiResponse
import com.plasdor.app.callbacks.SpinnerItemSelectedCallback
import com.plasdor.app.model.ProductListItems
import com.plasdor.app.session.SharePreferenceManager
import com.plasdor.app.view.activity.*
import com.plasdor.app.view.fragment.admin.AdminOrderDetailsFragment
import de.hdodenhof.circleimageview.CircleImageView
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


var snackbar: Snackbar? = null
var progressBar: ProgressBar? = null


fun Context.checkLogin() {
    if (SharePreferenceManager.getInstance(this).getValueBoolean(Constants.ISLOGIN)) {
        val userDetails = SharePreferenceManager.getInstance(this)
            .getUserLogin(Constants.USERDATA)

        if (userDetails?.get(0)?.userType == Constants.ADMIN) {
            openClearActivity(AdminHomeActivity::class.java)
        } else if (userDetails?.get(0)?.userType == Constants.MERCHANT) {
            openClearActivity(MerchantHomeActivity::class.java)
        } else if (userDetails?.get(0)?.userType == Constants.USER) {
            openClearActivity(UserHomeActivity::class.java)
        } else if (userDetails?.get(0)?.userType == Constants.DELIVERY_AGENT) {
            openClearActivity(DeliveryAgentHomeActivity::class.java)
        }
//        openActivity(UserHomeActivity::class.java)
    } else {
        openActivity(AuthActivity::class.java)
    }

}

fun Context.showToastMsg(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}
fun Context.showToastMsgLong(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
}


fun showSnackBar(view: View, str: String) {
    snackbar = Snackbar
        .make(view, str, Snackbar.LENGTH_SHORT)
    snackbar!!.show()
}

fun <T> Context.openActivity(it: Class<T>, extras: Bundle.() -> Unit = {}) {
    var intent = Intent(this, it)
    intent.putExtras(Bundle().apply(extras))
    startActivity(intent)

}

fun <T> Context.openClearActivity(it: Class<T>, extras: Bundle.() -> Unit = {}) {
    var intent = Intent(this, it)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//    intent.putExtras(Bundle().apply(extras))
    startActivity(intent)

}

fun Context.openCallLauncher(mobile: String) {
    val u: Uri = Uri.parse("tel:" + mobile)
    val i = Intent(Intent.ACTION_DIAL, u)
    try {
        startActivity(i)
    } catch (s: SecurityException) {
        showToastMsg(s.message.toString())
    }
}


fun AppCompatActivity.replaceFragmentWithData(
    fragment: Fragment?,
    allowStateLoss: Boolean = false,
    @IdRes containerViewId: Int,
    flag: String,
    bundle: Bundle
) {

    val fragmentManager: FragmentManager = supportFragmentManager

    val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
    fragment?.setArguments(bundle)
    fragmentTransaction.replace(containerViewId, fragment!!, flag)
    fragmentTransaction.addToBackStack(flag);
    fragmentTransaction.commit()

}

fun AppCompatActivity.replaceFragment(
    fragment: Fragment?,
    allowStateLoss: Boolean = false,
    @IdRes containerViewId: Int,
    flag: String
) {

    val fragmentManager: FragmentManager = supportFragmentManager

    val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
    fragmentTransaction.replace(containerViewId, fragment!!, flag)
    if (!supportFragmentManager.isStateSaved) {
        fragmentTransaction.commit()
    } else if (allowStateLoss) {
        fragmentTransaction.commitAllowingStateLoss()
    }
}

fun AppCompatActivity.addFragmentWithData(
    fragment: Fragment?,
    allowStateLoss: Boolean = false,
    @IdRes containerViewId: Int,
    flag: String,
    bundle: Bundle
) {

    val fragmentManager: FragmentManager = supportFragmentManager

    val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
    fragment?.setArguments(bundle)
    fragmentTransaction.add(containerViewId, fragment!!, flag)
    fragmentTransaction.addToBackStack(flag);
    fragmentTransaction.commit()

}

fun AppCompatActivity.addFragment(
    fragment: Fragment?,
    allowStateLoss: Boolean = false,
    @IdRes containerViewId: Int,
    flag: String
) {

    val fragmentManager: FragmentManager = supportFragmentManager
    val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
    fragmentTransaction.add(containerViewId, fragment!!, flag)
    fragmentTransaction.addToBackStack(flag)
    if (!supportFragmentManager.isStateSaved) {
        fragmentTransaction.commit()
    } else if (allowStateLoss) {
        fragmentTransaction.commitAllowingStateLoss()
    }
}


fun setStatusColor(window: Window, color: Int) {

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//        val window: Window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.setStatusBarColor(color)
    }
}

fun apiPostCall(url: String, jsonObject: JSONObject, apiResponse: ApiResponse, tag: String) {
    try {
        AndroidNetworking.post(url)
            .addJSONObjectBody(jsonObject)
            .setTag(tag)
            .setPriority(Priority.MEDIUM)
            .setMaxAgeCacheControl(30, java.util.concurrent.TimeUnit.SECONDS)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    try {
                        if (response.get("Response") is JSONArray) {
                            val arr = response.getJSONArray("Response")
                            apiResponse.onSuccess(arr, tag)
                        } else if (response?.get("Response") is String) {
                            val msg = response.getString("Response")
                            //showToastMsg(msg)
                            apiResponse.onSuccess(response.getString("Response").toString(), tag)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        e.message
                        apiResponse.onFailure(response?.getString("Response").toString())
                    }
                }
                override fun onError(error: ANError) {
                    error.errorDetail
                    apiResponse.onFailure(error.errorDetail)
                }
            })
    } catch (e: java.lang.Exception) {
        apiResponse.onFailure(e.message.toString())
    }
}

fun apiGetCall(url: String, apiResponse: ApiResponse, tag: String) {
    try {

        AndroidNetworking.get(url)
            .setTag(tag)
            .setPriority(Priority.MEDIUM)
            .setMaxAgeCacheControl(30, java.util.concurrent.TimeUnit.SECONDS)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    try {
                        if (response.get("Response") is JSONArray) {
                            val arr = response.getJSONArray("Response")
                            apiResponse.onSuccess(arr, tag)
                        } else if (response?.get("Response") is String) {
                            val msg = response.getString("Response")
                            //showToastMsg(msg)
                            apiResponse.onSuccess(response.getString("Response").toString(), tag)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        e.message
                        apiResponse.onFailure(response?.getString("Response").toString())
                    }
                }

                override fun onError(error: ANError) {
                    error.errorDetail
                    apiResponse.onFailure(error.errorDetail)
                }
            })

    } catch (e: java.lang.Exception) {
        apiResponse.onFailure(e.message.toString())
    }
}

fun Context.showDatePickerDialog(v: View, txtDate: TextView) {

    var calendar = Calendar.getInstance()
    var year = calendar!!.get(Calendar.YEAR)
    var month = calendar!!.get(Calendar.MONTH)
    var day = calendar!!.get(Calendar.DAY_OF_MONTH)
    val mDatePicker = DatePickerDialog(
        v.context,
        DatePickerDialog.OnDateSetListener { datepicker, selectedyear, selectedmonth, selectedday ->
            // TODO Auto-generated method stub
            /*      Your code   to get date and time    */
            var month: Int = selectedmonth + 1
            var selectedDateForSubmission =
                selectedday.toString() + "-" + month + "-" + selectedyear.toString();
//                     toast(selectedday.toString() + "-" + (selectedmonth + 1).toString() + "-" + selectedyear.toString())

            txtDate.text = selectedDateForSubmission
        },
        year,
        month,
        day
    )
    mDatePicker.show()

}

fun Context.getDateFromPicker(v: View): String {

    var calendar = Calendar.getInstance()
    var year = calendar!!.get(Calendar.YEAR)
    var month = calendar!!.get(Calendar.MONTH)
    var day = calendar!!.get(Calendar.DAY_OF_MONTH)
    var selectedDateForSubmission = ""
    val mDatePicker = DatePickerDialog(
        v.context,
        DatePickerDialog.OnDateSetListener { datepicker, selectedyear, selectedmonth, selectedday ->
            // TODO Auto-generated method stub
            /*      Your code   to get date and time    */
            var month: Int = selectedmonth + 1
            selectedDateForSubmission =
                selectedday.toString() + "-" + month + "-" + selectedyear.toString();
//                     toast(selectedday.toString() + "-" + (selectedmonth + 1).toString() + "-" + selectedyear.toString())


        },
        year,
        month,
        day
    )
    mDatePicker.show()
    return selectedDateForSubmission
}

fun isEmailValid(email: String): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

fun ImageView.setImage(image: Any, default: Int) {
    val options: RequestOptions = RequestOptions().error(default)
    Glide.with(context).load(image)
        .apply(options)
        .into(this);

}
fun AppCompatImageView.setImage(image: Any, default: Int) {
    val options: RequestOptions = RequestOptions().error(default)
    Glide.with(context).load(image)
        .apply(options)
        .into(this);

}

fun AppCompatImageView.setImage(image: Any) {
    val options: RequestOptions = RequestOptions().error(R.drawable.img_not_available)
    Glide.with(context).load(image)
        .apply(options)
        .into(this);


//    .skipMemoryCache(true)
//        .diskCacheStrategy(DiskCacheStrategy.NONE)

}

fun CircleImageView.setImage(image: Any, default: Int) {
    val options: RequestOptions = RequestOptions().error(default)
    Glide.with(context).load(image)
        .apply(options)
        .into(this);

}

fun Context.ShareText(content: String) {
    val shareIntent = Intent()
    shareIntent.action = Intent.ACTION_SEND
    shareIntent.type = "text/plain"
    shareIntent.putExtra(Intent.EXTRA_TEXT, content);
    startActivity(Intent.createChooser(shareIntent, "Share via"))
}

fun getRandomNumberString(): String {
    val rnd = Random()
    val number = rnd.nextInt(999999)
    return String.format("%06d", number)
}


fun hideKeyboard(view: View, activity: Context) {
    val `in` = activity.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager?
    `in`!!.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
}

fun parseDateToddMMyyyy(time: String?): String? {
    val inputPattern = "yyyy-MM-dd HH:mm:ss"
    val outputPattern = "dd-MMM-yyyy h:mm a"
    val inputFormat = SimpleDateFormat(inputPattern)
    val outputFormat = SimpleDateFormat(outputPattern)
    var date: Date? = null
    var str: String? = null
    try {
        date = inputFormat.parse(time)
        str = outputFormat.format(date)
    } catch (e: ParseException) {
        e.printStackTrace()
    }
    return str
}


fun Context.getSpinnerSelectedValue(
    spinnerValueArray: Array<String>,
    spinner: Spinner,
    spinnerFor: String,
    event: SpinnerItemSelectedCallback
) {


    val adapter =
        ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, spinnerValueArray)
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    spinner.setAdapter(adapter)

    spinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {

        override fun onNothingSelected(p0: AdapterView<*>?) {
//            event.onNothingSelected()
        }

        override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

            if (p2 > 0) {

                event.onItemSelected(p2, spinnerFor)
                showToastMsg(p2.toString() + " " + spinnerValueArray[p2])
            }
        }

    })
}


fun Context.showAlertDialog(
    title: String,
    description: String,
    positiveBtnText: String,
    negativeBtnText: String,
    alertDialogBtnClickedCallBack: AlertDialogBtnClickedCallBack
) {
    val builder = AlertDialog.Builder(this)
    builder.setTitle(title)
    builder.setMessage(description)
    builder.setIcon(android.R.drawable.ic_dialog_alert)
    builder.setPositiveButton(positiveBtnText) { dialog, which ->
        alertDialogBtnClickedCallBack.positiveBtnClicked()
    }

    builder.setNegativeButton(negativeBtnText) { dialog, which ->
        alertDialogBtnClickedCallBack.negativeBtnClicked()
        dialog.dismiss()
    }

    builder.show()
}


fun Context.addToCart(item: ProductListItems) {
    var cartListItems = ArrayList<ProductListItems>()
    var cartListItems2 = ArrayList<ProductListItems>()


    if (SharePreferenceManager.getInstance(this).getCartListItems(Constants.CartList) == null) {
        cartListItems.add(item)
        cartListItems2.add(item)
    } else {
        cartListItems = SharePreferenceManager.getInstance(this)
            .getCartListItems(Constants.CartList) as ArrayList<ProductListItems>
        cartListItems2 = SharePreferenceManager.getInstance(this)
            .getCartListItems(Constants.CartList2) as ArrayList<ProductListItems>
        if (!cartListItems.contains(item)) {
            cartListItems.add(item)
            cartListItems2.add(item)
        }
    }

    (this as UserHomeActivity)!!.updateCartCount(cartListItems.size)
    SharePreferenceManager.getInstance(this).saveCartListItems(Constants.CartList, cartListItems)
    SharePreferenceManager.getInstance(this).saveCartListItems(Constants.CartList2, cartListItems2)
}

fun Context.removeToCart(item: ProductListItems) {

    var cartListItems = SharePreferenceManager.getInstance(this)
        .getCartListItems(Constants.CartList) as ArrayList<ProductListItems>
    var cartListItems2 = SharePreferenceManager.getInstance(this)
        .getCartListItems(Constants.CartList2) as ArrayList<ProductListItems>

    if (cartListItems.contains(item)) {

        for (a in 0..cartListItems2.size - 1) {
            var name = cartListItems[a].productName
            if (name.equals(item.productName)) {
                cartListItems2.removeAt(a)
                break
            }
        }
        cartListItems.remove(item)

    }

    (this as UserHomeActivity)!!.updateCartCount(cartListItems.size)
    SharePreferenceManager.getInstance(this).saveCartListItems(Constants.CartList, cartListItems)
    SharePreferenceManager.getInstance(this).saveCartListItems(Constants.CartList2, cartListItems2)

}


fun getLocationFromAddress(context: Context?, strAddress: String?): LatLng? {
    val coder = Geocoder(context)
    val address: List<Address>?
    var p1: LatLng? = null
    try {
        // May throw an IOException
        address = coder.getFromLocationName(strAddress, 5)
        if (address == null) {
            return null
        }
        val location: Address = address[0]
        p1 = LatLng(location.getLatitude(), location.getLongitude())
    } catch (ex: IOException) {
        ex.printStackTrace()
    }
    return p1
}

fun getTodaysDate(): String {
    val simpleDateFormat= SimpleDateFormat("dd-MM-yyyy")
    var todaysDate = simpleDateFormat.format(Date())
    return todaysDate
}
fun getTodaysDateTime(): String {
    val simpleDateFormat= SimpleDateFormat("dd-MM-yyyy hh:mm:ss aa")
    var todaysDate = simpleDateFormat.format(Date())
    return todaysDate
}