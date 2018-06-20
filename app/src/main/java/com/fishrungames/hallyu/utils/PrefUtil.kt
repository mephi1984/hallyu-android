package com.fishrungames.hallyu.utils

import android.annotation.SuppressLint
import android.content.Context
import android.preference.PreferenceManager

object PrefUtil {

    private val APP_PREFERENCES_USER_FIRST_NAME: String = "userFirstName"
    private val APP_PREFERENCES_USER_LAST_NAME: String = "userLastName"
    private val APP_PREFERENCES_USER_PHOTO_URL: String = "userPhotoUrl"
    private val APP_PREFERENCES_USER_PHOTO_NAME: String = "userPhotoName"
    private val APP_PREFERENCES_USER_TOKEN: String = "userToken"

    fun getUserFirstName(context: Context): String {
        return getStringFromPrefs(context, APP_PREFERENCES_USER_FIRST_NAME, "")
    }

    fun setUserFirstName(context: Context, firstName: String) {
        saveStringToPrefs(context, APP_PREFERENCES_USER_FIRST_NAME, firstName)
    }

    fun getUserLastName(context: Context): String {
        return getStringFromPrefs(context, APP_PREFERENCES_USER_LAST_NAME, "")
    }

    fun setUserLastName(context: Context, lastName: String) {
        saveStringToPrefs(context, APP_PREFERENCES_USER_LAST_NAME, lastName)
    }

    fun getUserToken(context: Context): String {
        return getStringFromPrefs(context, APP_PREFERENCES_USER_TOKEN, "")
    }

    fun setUserToken(context: Context, token: String) {
        saveStringToPrefs(context, APP_PREFERENCES_USER_TOKEN, token)
    }

    fun getUserPhotoUrl(context: Context): String {
        return getStringFromPrefs(context, APP_PREFERENCES_USER_PHOTO_URL, "")
    }

    fun setUserPhotoUrl(context: Context, photoUrl: String) {
        saveStringToPrefs(context, APP_PREFERENCES_USER_PHOTO_URL, photoUrl)
    }

    fun getUserPhotoName(context: Context): String {
        return getStringFromPrefs(context, APP_PREFERENCES_USER_PHOTO_NAME, "")
    }

    fun setUserPhotoName(context: Context, photoName: String) {
        saveStringToPrefs(context, APP_PREFERENCES_USER_PHOTO_NAME, photoName)
    }

    @SuppressLint("ApplySharedPref")
    private fun saveStringToPrefs(context: Context, key: String, value: String) {
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = sharedPrefs.edit()
        editor.putString(key, value)
        editor.commit()
    }

    private fun getStringFromPrefs(context: Context, key: String, defaultValue: String): String {
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context)
        return try {
            sharedPrefs.getString(key, defaultValue)
        } catch (e: Exception) {
            e.printStackTrace()
            defaultValue
        }
    }

    private fun saveStringArrayToPrefs(context: Context, key: String, value: ArrayList<String>) {
        val stringFromArray = value.joinToString(",")
        saveStringToPrefs(context, key, stringFromArray)
    }

    private fun getStringArrayFromPrefs(context: Context, key: String, defaultValue: String): ArrayList<String> {
        val stringFromPrefs = getStringFromPrefs(context, key, defaultValue)
        return stringFromPrefs.split(",") as ArrayList<String>
    }

    @SuppressLint("ApplySharedPref")
    private fun saveIntToPrefs(context: Context, key: String, value: Int){
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = sharedPrefs.edit()
        editor.putInt(key, value)
        editor.commit()
    }

    private fun getIntFromPrefs(context: Context, key: String, defaultValue: Int): Int {
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context)
        return try {
            sharedPrefs.getInt(key, defaultValue)
        } catch (e: Exception) {
            e.printStackTrace()
            defaultValue
        }
    }

    @SuppressLint("ApplySharedPref")
    private fun saveBooleanToPrefs(context: Context, key: String, value: Boolean){
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = sharedPrefs.edit()
        editor.putBoolean(key, value)
        editor.commit()
    }

    private fun getBooleanFromPrefs(context: Context, key: String, defaultValue: Boolean): Boolean {
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context)
        return try {
            sharedPrefs.getBoolean(key, defaultValue)
        } catch (e: Exception) {
            e.printStackTrace()
            defaultValue
        }
    }



}