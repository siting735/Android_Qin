package com.example.android_qin.util

import java.net.HttpURLConnection
import java.net.URL

class ConnectionUtil {
    companion object{
        public fun getDataFromConnection(connection: HttpURLConnection): StringBuilder {
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
        fun getDataByUrl(url: URL?): StringBuilder{
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

    }
}