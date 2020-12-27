package com.example.android_qin

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.android_qin.util.ConnectionUtil
import com.example.android_qin.util.NavUtil
import com.xuexiang.xui.XUI
import com.xuexiang.xui.widget.edittext.ClearEditText
import com.xuexiang.xui.widget.edittext.PasswordEditText
import com.xuexiang.xui.widget.spinner.materialspinner.MaterialSpinner
import org.json.JSONObject
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initUI()
        grantPermission()
        buildAnimationAndUI()
    }

    override fun onStart() {
        super.onStart()
        if (LOG_OUT) {
            if (!cancelAnimation){
                alphaAnimation?.cancel()
                cancelAnimation = true
            }
            buildUI()
        }
    }

    private fun buildAnimationAndUI(): Unit{
        if (LOG_OUT) {
            return Unit
        }
        val view = View.inflate(this, R.layout.start, null)
        setContentView(view)
        alphaAnimation = AlphaAnimation(0.3f, 1.0f)
        alphaAnimation?.duration = 3000
        view.startAnimation(alphaAnimation)
        alphaAnimation?.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationEnd(arg0: Animation?) {
                buildUI()
            }

            override fun onAnimationRepeat(animation: Animation?) {}
            override fun onAnimationStart(animation: Animation?) {}
        })
    }

    private fun configIdentity() {
        val identityView = findViewById<MaterialSpinner>(R.id.identity)
        identityView.setItems("我是学生", "我是教师")
        identityView.selectedIndex = 0
        identityView.setOnItemSelectedListener { view, position, id, item ->
            if (view.selectedIndex == 0) {
                identity = STUDENT
            } else if (view.selectedIndex == 1) {
                identity = TEACHER
            }
        }
    }

    private fun buildUI() {
        setContentView(R.layout.activity_main)
        checkLocalAccount()
        configLoginBtn()
        configIdentity()
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
            NO_USER -> runOnUiThread {
                Toast.makeText(this, "用户名不存在", Toast.LENGTH_LONG).show()
            }
            WRONG_PASSWORD -> runOnUiThread {
                Toast.makeText(this, "密码错误", Toast.LENGTH_LONG).show()
            }
            else -> runOnUiThread {
                Toast.makeText(this, "未知错误", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun toTeacherPage(loginInfo: JSONObject) {
        val intent = Intent(this, TeacherActivity::class.java).apply {}
        TeacherActivity.teacherId = loginInfo["teacherId"].toString()
        TeacherActivity.teacherName = loginInfo["teacherName"].toString()
        TeacherActivity.classesInfoString = loginInfo["classesInfo"].toString()
        startActivity(intent)
    }

    private fun toStudentPage(loginInfo: JSONObject) {
        val intent = Intent(this, StudentActivity::class.java).apply {}
        StudentActivity.studentId = loginInfo["studentId"].toString()
        StudentActivity.studentName = loginInfo["studentName"].toString()
        StudentActivity.classId = loginInfo["classId"].toString()
        StudentActivity.className = loginInfo["className"].toString()
        startActivity(intent)
    }


    private fun login() {
        Thread {
            buildRequestForLogin()
            configLoadingProgress()
            try {
                response = ConnectionUtil.getDataByUrl(urlForLogin)
            } catch (e: Exception) {
                ConnectionUtil.buildConnectFailDialog(applicationContext)
                runOnUiThread {
                    ConnectionUtil.connectFailDialog?.show()
                }
                Thread.currentThread().join()
            }
            dealWithResponse(response)
        }.start()
    }

    private fun dealWithResponse(response: StringBuilder?) {
        val loginInfo = JSONObject(response.toString())
        var loginState = loginInfo["loginState"]
        if (loginState is String) {
            loginState = loginState.toInt()
        }
        when (loginState) {
            STUDENT -> {
                toStudentPage(loginInfo)
            }
            TEACHER -> {
                toTeacherPage(loginInfo)
            }
            else -> errorTip(loginState)
        }
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

    private fun initUI() {
        XUI.init(this.application)
        XUI.debug(true)
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

    private fun buildRequestForLogin() {
        if (userName == null || password == null) {
            userName = findViewById(R.id.user_name)
            password = findViewById(R.id.password)
        }
        urlForLogin = if (identity == STUDENT) {
            URL("http://$ip:8080/student/login?username=" + userName?.text + "&password=" + password?.text)
        } else {
            URL("http://$ip:8080/teacher/login?username=" + userName?.text + "&password=" + password?.text)
        }
    }

    override fun onBackPressed() {
        // super.onBackPressed()
    }

    var loadingDialog: AlertDialog? = null
    var userName: ClearEditText? = null
    var password: PasswordEditText? = null
    var urlForLogin: URL? = null
    var response: StringBuilder? = null

    companion object {
        const val STUDENT = 1
        const val TEACHER = 2
        const val NO_USER = 3
        const val WRONG_PASSWORD = 4
        var LOG_OUT = false
        const val ip = "10.60.0.13"
        var identity = STUDENT
        var alphaAnimation: AlphaAnimation? = null
        var cancelAnimation = false
    }
}