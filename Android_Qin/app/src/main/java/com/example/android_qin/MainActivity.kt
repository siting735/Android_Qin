package com.example.android_qin

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.amap.api.maps2d.AMap
import com.amap.api.maps2d.MapView
import com.amap.api.maps2d.model.MyLocationStyle
import com.xuexiang.xui.XUI
import com.xuexiang.xui.widget.edittext.ClearEditText
import com.xuexiang.xui.widget.edittext.PasswordEditText
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        XUI.init(this.application) // 初始化UI框架
        XUI.debug(true)  // 开启UI框架调试日志
        grantPermission()
        //checkLocalAccount()
        setContentView(R.layout.activity_main)
        configLoginBtn()
    }
    private fun checkLocalAccount(){
        // valid day 5
        // "username":"","password":"","role":"","loginDate":""
        val intent = Intent(this, StudentActivity::class.java).apply {}
        startActivity(intent)
//        val intent = Intent(this, TeacherActivity::class.java).apply {}
//        startActivity(intent)
    }
    private fun configLoginBtn(){
        val loginBtn=findViewById<com.xuexiang.xui.widget.button.ButtonView>(R.id.login_btn)
        loginBtn.setOnClickListener {
            when (val loginState=login()) {
                1 -> toStudentPage()
                2 -> toTeacherPage()
                else -> errorTip(loginState)
            }
        }
    }
    private fun errorTip(loginState:Int){
        when(loginState){
            3 -> Toast.makeText(this,"用户名不存在",Toast.LENGTH_LONG).show()
            4 -> Toast.makeText(this,"密码错误",Toast.LENGTH_LONG).show()
            else -> Toast.makeText(this,"未知错误",Toast.LENGTH_LONG).show()
        }
    }
    private fun toTeacherPage(){
        val intent = Intent(this, TeacherActivity::class.java).apply {}
        startActivity(intent)
    }
    private fun toStudentPage(){
        val intent = Intent(this, StudentActivity::class.java).apply {}
        startActivity(intent)
    }
    private fun login(): Int {
        val userName = findViewById<ClearEditText>(R.id.user_name)
        val password = findViewById<PasswordEditText>(R.id.password)
        Thread {
            val url = URL("http://10.60.0.13:8080/student/login?name="+userName.text+"&password="+password.text)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            val inputstream = connection.inputStream
            val reader = inputstream.bufferedReader()
            var response = StringBuilder()
            while (true)
            {
                val line = reader.readLine() ?: break
                response.append(line)
            }
            reader.close()
            connection.disconnect()
            val jsonsString = response.toString()
            val jsons = JSONObject(jsonsString)
            Log.i("json",jsonsString)
            runOnUiThread{

            }
        }.start()
        return 1
    }
    fun grantPermission(){
        if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
        {
            Toast.makeText(this,"granting permission",Toast.LENGTH_SHORT).show()
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION
                            , Manifest.permission.ACCESS_FINE_LOCATION
                            , Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_NETWORK_STATE,
                            Manifest.permission.INTERNET,
                            Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS
                    ),
                    10)
        }
        else{
            Toast.makeText(this,"already get",Toast.LENGTH_SHORT).show()
        }
    }
}