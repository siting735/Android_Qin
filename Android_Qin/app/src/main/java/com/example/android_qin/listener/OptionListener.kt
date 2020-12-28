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
import com.xuexiang.xui.widget.picker.widget.listener.OnOptionsSelectListener
import com.xuexiang.xui.widget.textview.supertextview.SuperTextView
import org.json.JSONObject
import java.lang.Exception
import java.net.URL
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class OptionListener(val activity: FragmentActivity?, val context: Context?) :
    OnOptionsSelectListener {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onOptionsSelect(
        view: View?,
        options1: Int,
        options2: Int,
        options3: Int
    ): Boolean {
        signView = view
        classInfo = TeacherActivity.classList!![options1] as JSONObject
        currentClassId = classInfo!!["classId"].toString()
        buildConfirmDialogForLaunch()
        activity?.runOnUiThread {
            LocationFragmentForTeacher.optionsPickerView?.dialog?.cancel()
            confirmDialogForLaunch?.show()
        }
        return true
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun buildConfirmDialogForLaunch() {
        if (confirmDialogForLaunch == null) {
            confirmDialogForLaunch = AlertDialog.Builder(context)
            confirmDialogForLaunch?.setTitle("提示")
            confirmDialogForLaunch?.setMessage("确认发起活动？")
            confirmDialogForLaunch?.setPositiveButton("确定") { dialog, id ->
                launchActivity()
            }
            confirmDialogForLaunch?.setNegativeButton("取消") { dialog, id ->
                {}
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun launchActivity() {
        Thread {
            buildRequestForLaunchActivity()
            try {
                LocationFragmentForTeacher.response =
                    ConnectionUtil.getDataByUrl(urlForLaunchActivity)
            } catch (e: Exception) {
                e.printStackTrace()
                ConnectionUtil.buildConnectFailDialog(context)
                activity?.runOnUiThread {
                    ConnectionUtil.connectFailDialog?.show()
                }
                Thread.currentThread().join()
            }
            dealWithResponseForLaunchActivity()
        }.start()
    }


    private fun dealWithResponseForLaunchActivity() {
        buildDataForLaunchActivity()
        if (activityState == LocationFragmentForTeacher.LAUNCH_FAIL) {
            activity?.runOnUiThread {
                Toast.makeText(context, "发布失败", Toast.LENGTH_LONG).show()
            }
        } else {
            currentActivityId = MainActivity.responseJson!!["activityId"].toString()
            activity?.runOnUiThread {
                Toast.makeText(context, "发布成功", Toast.LENGTH_LONG).show()
            }
            refreshActivity()
        }

    }

    private fun refreshActivity() {
        Thread {
            buildRequestForRefreshActivity()
            try {
                LocationFragmentForTeacher.response =
                    ConnectionUtil.getDataByUrl(urlForRefreshActivity)
            } catch (e: Exception) {
                ConnectionUtil.buildConnectFailDialog(context)
                activity?.runOnUiThread {
                    ConnectionUtil.connectFailDialog?.show()
                }
                Thread.currentThread().join()
            }
            dealWithResponseForRefreshActivity(LocationFragmentForTeacher.response)
        }.start()
    }

    private fun dealWithResponseForRefreshActivity(response: StringBuilder?) {
        buildDataForRefreshActivity()
        if (currentActivityTitle == "") {
            currentClassId = ""
            activity?.runOnUiThread {
                activityTitleTextView?.setLeftString("暂无活动")
                activityTitleTextView?.setRightIcon(R.drawable.no_activity)
            }
        } else {
            currentClassId = MainActivity.responseJson!!["classId"].toString()
            currentActivityId = MainActivity.responseJson!!["activityId"].toString()
            activity?.runOnUiThread {
                activityTitleTextView?.setLeftString(currentActivityTitle.toString())
                activityTitleTextView?.setRightIcon(R.drawable.activity_running)
            }
        }
    }

    private fun buildDataForRefreshActivity() {
        MainActivity.responseJson = JSONObject(LocationFragmentForTeacher.response.toString())
        currentActivityTitle = MainActivity.responseJson!!["activityTitle"].toString()
        if (activityTitleTextView == null) {
            activityTitleTextView = signView?.findViewById(R.id.activity_title_for_teacher)
        }
    }

    private fun buildRequestForRefreshActivity() {
        if (activityTitleTextView == null) {
            activityTitleTextView = signView?.findViewById(R.id.activity_title_for_teacher)
        }
        urlForRefreshActivity =
            URL("http://${MainActivity.ip}:8080/activity/searchActivityInProcess?teacherId=${TeacherActivity.teacherId}")
    }

    private fun buildDataForLaunchActivity() {
        MainActivity.responseJson = JSONObject(LocationFragmentForTeacher.response.toString())
        activityState = MainActivity.responseJson!!["activityState"].toString()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun buildRequestForLaunchActivity() {
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-ddHH:mm")
        newActivityTitle = current.format(formatter)
        teacherLatitude = LocationFragmentForTeacher.locationInfo["teacherLatitude"]
        teacherLongitude = LocationFragmentForTeacher.locationInfo["teacherLongitude"]
        urlForLaunchActivity =
            URL("http://${MainActivity.ip}:8080/activity/launchActivity?classId=$currentClassId&activityTitle=$newActivityTitle&teacherLongitude=$teacherLongitude&teacherLatitude=$teacherLatitude")
    }

    private var currentClassId: String? = null
    private var newActivityTitle: String? = null
    private var activityTitleTextView: SuperTextView? = null
    var teacherLatitude: String? = null
    var teacherLongitude: String? = null
    var urlForLaunchActivity: URL? = null
    var confirmDialogForLaunch: AlertDialog.Builder? = null
    var classInfo: JSONObject? = null
    var activityState: String? = null
    var currentActivityId: String? = null
    var urlForRefreshActivity: URL? = null
    var currentActivityTitle: String? = null
    var signView: View? = null
}