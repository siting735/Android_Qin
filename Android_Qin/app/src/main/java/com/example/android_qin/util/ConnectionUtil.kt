package com.example.android_qin.util

import android.content.Context
import androidx.appcompat.app.AlertDialog
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class ConnectionUtil {
    companion object {
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
            if (connectFailDialog == null) {
                connectFailDialog = AlertDialog.Builder(context!!)
                connectFailDialog?.setTitle("提示信息")
                connectFailDialog?.setMessage("连接服务器失败")
                connectFailDialog?.setPositiveButton("确定") { dialog, id ->
                    {}
                }
            }
        }

        var connection: HttpURLConnection? = null
        var inputStream: InputStream? = null
        var reader: BufferedReader? = null
        var response: StringBuilder? = null
        var responseJson: JSONObject? = null
        var connectFailDialog: AlertDialog.Builder? = null
    }
}