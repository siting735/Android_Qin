package com.example.android_qin.util

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AlertDialog
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL

class ConnectionUtil {
    companion object {

        fun getDataByRequest(activity:Activity?, context:Context, urlForLogin:URL?) {
            try {
                getDataByUrl(urlForLogin)
            } catch (e: Exception) {
                e.printStackTrace()
                buildConnectFailDialog(context)
                activity?.runOnUiThread {
                    connectFailDialog = connectFailDialogBuilder?.create()
                    connectFailDialog?.show()
                }
                Thread.currentThread().join()
            }
        }

        fun getDataByUrl(url: URL?){
            connection = url?.openConnection() as HttpURLConnection
            connection?.requestMethod = "GET"
            inputStream = connection?.inputStream
            reader = inputStream?.bufferedReader()
            response = StringBuilder()
            while (true) {
                val line = reader?.readLine() ?: break
                response?.append(line)
            }
            reader?.close()
            connection?.disconnect()
            responseJson = JSONObject(response.toString())
        }

        fun buildConnectFailDialog(context: Context?) {
            if (connectFailDialogBuilder == null) {
                connectFailDialogBuilder = AlertDialog.Builder(context!!)
                connectFailDialogBuilder?.setTitle("提示信息")
                connectFailDialogBuilder?.setMessage("连接服务器失败")
                connectFailDialogBuilder?.setPositiveButton("确定") { dialog, id -> {} }
            }
        }

        var connection: HttpURLConnection? = null
        var inputStream: InputStream? = null
        var reader: BufferedReader? = null
        var response: StringBuilder? = null
        var responseJson: JSONObject? = null
        var connectFailDialogBuilder: AlertDialog.Builder? = null
        var connectFailDialog: AlertDialog? = null
    }
}