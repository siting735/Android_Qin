package com.example.android_qin

import java.net.HttpURLConnection

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

    }
}