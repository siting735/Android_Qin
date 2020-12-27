package com.example.android_qin.listener

import android.os.Bundle
import android.view.View
import androidx.navigation.NavController
import com.example.android_qin.R
import com.example.android_qin.util.NavUtil

class GetInClassListener(var classId: String?, var className: String?): View.OnClickListener {
    override fun onClick(v: View?) {
        val classInfoBundle = buildBundleForClassInfo(classId, className)
        NavUtil.navController?.popBackStack()
        NavUtil.navController?.navigate(R.id.signDataForEachClass, classInfoBundle)
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