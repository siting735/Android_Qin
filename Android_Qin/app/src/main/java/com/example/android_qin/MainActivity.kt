package com.example.android_qin

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.DialogFragment.STYLE_NORMAL
import com.xuexiang.xui.XUI
import com.xuexiang.xui.widget.edittext.ClearEditText
import com.xuexiang.xui.widget.edittext.PasswordEditText
import org.json.JSONObject
import java.lang.Exception
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
        //toTeacherPage()
        toStudentPage()
    }
    private fun configLoginBtn(){
        val loginBtn=findViewById<com.xuexiang.xui.widget.button.ButtonView>(R.id.login_btn)
        loginBtn.setOnClickListener {
            login()
        }
    }
    private fun errorTip(loginState:Any){
        when(loginState){
            3 -> runOnUiThread {
                Toast.makeText(this,"用户名不存在",Toast.LENGTH_LONG).show()
            }
            4 -> runOnUiThread {
                Toast.makeText(this,"密码错误",Toast.LENGTH_LONG).show()
            }
            else -> runOnUiThread {
                Toast.makeText(this,"未知错误",Toast.LENGTH_LONG).show()
            }
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
    var loadingDialog:AlertDialog? = null
    private fun login(){
        var jsonString = ""
        val userName = findViewById<ClearEditText>(R.id.user_name)
        val password = findViewById<PasswordEditText>(R.id.password)
        configLoadingProgress()
        Thread {
            val url = URL("http://10.60.0.13:8081/student/login?name="+userName.text+"&password="+password.text)
            var connection: HttpURLConnection? = null
            var response:StringBuilder? = null
            try {
                connection = url.openConnection() as HttpURLConnection
                connection?.requestMethod = "GET"
                val inputStream = connection?.inputStream
                val reader = inputStream?.bufferedReader()
                response = StringBuilder()
                while (true)
                {
                    val line = reader?.readLine() ?: break
                    response.append(line)
                }
                reader?.close()
                connection?.disconnect()
            }
            catch (e:Exception){
                var loginFailDialog = AlertDialog.Builder(this)
                loginFailDialog.setTitle("提示信息")
                loginFailDialog.setMessage("连接服务器超时")
                loginFailDialog.setPositiveButton("确定"){
                    dialog, id -> loadingDialog?.cancel()
                }
                runOnUiThread {
                    loginFailDialog.show()
                }
                Log.i("login","error")
                Thread.currentThread().join()
            }
            Log.i("thread","this thread is still alive")
            jsonString = response.toString()
            Log.i("json",jsonString)
            val loginData=JSONObject(jsonString)
            var loginState=loginData["loginState"]
            if (loginState is String){
                loginState=loginState.toInt()
            }
            when (loginState) {
                1 -> {
                    //transport data
                    toStudentPage()
                }
                2 -> {
                    //transport data
                    toTeacherPage()
                }
                else -> errorTip(loginState)
            }
        }.start()
    }
    private fun configLoadingProgress(){
        var loadingDialogBuilder = AlertDialog.Builder(this)
        val loadingProgress = ProgressBar(this)
        loadingDialogBuilder.setView(loadingProgress)
        loadingDialogBuilder.setTitle("正在登陆...")
        loadingDialog = loadingDialogBuilder.create()
        loadingDialog!!.show()
        //loadingDialog.show()
//        var loginFailDialog = AlertDialog.Builder(this)
//        loginFailDialog.setTitle("提示信息")
//        loginFailDialog.setMessage("连接服务器超时")
//        loginFailDialog.setPositiveButton("确定"){
//            dialog, id ->{
//
//        }
//        }
//        loginFailDialog.show()
//        val dialogFragment = LoadingStateFragment()
//        dialogFragment.show(fragmentManager,"loading")
//        dialogFragment.cancel()
    }
    private fun grantPermission(){
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