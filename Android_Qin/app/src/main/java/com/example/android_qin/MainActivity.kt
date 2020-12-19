package com.example.android_qin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.amap.api.maps2d.AMap
import com.amap.api.maps2d.MapView
import com.amap.api.maps2d.model.MyLocationStyle
import com.xuexiang.xui.XUI

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        XUI.init(this.application) // 初始化UI框架
        XUI.debug(true)  // 开启UI框架调试日志
        checkLocalAccount()
        setContentView(R.layout.activity_main)
        configLoginBtn()
    }
    private fun checkLocalAccount(){
        // valid day 5
        // "username":"","password":"","role":"","loginDate":""

        val intent = Intent(this, StudentActivity::class.java).apply {}
        startActivity(intent)
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
        return 2
    }
}