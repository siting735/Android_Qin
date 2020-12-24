package com.example.android_qin

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.core.view.marginLeft
import com.xuexiang.xui.widget.textview.supertextview.SuperTextView
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SignDataForStudent.newInstance] factory method to
 * create an instance of this fragment.
 */
class SignDataForStudent : Fragment() {
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        getSignDataForStudent()
        configSwipeRefresh()
    }
    private fun getSignDataForStudent(){
        val ip =getString(R.string.ip)
        val studentId = arguments?.get("studentId").toString()
        Thread{
            val url = "http://$ip:8080/student/studentSignMessage?studentId=$studentId"
            val urlForGetSignData = URL(url)
            var connection: HttpURLConnection? = null
            var response: StringBuilder? = null
            try {
                connection = urlForGetSignData.openConnection() as HttpURLConnection
                connection?.requestMethod = "GET"
                response = getDataFromConnection(connection)
                connection?.disconnect()
            } catch (e: Exception) {
                Log.e("error in sign",e.toString())
                var loginFailDialog = buildConnectFailDialog()
                activity?.runOnUiThread {
                    loginFailDialog.show()
                }
                Thread.currentThread().join()
            }
            dealWithResponse(response)
        }.start()

    }
    private fun dealWithResponse(response: StringBuilder?){
        val jsonString = response.toString()
        val responseJson = JSONObject(jsonString)
        val signDataList = responseJson["activityInfo"] as JSONArray
        updateSignRiro(responseJson)
        for(index in 0 until signDataList.length()){
            addSignDataToLayout(signDataList[index] as JSONObject)
        }
    }
    private fun updateSignRiro(responseJson: JSONObject){
        val signRito = responseJson["signRito"].toString()
        val signRitoTextView = view?.findViewById<SuperTextView>(R.id.sign_rito)
        activity?.runOnUiThread {
            signRitoTextView?.setCenterBottomString("$signRito%")
        }
    }
    private fun addSignDataToLayout(signData: JSONObject?){
        val signDataListLayout= view?.findViewById<LinearLayout>(R.id.sign_data_list)
        val signDataView = SuperTextView(context)
        val activityTitle = signData?.get("activityTitle")?.toString()
        val signState = signData?.get("signState")?.toString()
        signDataView.setLeftBottomString(activityTitle)
        when(signState){
            "1" -> signDataView.setRightString("已打卡")
            "2" -> signDataView.setRightString("未打卡")
        }
        var layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dip2px(100f))
        layoutParams.setMargins(dip2px(10f),dip2px(10f),dip2px(10f),0)
        signDataView.layoutParams = layoutParams
        activity?.runOnUiThread {
            signDataListLayout?.addView(signDataView)
        }
    }
    private fun buildConnectFailDialog(): AlertDialog.Builder {
        val loginFailDialog = AlertDialog.Builder(this.requireContext())
        loginFailDialog.setTitle("提示信息")
        loginFailDialog.setMessage("连接服务器失败")
        loginFailDialog.setPositiveButton("确定") {
                dialog, id ->{}
        }
        return loginFailDialog
    }
    private fun getDataFromConnection(connection: HttpURLConnection): StringBuilder {
        val inputStream = connection?.inputStream
        val reader = inputStream?.bufferedReader()
        val response = StringBuilder()
        while (true) {
            val line = reader?.readLine() ?: break
            response.append(line)
        }
        reader?.close()
        return response
    }
    private fun configSwipeRefresh(){
        val swipe= view?.findViewById<androidx.swiperefreshlayout.widget.SwipeRefreshLayout>(R.id.sign_data_swipe)
        swipe?.setOnRefreshListener {
            removeOriginSignDatas()
            getSignDataForStudent()
            swipe.isRefreshing=false
        }
    }
    private fun removeOriginSignDatas(){
        val signDataList= view?.findViewById<LinearLayout>(R.id.sign_data_list)
        activity?.runOnUiThread {
            signDataList?.removeAllViews()
        }
    }
    private fun dip2px(dpValue:Float): Int {
        val scale = context?.resources?.displayMetrics?.density;
        return (dpValue * scale!! + 0.5f).toInt()
    }
    private fun px2dip(pxValue:Float):Int{
        val scale = context?.resources?.displayMetrics?.density;
        return (pxValue / scale!! + 0.5f).toInt()
    }
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_data_for_student, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SignDataForStudent.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SignDataForStudent().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}