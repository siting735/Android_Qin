package com.example.android_qin.listener

import android.os.Bundle
import android.view.View
import androidx.navigation.NavController
import com.example.android_qin.R

class GetInClassListener(var classId: String?, var className: String?, var navController:NavController?): View.OnClickListener {
    override fun onClick(v: View?) {
        val classInfoBundle = buildBundleForClassInfo(classId, className)
        navController?.navigate(R.id.signDataForEachClass, classInfoBundle)
        pageState = 1
    }
    private fun buildBundleForClassInfo(classId: String?, className: String?): Bundle {
        val bundle = Bundle()
        bundle.putString("classId", classId)
        bundle.putString("className", className)
        return bundle
    }
    companion object{
        var pageState = 0
    }
}