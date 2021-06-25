package com.plasdor.app.session

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.plasdor.app.model.ProductListItems
import com.plasdor.app.model.UserModel
import com.plasdor.app.utils.Constants
import com.plasdor.app.utils.openActivity
import com.plasdor.app.view.activity.AuthActivity
import java.lang.reflect.Type


class SharePreferenceManager {
    var sharedPref: SharedPreferences? = null
    private val PREFS_NAME = Constants.PROJECT_NAME


    private constructor(context: Context) {
        sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    }

    companion object {
        private var instance: SharePreferenceManager? = null
        fun getInstance(context: Context): SharePreferenceManager {
            if (instance == null) {
                instance = SharePreferenceManager(context)
            }

            return instance!!
        }
    }


    fun save(KEY_NAME: String, text: String) {

        val editor: SharedPreferences.Editor = sharedPref!!.edit()

        editor.putString(KEY_NAME, text)

        editor.commit()
    }

    fun save(KEY_NAME: String, value: Int) {
        val editor: SharedPreferences.Editor = sharedPref!!.edit()

        editor.putInt(KEY_NAME, value)

        editor.commit()
    }

    fun save(KEY_NAME: String, status: Boolean) {

        val editor: SharedPreferences.Editor = sharedPref!!.edit()

        editor.putBoolean(KEY_NAME, status)

        editor.commit()
    }


    fun getValueString(KEY_NAME: String): String? {
        return sharedPref!!.getString(KEY_NAME, null)
    }

    /* fun save(KEY_NAME: String, profileDetailsResponse: ProfileDetailsResponse?){
         val editor: SharedPreferences.Editor = sharedPref!!.edit()
         val gson = Gson()
         val json = gson.toJson(profileDetailsResponse)
         editor.putString(KEY_NAME, json)
         editor.commit()
     }

     fun getProfileData(KEY_NAME: String):ProfileDetailsResponse{
         val gson = Gson()
         val  value =sharedPref!!.getString(KEY_NAME, null)
         return gson.fromJson(value, ProfileDetailsResponse::class.java)
     }
 */
    fun getValueInt(KEY_NAME: String): Int {

        return sharedPref!!.getInt(KEY_NAME, 0)
    }

    fun getValueBoolean(KEY_NAME: String): Boolean {

        return sharedPref!!.getBoolean(KEY_NAME, false)

    }

    fun clearSharedPreference(context: Context) {

        val editor: SharedPreferences.Editor = sharedPref!!.edit()

        //sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        editor.clear()
        removeValue(Constants.USERDATA)
        removeValue(Constants.ISLOGIN)
        clearCart()
        // editor.commit()

        context.openActivity(AuthActivity::class.java)
    }

    fun clearCart() {
        removeValue(Constants.finalOrderMsg)
        removeValue(Constants.totalDiscountedAmount)
        removeValue(Constants.delCharges)
        removeValue(Constants.totalAmount)
        removeValue(Constants.CartList)
    }

    fun removeValue(KEY_NAME: String) {
        val editor: SharedPreferences.Editor = sharedPref!!.edit()
        editor.remove(KEY_NAME)
        editor.commit()
    }

    fun saveUserLogin(key: String?, list: List<UserModel>?) {
        val editor: SharedPreferences.Editor = sharedPref!!.edit()
        editor.commit()
        val gson = Gson()
        val json = gson.toJson(list)
        editor.putString(key, json)
        editor.commit() // This line is IMPORTANT !!!
    }

    fun getUserLogin(key: String?): List<UserModel>? {
        val gson = Gson()
        val json = sharedPref?.getString(key, null)
        val type: Type = object : TypeToken<List<UserModel>?>() {}.type
        return gson.fromJson(json, type)
    }


    fun saveCartListItems(key: String?, list: List<ProductListItems>?) {
        val editor: SharedPreferences.Editor = sharedPref!!.edit()
        editor.commit()
        val gson = Gson()
        val json = gson.toJson(list)
        editor.putString(key, json)
        editor.commit() // This line is IMPORTANT !!!
    }

    fun getCartListItems(key: String?): List<ProductListItems>? {
        val gson = Gson()
        val json = sharedPref?.getString(key, null)
        val type: Type = object : TypeToken<List<ProductListItems>?>() {}.type
        return gson.fromJson(json, type)
    }

}