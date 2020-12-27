package com.example.android_qin.util

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import com.example.android_qin.R
import com.example.android_qin.StudentActivity
import com.xuexiang.xui.utils.ResUtils.getDrawable
import com.xuexiang.xui.widget.tabbar.TabSegment

class TabBarUtil {
    companion object {
        fun configTabBar(
            tabSegment: TabSegment?,
            navController: NavController?,
            infoBundle: Bundle,
            identity: Int
        ) {
            addTabsToTabSegment(tabSegment)
            tabSegment?.mode = TabSegment.MODE_FIXED
            tabSegment?.selectTab(0)
            tabSegment?.notifyDataChanged()
            if (identity == STUDENT) {
                configNavForStudent(tabSegment, navController, infoBundle)
            } else if (identity == TEACHER) {
                configNavForTeacher(tabSegment, navController, infoBundle)
            }
        }

        private fun configNavForTeacher(
            tabSegment: TabSegment?,
            navController: NavController?,
            teacherInfo: Bundle
        ) {
            tabSegment?.setOnTabClickListener {
                when (it) {
                    TEACHER_SIGN_PAGE -> {
                        navController?.popBackStack()
                        navController?.navigate(
                            R.id.locationFragmentForTeacher,
                            teacherInfo
                        )
                    }
                    TEACHER_SIGN_DATA_PAGE -> {
                        navController?.popBackStack()
                        navController?.navigate(
                            R.id.signDataForTeacher,
                            teacherInfo
                        )
                    }
                    TEACHER_MINE_PAGE -> {
                        navController?.popBackStack()
                        navController?.navigate(R.id.mineForTeacher, teacherInfo)
                    }
                }
            }
        }

        private fun configNavForStudent(
            tabSegment: TabSegment?,
            navController: NavController?,
            studentInfo: Bundle
        ) {
            tabSegment?.setOnTabClickListener {
                when (it) {
                    STUDENT_SIGN_PAGE -> {
                        navController?.popBackStack()
                        navController?.navigate(
                            R.id.locationFragmentForStudent,
                            studentInfo
                        )
                    }
                    STUDENT_SIGN_DATA_PAGE -> {
                        navController?.popBackStack()
                        navController?.navigate(
                            R.id.signDataForStudent,
                            studentInfo
                        )
                    }
                    STUDENT_MINE_PAGE -> {
                        navController?.popBackStack()
                        navController?.navigate(R.id.mineForStudent, studentInfo)
                    }
                }
            }
        }

        private fun addTabsToTabSegment(tabSegment: TabSegment?) {
            tabSegment?.addTab(locationTab)
            tabSegment?.addTab(dataTab)
            tabSegment?.addTab(mineTab)
        }

        private fun buildTabForSign(): TabSegment.Tab {
            return TabSegment.Tab(
                getDrawable(R.drawable.location),
                getDrawable(R.drawable.location_chosen),
                "打卡",
                true
            )
        }

        private fun buildTabForSignData(): TabSegment.Tab {
            return TabSegment.Tab(
                getDrawable(R.drawable.data),
                getDrawable(R.drawable.data_chosen),
                "统计",
                true
            )
        }

        private fun buildTabForMine(): TabSegment.Tab {
            return TabSegment.Tab(
                getDrawable(R.drawable.mine),
                getDrawable(R.drawable.mine),
                "我的",
                true
            )
        }

        var locationTab: TabSegment.Tab = buildTabForSign()
        var dataTab: TabSegment.Tab = buildTabForSignData()
        var mineTab: TabSegment.Tab = buildTabForMine()
        private const val STUDENT_SIGN_PAGE = 0
        private const val STUDENT_SIGN_DATA_PAGE = 1
        private const val STUDENT_MINE_PAGE = 2
        private const val TEACHER_SIGN_PAGE = 0
        private const val TEACHER_SIGN_DATA_PAGE = 1
        private const val TEACHER_MINE_PAGE = 2
        const val STUDENT = 1
        const val TEACHER = 2
        var supportFragmentManager: FragmentManager? = null
    }
}