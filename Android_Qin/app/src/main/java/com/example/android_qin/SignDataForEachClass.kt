package com.example.android_qin

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
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
 * Use the [SignDataForEachClass.newInstance] factory method to
 * create an instance of this fragment.
 */
class SignDataForEachClass : Fragment() {
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        updateClassName()
        getStudentsSignData()
    }
    private fun updateClassName(){
        val className = arguments?.get("className").toString()
        val classNameView = view?.findViewById<SuperTextView>(R.id.class_name)
        activity?.runOnUiThread {
            classNameView?.setLeftString(className)
        }
    }
    private fun getStudentsSignData() {
        val classId = arguments?.get("classId").toString()

        val ip =getString(R.string.ip)
        Thread{
            val url = "http://$ip:8080/teacher/signRitoOfStudents?classId=$classId"
            val urlForGetSignData = URL(url)
            var connection: HttpURLConnection? = null
            var response: StringBuilder? = null
            try {
                connection = urlForGetSignData.openConnection() as HttpURLConnection
                connection?.requestMethod = "GET"
                response = ConnectionUtil.getDataFromConnection(connection)
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
        Log.i("classId in each class", classId)
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
    private fun dealWithResponse(response:StringBuilder?){
        val jsonString = response.toString()
        val responseJson = JSONObject(jsonString)
        val signDataList = responseJson["signRitoOfStudents"] as JSONArray
        for(index in 0 until signDataList.length()){
            addSignDataToLayout(signDataList[index] as JSONObject)
        }
    }
    private fun addSignDataToLayout(signData: JSONObject?){
        val signDataListLayout= view?.findViewById<LinearLayout>(R.id.students_sign_data_list)
        val signDataView = SuperTextView(context)
        val studentId = signData?.get("studentId")?.toString()
        val studentName = signData?.get("studentName")?.toString()
        val signRito = signData?.get("signRito")?.toString()
        signDataView.setLeftBottomString(studentId)
        signDataView.setLeftString(studentName)
        signDataView.setRightString("出勤率：")
        signDataView.setRightBottomString("$signRito%")
        var layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dip2px(100f))
        layoutParams.setMargins(dip2px(10f),dip2px(10f),dip2px(10f),0)
        signDataView.layoutParams = layoutParams
        activity?.runOnUiThread {
            signDataListLayout?.addView(signDataView)
        }
    }

    private fun dip2px(dpValue:Float): Int {
        val scale = context?.resources?.displayMetrics?.density;
        return (dpValue * scale!! + 0.5f).toInt()
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
        return inflater.inflate(R.layout.fragment_sign_data_for_each_class, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SignDataForEachClass.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SignDataForEachClass().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}