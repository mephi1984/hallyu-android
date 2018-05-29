package com.fishrungames.hallyu.utils

import android.annotation.SuppressLint
import android.content.Context
import android.preference.PreferenceManager

object PrefUtil {

    private val APP_PREFERENCES_POST_CATEGORIES: String = "postCategories"

    fun getPostCategories(context: Context): ArrayList<String> {
        return getStringArrayFromPrefs(context, APP_PREFERENCES_POST_CATEGORIES, "")
    }
    fun savePostCategories(context: Context, categories: ArrayList<String>) {
        saveStringArrayToPrefs(context, APP_PREFERENCES_POST_CATEGORIES, categories)
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