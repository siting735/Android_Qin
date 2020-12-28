package com.example.android_qin.util

import android.content.Context

class DpUtil {
    companion object{
        fun dip2px(context: Context?, dpValue: Float): Int {
            val scale = context?.resources?.displayMetrics?.density
            return (dpValue * scale!! + 0.5f).toInt()
        }
    }
}