package com.example.android_qin

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.android_qin.util.*
import com.xuexiang.xui.XUI
import com.xuexiang.xui.widget.button.ButtonView
import com.xuexiang.xui.widget.edittext.ClearEditText
import com.xuexiang.xui.widget.edittext.PasswordEditText
import com.xuexiang.xui.widget.spinner.materialspinner.MaterialSpinner
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import java.util.zip.Inflater

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initUILibrary()
        grantPermission()
        AccountInfoUtil.getSharePreference(applicationContext)
        buildAnimationAndUI() // build UI should after load login layout
        LayoutUtil.buildLayoutParamsForInfoUnit(applicationContext)
    }

    override fun onStart() {
        super.onStart()
        if (LOG_OUT) {
            if (!cancelAnimation) {
                alphaAnimation?.cancel()
                cancelAnimation = true
            }
            buildUIAfterLogOut()
        }
    }

    private fun buildAnimationAndUI(): Unit {
        if (LOG_OUT) {
            return Unit
        }
        val advertisementView = View.inflate(this, R.layout.start, null)
        setContentView(advertisementView)
        alphaAnimation = AlphaAnimation(0.3f, 1.0f)
        alphaAnimation?.duration = 3000
        advertisementView.startAnimation(alphaAnimation)
        alphaAnimation?.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationEnd(arg0: Animation?) {
                checkAutoLogin()
                buildUI()
            }

            override fun onAnimationRepeat(animation: Animation?) {}
            override fun onAnimationStart(animation: Animation?) {}
        })
    }

    private fun buildUI() {
        setContentView(R.layout.activity_main)
        configInputEditText()
        configLoginBtn()
        configIdentity()
        toLoginPage()
    }

    private fun checkAutoLogin() {
        val accessIp = getAccessIp()
        Log.i("accessIp", accessIp)
        if (accessIp == null.toString()) {
            return Unit
        }
        buildDataForCheckLocalAccount()
        Log.i("phoneIp", phoneIp.toString())
        Log.i("accessIp", accessIp)
        if (phoneIp.equals(accessIp)) {
            configLoadingProgress()
            Log.i("phoneIp", "equal accessIp")
            getAccountInfoFromSharePreference()
            buildUrlForLogin()
            Log.i("UrlForLogin",urlForLogin.toString())
            Thread{
                getDataByRequest()
                dealWithResponse()
            }.start()
        } else {
            return Unit
        }
    }

    private fun login() {
        requestThread = Thread {
            buildRequestForLogin()
            configLoadingProgress()
            getDataByRequest()
            dealWithResponse()
        }
        requestThread?.start()
    }

    private fun recordLoginState() {
        wifi = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        wifiInfo = wifi?.connectionInfo
        phoneIp = wifiInfo?.ipAddress.toString()
        AccountInfoUtil.sharePreferenceEditor?.putString("ip", phoneIp)
        if (username != null) {
            AccountInfoUtil.sharePreferenceEditor?.putString("username", username.toString())
            AccountInfoUtil.sharePreferenceEditor?.putString("password", password.toString())
        }
        AccountInfoUtil.sharePreferenceEditor?.putString("identity", identity.toString())
        AccountInfoUtil.sharePreferenceEditor?.commit()
    }

    private fun configInputEditText() {
        if (userNameEditText == null || passwordEditText == null) {
            userNameEditText = findViewById(R.id.user_name)
            passwordEditText = findViewById(R.id.password)
        }
    }

    private fun buildUrlForLogin() {
        urlForLogin = if (identity == STUDENT) {
            URL("http://$ip:8080/student/login?username=$username&password=$password")
        } else {
            URL("http://$ip:8080/teacher/login?username=$username&password=$password")
        }
    }

    private fun getAccountInfoFromSharePreference() {
        username = AccountInfoUtil.sharePreference?.getString("username", null).toString()
        password = AccountInfoUtil.sharePreference?.getString("password", null).toString()
        identity = AccountInfoUtil.sharePreference?.getString("identity", null)!!.toInt()
    }

    private fun buildUIAfterLogOut() {
        setContentView(R.layout.activity_main)
        loginView = findViewById(R.id.login_view)
        loginView?.visibility = View.VISIBLE
        configLoginBtn()
        configIdentity()
    }

    private fun toLoginPage() {
        loginView = findViewById(R.id.login_view)
        Log.i("loginView", loginView.toString())
        runOnUiThread {
            loginView?.visibility = View.VISIBLE
        }
    }

    private fun getAccessIp(): String {
        return AccountInfoUtil.sharePreference?.getString("ip", null).toString()
    }

    private fun configLoginBtn() {
        val loginBtn = findViewById<ButtonView>(R.id.login_btn)
        loginBtn.setOnClickListener {
            if (!connectingServer) {
                login()
            }
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
        TeacherActivity.classList = loginInfo["classesInfo"] as JSONArray
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

    private fun getDataByRequest() {
        try {
            ConnectionUtil.getDataByUrl(urlForLogin)
        } catch (e: Exception) {
            e.printStackTrace()
            loadingDialog?.cancel()
            ConnectionUtil.buildConnectFailDialog(applicationContext)
            runOnUiThread {
                ConnectionUtil.connectFailDialog?.show()
            }
            Thread.currentThread().join()
        }
    }

    private fun dealWithResponse() {
        val loginInfo = JSONObject(ConnectionUtil.response.toString())
        val loginState = loginInfo["loginState"] as Int
        Log.i("loginState",loginInfo.toString())
        connectingServer = false
        when (loginState) {
            STUDENT -> {
                recordLoginState()
                toStudentPage(loginInfo)
            }
            TEACHER -> {
                recordLoginState()
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
        loadingDialogBuilder.setOnCancelListener {
            connectingServer = false
            requestThread?.join()
        }
        runOnUiThread {
            loadingDialog = loadingDialogBuilder.create()
            loadingDialog!!.show()
        }

    }

    private fun initUILibrary() {
        XUI.init(this.application)
        XUI.debug(true)
    }

    private fun grantPermission() {
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
                Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.WAKE_LOCK
            ),
            10
        )
    }

    private fun buildDataForCheckLocalAccount() {
        wifi = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        wifiInfo = wifi?.connectionInfo
        Log.i("wifiInfo",wifiInfo.toString())
        phoneIp = wifiInfo?.ipAddress.toString()
    }

    private fun buildRequestForLogin() {
        if (userNameEditText == null || passwordEditText == null) {
            userNameEditText = findViewById(R.id.user_name)
            passwordEditText = findViewById(R.id.password)
        }
        username = userNameEditText?.text.toString()
        password = passwordEditText?.text.toString()
        urlForLogin = if (identity == STUDENT) {
            URL("http://$ip:8080/student/login?username=$username&password=$password")
        } else {
            URL("http://$ip:8080/teacher/login?username=$username&password=$password")
        }
    }

    override fun onBackPressed() {
        // super.onBackPressed()
    }

    override fun onPause() {
        super.onPause()
        tempUserName = userNameEditText?.text.toString()
        tempPasswordString = passwordEditText?.text.toString()

    }

    private fun configIdentity() {
        val identityView = findViewById<MaterialSpinner>(R.id.identity)
        identityView.setItems("我是学生", "我是教师")
        identityView.selectedIndex = 0
        identityView.setOnItemSelectedListener { view, position, id, item ->
            if (view.selectedIndex == STUDENT_INDEX) {
                identity = STUDENT
            } else if (view.selectedIndex == TEACHER_INDEX) {
                identity = TEACHER
            }
        }
    }

    override fun onResume() {
        userNameEditText?.setText(tempUserName)
        passwordEditText?.setText(tempPasswordString)
        super.onResume()
    }

    var loadingDialog: AlertDialog? = null
    var userNameEditText: ClearEditText? = null
    var tempUserName: String? = null
    var tempPasswordString: String? = null
    var passwordEditText: PasswordEditText? = null
    var urlForLogin: URL? = null
    var connectingServer: Boolean = false
    var requestThread: Thread? = null
    var username: String? = null
    var password: String? = null
    private var wifi: WifiManager? = null
    private var wifiInfo: WifiInfo? = null
    var phoneIp: String? = null

    companion object {
        const val STUDENT = 1
        const val STUDENT_INDEX = 0
        const val TEACHER_INDEX = 1
        const val TEACHER = 2
        const val NO_USER = 3
        const val WRONG_PASSWORD = 4
        var LOG_OUT = false
        const val ip = "10.60.0.13"

        // const val ip = "192.168.3.195"
        var identity = STUDENT
        var alphaAnimation: AlphaAnimation? = null
        var cancelAnimation = false
        var loginView: LinearLayout? = null

    }
}