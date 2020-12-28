package com.example.android_qin.util

import android.content.Context
import android.view.ViewGroup
import android.widget.LinearLayout

class LayoutUtil {
    companion object{
        var layoutParamsForInfoUnit: LinearLayout.LayoutParams? = null
        fun buildLayoutParamsForInfoUnit(context: Context?){
            layoutParamsForInfoUnit =
                LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DpUtil.dip2px(context,100f))
            layoutParamsForInfoUnit?.setMargins(DpUtil.dip2px(context, 10f), DpUtil.dip2px(context,10f), DpUtil.dip2px(context,10f), 0)
        }
    }
}