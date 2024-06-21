package com.example.thread.utils

import android.content.Context
import android.content.SharedPreferences

object SharedPref {

    private const val PREF_NAME = "user_pref"
    private const val KEY_NAME = "name"
    private const val KEY_EMAIL = "email"
    private const val KEY_BIO = "bio"
    private const val KEY_USERNAME = "userName"
    private const val KEY_IMAGE_URL = "imageUrl"

    fun storeData(name: String, email: String, bio: String, userName: String, imageUrl: String, context: Context) {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(KEY_NAME, name)
        editor.putString(KEY_EMAIL, email)
        editor.putString(KEY_BIO, bio)
        editor.putString(KEY_USERNAME, userName)
        editor.putString(KEY_IMAGE_URL, imageUrl)
        editor.apply()
    }

    fun getUserName(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(KEY_USERNAME, "")!!
    }

    fun getName(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(KEY_NAME, "")!!
    }

    fun getEmail(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(KEY_EMAIL, "")!!
    }

    fun getBio(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(KEY_BIO, "")!!
    }

    fun getImage(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(KEY_IMAGE_URL, "")!!
    }
}
