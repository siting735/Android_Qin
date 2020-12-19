package com.example.android_qin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.xuexiang.xui.widget.tabbar.TabSegment

class TeacherActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teacher)
        configTabBar()
    }
    private fun configTabBar(){
        var tabSegment=findViewById<TabSegment>(R.id.teacher_tab_bar)
        tabSegment.mode = TabSegment.MODE_FIXED
        tabSegment.addTab(TabSegment.Tab(this.applicationContext.getDrawable(R.drawable.location),this.applicationContext.getDrawable(R.drawable.location_chosen),"打卡",true))
        tabSegment.addTab(TabSegment.Tab(this.applicationContext.getDrawable(R.drawable.data),this.applicationContext.getDrawable(R.drawable.data_chosen),"统计",true))
        tabSegment.addTab(TabSegment.Tab(this.applicationContext.getDrawable(R.drawable.mine),this.applicationContext.getDrawable(R.drawable.mine),"我的",true))
        tabSegment.notifyDataChanged()
        //Log.i("draw",this.applicationContext.getDrawable(R.drawable.mainpage).toString())
    }
}