package com.example.android_qin.util

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences

class AccountInfoUtil {


    companion object{
        fun getSharePreference(context: Context){
            sharePreference = context.getSharedPreferences("account_info",
                Activity.MODE_PRIVATE
            )
            sharePreferenceEditor = AccountInfoUtil.sharePreference?.edit()
        }

        fun clearAccountInfo(){
            sharePreferenceEditor?.putString("ip", null)
            sharePreferenceEditor?.commit()
        }

        var sharePreference: SharedPreferences? = null
        var sharePreferenceEditor: SharedPreferences.Editor? = null
    }
}