package com.example.android_qin.listener

import android.app.AlertDialog
import android.content.Context
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentActivity
import com.example.android_qin.MainActivity
import com.example.android_qin.R
import com.example.android_qin.TeacherActivity
import com.example.android_qin.sign.LocationFragmentForTeacher
import com.example.android_qin.util.ConnectionUtil
import com.example.android_qin.util.MapUtil
import com.xuexiang.xui.widget.picker.widget.listener.OnOptionsSelectListener
import com.xuexiang.xui.widget.textview.supertextview.SuperTextView
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception
import java.net.URL
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class OptionListener(val activity: FragmentActivity?, val context: Context?, val signView: View?) :
    OnOptionsSelectListener {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onOptionsSelect(
        view: View?,
        options1: Int,
        options2: Int,
        options3: Int
    ): Boolean {
        buildConfirmDialogForLaunch(options1)
        activity?.runOnUiThread {
            cancelPickerDialog()
            confirmDialogForLaunch?.show()
        }
        return true
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun buildConfirmDialogForLaunch(option: Int) {
        classList = TeacherActivity.classList
        classInfo = classList?.get(option) as JSONObject
        LocationFragmentForTeacher.currentClassId = classInfo!!["classId"].toString()
        if (confirmDialogForLaunch == null) {
            confirmDialogForLaunch = AlertDialog.Builder(context)
            confirmDialogForLaunch?.setTitle("提示")
            confirmDialogForLaunch?.setMessage("确认发起活动？")
            confirmDialogForLaunch?.setPositiveButton("确定") { dialog, id ->
                launchActivity()
            }
            confirmDialogForLaunch?.setNegativeButton("取消") { dialog, id -> {} }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun launchActivity() {
        Thread {
            if (context != null) {
                buildRequestForLaunchActivity()
                ConnectionUtil.getDataByRequest(activity, context, urlForLaunchActivity)
                dealWithResponseForLaunchActivity()
            }
        }.start()
    }

    private fun dealWithResponseForLaunchActivity() {
        buildDataForLaunchActivity()
        if (activityState == LocationFragmentForTeacher.LAUNCH_FAIL) {
            activity?.runOnUiThread {
                Toast.makeText(context, "发布失败", Toast.LENGTH_LONG).show()
            }
        } else {
            updateActivityState()
            activity?.runOnUiThread {
                Toast.makeText(context, "发布成功", Toast.LENGTH_LONG).show()
            }
        }

    }

    private fun updateActivityState() {
        if (activityTitleTextView == null) {
            activityTitleTextView = signView?.findViewById(R.id.activity_title_for_teacher)
        }
        if (newActivityTitle == null) {
            return Unit
        } else {
            reFormatActivityTitle()
            activity?.runOnUiThread {
                activityTitleTextView?.setLeftString(newActivityTitle.toString())
                activityTitleTextView?.setRightIcon(R.drawable.activity_running)
            }
        }
    }

    private fun buildDataForLaunchActivity() {
        ConnectionUtil.responseJson = JSONObject(ConnectionUtil.response.toString())
        activityState = ConnectionUtil.responseJson!!["activityState"].toString()
        LocationFragmentForTeacher.currentActivityId =
            ConnectionUtil.responseJson!!["activityId"].toString()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun buildRequestForLaunchActivity() {
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-ddHH:mm")
        newActivityTitle = current.format(formatter)
        teacherLatitude = MapUtil.locationInfo["teacherLatitude"]
        teacherLongitude = MapUtil.locationInfo["teacherLongitude"]
        urlForLaunchActivity =
            URL("http://${MainActivity.ip}:8080/activity/launchActivity?classId=${LocationFragmentForTeacher.currentClassId}&activityTitle=$newActivityTitle&teacherLongitude=$teacherLongitude&teacherLatitude=$teacherLatitude")
    }

    private fun cancelPickerDialog() {
        LocationFragmentForTeacher.optionsPickerView?.dialog?.cancel()
    }

    private fun reFormatActivityTitle() {
        newActivityTitle = newActivityTitle!!.replaceRange(
            10,
            newActivityTitle!!.length,
            " " + newActivityTitle!!.removeRange(0, 10)
        )
    }

    private var newActivityTitle: String? = null
    private var activityTitleTextView: SuperTextView? = null
    var teacherLatitude: String? = null
    var teacherLongitude: String? = null
    var urlForLaunchActivity: URL? = null
    var confirmDialogForLaunch: AlertDialog.Builder? = null
    var classInfo: JSONObject? = null
    var activityState: String? = null
    var classList: JSONArray? = null

}