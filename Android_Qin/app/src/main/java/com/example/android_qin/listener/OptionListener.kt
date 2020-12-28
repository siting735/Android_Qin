package com.example.android_qin.listener

import android.util.Log
import android.view.View
import androidx.fragment.app.FragmentActivity
import com.example.android_qin.TeacherActivity
import com.example.android_qin.sign.LocationFragmentForTeacher
import com.xuexiang.xui.widget.picker.widget.listener.OnOptionsSelectListener
import org.json.JSONObject

class OptionListener(activity: FragmentActivity?) :OnOptionsSelectListener{
    val activity = activity
    override fun onOptionsSelect(
        view: View?,
        options1: Int,
        options2: Int,
        options3: Int
    ): Boolean {
        classInfo = TeacherActivity.classList!![options1] as JSONObject
        TeacherActivity.currentClassId = classInfo!!["classId"].toString()
        activity?.runOnUiThread {
            LocationFragmentForTeacher.optionsPickerView?.dialog?.cancel()
        }
        return true
    }
    var classInfo: JSONObject? = null
}