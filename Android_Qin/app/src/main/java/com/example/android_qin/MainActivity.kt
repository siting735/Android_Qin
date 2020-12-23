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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.xuexiang.xui.XUI
import com.xuexiang.xui.widget.edittext.ClearEditText
import com.xuexiang.xui.widget.edittext.PasswordEditText
import com.xuexiang.xui.widget.spinner.materialspinner.MaterialSpinner
import org.json.JSONObject
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {
    val teacher = 1
    val student = 0
    var identity = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        XUI.init(this.application) // 初始化UI框架
        XUI.debug(true)  // 开启UI框架调试日志
        grantPermission()
        checkLocalAccount()
        setContentView(R.layout.activity_main)
        configLoginBtn()
        configIdentity()
    }

    private fun configIdentity() {
        val identityView = findViewById<MaterialSpinner>(R.id.identity)
        identityView.setItems("我是学生", "我是教师")
        identityView.selectedIndex = 0
        identityView.setOnItemSelectedListener { view, position, id, item ->
            if (view.selectedIndex == student) {
                identity = student
            } else if (view.selectedIndex == teacher) {
                identity = teacher
            }
        }
    }

    private fun checkLocalAccount() {
        // valid day 5
        // "username":"","password":"","role":"","loginDate":""
        //toTeacherPage()
        //toStudentPage()
    }

    private fun configLoginBtn() {
        val loginBtn = findViewById<com.xuexiang.xui.widget.button.ButtonView>(R.id.login_btn)
        loginBtn.setOnClickListener {
            login()
        }
    }

    private fun errorTip(loginState: Any) {
        loadingDialog?.cancel()
        when (loginState) {
            3 -> runOnUiThread {
                Toast.makeText(this, "用户名不存在", Toast.LENGTH_LONG).show()
            }
            4 -> runOnUiThread {
                Toast.makeText(this, "密码错误", Toast.LENGTH_LONG).show()
            }
            else -> runOnUiThread {
                Toast.makeText(this, "未知错误", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun toTeacherPage(loginInfo: JSONObject) {
        val intent = Intent(this, TeacherActivity::class.java).apply {}
        startActivity(intent)
    }

    private fun toStudentPage(loginInfo: JSONObject) {
        val intent = Intent(this, StudentActivity::class.java).apply {}
        intent.putExtra("studentId", loginInfo["studentId"].toString())
        intent.putExtra("studentName", loginInfo["studentName"].toString())
        intent.putExtra("classId", loginInfo["classId"].toString())
        intent.putExtra("className", loginInfo["className"].toString())
        startActivity(intent)
    }

    var loadingDialog: AlertDialog? = null
    private fun login() {
        Thread {
            val userName = findViewById<ClearEditText>(R.id.user_name)
            val password = findViewById<PasswordEditText>(R.id.password)
            var urlForLogin: URL? = null
            configLoadingProgress()
            urlForLogin = if(identity == student){
                URL("http://10.60.0.13:8080/student/login?username=" + userName.text + "&password=" + password.text)
            } else{
                URL("http://10.60.0.13:8080/teacher/login?username=" + userName.text + "&password=" + password.text)
            }
            var connection: HttpURLConnection? = null
            var response: StringBuilder? = null
            try {
                connection = urlForLogin.openConnection() as HttpURLConnection
                connection?.requestMethod = "GET"
                response = getDataFromConnection(connection)
                connection?.disconnect()
            } catch (e: Exception) {
                var loginFailDialog = buildConnectFailDialog()
                runOnUiThread {
                    loginFailDialog.show()
                    Log.e("error in location", e.toString())
                    Log.i("fail dialog", "i am main")
                }
                Thread.currentThread().join()
            }
            dealWithResponse(response)
        }.start()
    }

    private fun dealWithResponse(response: StringBuilder?) {
        val jsonString = response.toString()
        val loginInfo = JSONObject(jsonString)
        var loginState = loginInfo["loginState"]
        if (loginState is String) {
            loginState = loginState.toInt()
        }
        Log.i("loginState", loginState.toString())
        when (loginState) {
            1 -> {
                //transport data
                toStudentPage(loginInfo)
            }
            2 -> {
                //transport data
                toTeacherPage(loginInfo)
            }
            else -> errorTip(loginState)
        }
    }

    private fun buildConnectFailDialog(): AlertDialog.Builder {
        val loginFailDialog = AlertDialog.Builder(this)
        loginFailDialog.setTitle("提示信息")
        loginFailDialog.setMessage("连接服务器失败")
        loginFailDialog.setPositiveButton("确定") { dialog, id ->
            loadingDialog?.cancel()
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

    private fun configLoadingProgress() {
        var loadingDialogBuilder = AlertDialog.Builder(this)
        val loadingProgress = ProgressBar(this)
        loadingDialogBuilder.setView(loadingProgress)
        loadingDialogBuilder.setTitle("正在登陆...")
        runOnUiThread {
            loadingDialog = loadingDialogBuilder.create()
            loadingDialog!!.show()
        }
    }

    private fun grantPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(this, "granting permission", Toast.LENGTH_SHORT).show()
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.INTERNET,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS
                ),
                10
            )
        } else {
            Toast.makeText(this, "already get permission", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        const val TEACHER = 1
    }
}