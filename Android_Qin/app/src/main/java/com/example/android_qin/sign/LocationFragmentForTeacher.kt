package com.example.android_qin.sign


import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.ArrayMap
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.view.marginTop
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import com.amap.api.maps2d.AMap
import com.amap.api.maps2d.MapView
import com.amap.api.maps2d.model.MyLocationStyle
import com.example.android_qin.MainActivity
import com.example.android_qin.R
import com.example.android_qin.TeacherActivity
import com.example.android_qin.listener.OptionListener
import com.example.android_qin.util.ConnectionUtil
import com.example.android_qin.util.MapUtil
import com.example.android_qin.util.NavUtil
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.xuexiang.xui.widget.picker.widget.OptionsPickerView
import com.xuexiang.xui.widget.picker.widget.builder.OptionsPickerBuilder
import com.xuexiang.xui.widget.picker.widget.listener.OnOptionsSelectListener
import com.xuexiang.xui.widget.textview.supertextview.SuperTextView
import org.json.JSONObject
import java.lang.Exception
import java.net.URL
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class LocationFragmentForTeacher : Fragment() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        MapUtil.buildMap(view)
        MapUtil.mMapView?.onCreate(savedInstanceState)
        configSignBtn()
        configSwipeRefresh()
        configManualSign()
        refreshActivity()
    }


    private fun configManualSign() {
        val manualSignTextVIew = view?.findViewById<TextView>(R.id.manual_sign)
        manualSignTextVIew?.setOnClickListener {
            if (currentActivityId == null) {
                activity?.runOnUiThread {
                    Toast.makeText(requireContext(), "暂无活动", Toast.LENGTH_LONG).show()
                }
            } else {
                buildManualDialog()
                manualSignDialog?.show()
            }
        }
    }

    private fun configSwipeRefresh() {
        val swipe =
            view?.findViewById<SwipeRefreshLayout>(R.id.location_swipe_for_teacher)
        swipe?.setOnRefreshListener {
            MapUtil.mLocationClient!!.startLocation()
            refreshActivity()
            swipe.isRefreshing = false
        }
    }

    private fun refreshActivity() {
        Thread {
            buildRequestForRefreshActivity()
            try {
                ConnectionUtil.getDataByUrl(urlForRefreshActivity)
            } catch (e: Exception) {
                ConnectionUtil.buildConnectFailDialog(requireContext())
                activity?.runOnUiThread {
                    ConnectionUtil.connectFailDialog?.show()
                }
                Thread.currentThread().join()
            }
            dealWithResponseForRefreshActivity()
        }.start()
    }

    private fun dealWithResponseForRefreshActivity() {
        buildDataForRefreshActivity()
        if (currentActivityTitle == "") {
            currentClassId = ""
            activity?.runOnUiThread {
                activityTitleTextView?.setLeftString("暂无活动")
                activityTitleTextView?.setRightIcon(R.drawable.no_activity)
            }
        } else {
            currentClassId = ConnectionUtil.responseJson!!["classId"].toString()
            currentActivityId = ConnectionUtil.responseJson!!["activityId"].toString()
            activity?.runOnUiThread {
                activityTitleTextView?.setLeftString(currentActivityTitle.toString())
                activityTitleTextView?.setRightIcon(R.drawable.activity_running)
            }
        }
    }

    private fun manualSign() {
        Thread {
            buildRequestForManualSign()
            try {
                ConnectionUtil.getDataByUrl(urlForManualSign)
            } catch (e: Exception) {
                ConnectionUtil.buildConnectFailDialog(requireContext())
                activity?.runOnUiThread {
                    ConnectionUtil.connectFailDialog?.show()
                }
                Thread.currentThread().join()
            }
            dealWithResponseForManualSign()
        }.start()

    }


    private fun buildRequestForManualSign() {
        urlForManualSign =
            URL("http://${MainActivity.ip}:8080/sign/manualSign?activityId=$currentActivityId&studentId=$studentId")
    }

    private fun buildDataForManualSign() {
        signState = ConnectionUtil.responseJson!!["signState"].toString()
    }

    private fun dealWithResponseForManualSign() {
        buildDataForManualSign()
        if (signState == SIGN_FAIL) {
            activity?.runOnUiThread {
                Toast.makeText(requireContext(), "签到失败", Toast.LENGTH_LONG).show()
            }
        } else {
            activity?.runOnUiThread {
                Toast.makeText(requireContext(), "签到成功", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun buildManualDialog() {
        if (manualSignDialog == null) {
            manualSignDialogBuilder = AlertDialog.Builder(context)
            manualSignDialogBuilder!!.setTitle("手动签到")
            studentIdLayout = LinearLayout.inflate(
                requireContext(),
                R.layout.student_id_input,
                null
            ) as LinearLayout
            studentIdEditText = studentIdLayout?.findViewById<EditText>(R.id.student_id_edit_text)
            manualSignDialogBuilder!!.setView(studentIdLayout)
            manualSignDialogBuilder?.setPositiveButton("确定") { dialog, id ->
                studentId = studentIdEditText?.text.toString()
                manualSign()
            }
            manualSignDialogBuilder?.setNegativeButton("取消") { dialog, id ->
                {}
            }
            manualSignDialog = manualSignDialogBuilder!!.create()
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun configSignBtn() {
        val signBtn = view?.findViewById<Button>(R.id.sign_btn_for_teacher)
        NavUtil.buildNavHost(activity?.supportFragmentManager)
        signBtn?.setOnClickListener {
            if (currentClassId == "") {
                selectClassForLaunch()
            } else {
                buildConfirmDialogForEndActivity()
                confirmDialogForEnd?.show()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun endActivity() {
        Thread {
            buildRequestForEndActivity()
            try {
                ConnectionUtil.getDataByUrl(urlForEndActivity)
            } catch (e: Exception) {
                ConnectionUtil.buildConnectFailDialog(requireContext())
                activity?.runOnUiThread {
                    ConnectionUtil.connectFailDialog?.show()
                }
                Thread.currentThread().join()
            }
            dealWithResponseForEndActivity()
        }.start()
    }


    private fun dealWithResponseForEndActivity() {
        buildDataForEndActivity()
        NavUtil.buildNavHost(activity?.supportFragmentManager)
        if (activityState == END_FAIL) {
            activity?.runOnUiThread {
                Toast.makeText(requireContext(), "结束活动失败", Toast.LENGTH_LONG).show()
            }
        } else {
            currentActivityId = null
            activity?.runOnUiThread {
                NavUtil.navController?.navigate(R.id.signStateForTeacher)
            }
        }

    }

    private fun buildRequestForEndActivity() {
        urlForEndActivity =
            URL("http://${MainActivity.ip}:8080/sign/changeSignToStop?classId=$currentClassId&activityId=$currentActivityId")
    }

    private fun configPickerBuilder() {
        optionsPickerBuilder =
            OptionsPickerBuilder(context, OptionListener(requireActivity(), requireContext(), view))
        optionsPickerBuilder!!.setTitleText("选择班级")
        optionsPickerBuilder!!.setDividerColor(Color.BLACK)
        optionsPickerBuilder!!.setTextColorCenter(Color.BLACK)
        optionsPickerBuilder!!.setContentTextSize(20)
        optionsPickerBuilder!!.isDialog(true)
    }

    private fun selectClassForLaunch() {
        configPickerBuilder()
        optionsPickerView = optionsPickerBuilder!!.build()
        addClassListToPicker()
        activity?.runOnUiThread {
            optionsPickerView!!.show()
        }
    }

    private fun addClassListToPicker() {
        val classList = ArrayList<String>()
        val classListJsonArray = TeacherActivity.classList
        val classListLength = TeacherActivity.classList!!.length()
        if (classListLength == 0) {
            activity?.runOnUiThread {
                Toast.makeText(context, "暂无班级", Toast.LENGTH_LONG).show()
            }
            return Unit
        }
        for (index in 0 until classListLength) {
            tempClassInfo = classListJsonArray!![index] as JSONObject
            classList.add(tempClassInfo!!["className"].toString())
        }
        optionsPickerView?.setPicker(classList)
    }

    @RequiresApi(Build.VERSION_CODES.O)

    private fun buildConfirmDialogForEndActivity() {
        if (confirmDialogForEnd == null) {
            confirmDialogForEnd = AlertDialog.Builder(context)
            confirmDialogForEnd?.setTitle("提示")
            confirmDialogForEnd?.setMessage("确认结束当前活动？")
            confirmDialogForEnd?.setPositiveButton("确定") { dialog, id ->
                endActivity()
            }
            confirmDialogForEnd?.setNegativeButton("取消") { dialog, id ->
                {}
            }
        }
    }

    private fun buildRequestForRefreshActivity() {
        if (activityTitleTextView == null) {
            activityTitleTextView = view?.findViewById(R.id.activity_title_for_teacher)
        }
        urlForRefreshActivity =
            URL("http://${MainActivity.ip}:8080/activity/searchActivityInProcess?teacherId=${TeacherActivity.teacherId}")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_location_for_teacher, container, false)
    }

    private fun buildDataForRefreshActivity() {
        currentActivityTitle = ConnectionUtil.responseJson!!["activityTitle"].toString()
        Log.i("currentActivityTitle", currentActivityTitle.toString())
        if (activityTitleTextView == null) {
            activityTitleTextView = view?.findViewById(R.id.activity_title_for_teacher)
        }
    }

    override fun onStart() {
        super.onStart()
        MapUtil.getLocationInfo(requireContext(),activity)
    }
    private fun buildDataForEndActivity() {
        activityState = ConnectionUtil.responseJson!!["activityState"].toString()
    }

    var activityState: String? = null
    var currentActivityTitle: String? = null
    var urlForRefreshActivity: URL? = null
    var confirmDialogForEnd: AlertDialog.Builder? = null
    var urlForEndActivity: URL? = null
    var optionsPickerBuilder: OptionsPickerBuilder? = null
    var tempClassInfo: JSONObject? = null
    var activityTitleTextView: SuperTextView? = null
    var urlForManualSign: URL? = null
    var studentId: String? = null
    private var signState: String? = null
    var manualSignDialogBuilder: AlertDialog.Builder? = null
    private var manualSignDialog: AlertDialog? = null
    var studentIdLayout: LinearLayout? = null
    var studentIdEditText: EditText? = null

    companion object {
        const val LAUNCH_FAIL = "0"
        const val END_FAIL = "0"
        const val SIGN_FAIL = "0"
        var optionsPickerView: OptionsPickerView<String>? = null
        var currentClassId: String? = null
        var currentActivityId: String? = null

    }
}