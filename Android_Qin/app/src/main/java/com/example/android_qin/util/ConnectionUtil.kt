package com.example.android_qin.util

import android.content.Context
import androidx.appcompat.app.AlertDialog
import java.net.HttpURLConnection
import java.net.URL

class ConnectionUtil {
    companion object {
        fun getDataByUrl(url: URL?): StringBuilder {
            val connection = url?.openConnection() as HttpURLConnection
            connection?.requestMethod = "GET"
            val inputStream = connection?.inputStream
            val reader = inputStream?.bufferedReader()
            val response = StringBuilder()
            while (true) {
                val line = reader?.readLine() ?: break
                response.append(line)
            }
            reader?.close()
            connection?.disconnect()
            return response
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

        var connectFailDialog: AlertDialog.Builder? = null
    }
}