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
            tabSegment: TabSegment?, identity: Int
        ) {
            addTabsToTabSegment(tabSegment)
            tabSegment?.mode = TabSegment.MODE_FIXED
            tabSegment?.selectTab(0)
            tabSegment?.notifyDataChanged()
            if (identity == STUDENT) {
                configNavForStudent(tabSegment)
            } else if (identity == TEACHER) {
                configNavForTeacher(tabSegment)
            }
        }

        private fun configNavForTeacher(tabSegment: TabSegment?) {
            tabSegment?.setOnTabClickListener {
                when (it) {
                    TEACHER_SIGN_PAGE -> {
                        NavUtil.navController?.popBackStack()
                        NavUtil.navController?.navigate(R.id.locationFragmentForTeacher)
                    }
                    TEACHER_SIGN_DATA_PAGE -> {
                        NavUtil.navController?.popBackStack()
                        NavUtil.navController?.navigate(R.id.signDataForTeacher)
                    }
                    TEACHER_MINE_PAGE -> {
                        NavUtil.navController?.popBackStack()
                        NavUtil.navController?.navigate(R.id.mineForTeacher)
                    }
                }
            }
        }

        private fun configNavForStudent(tabSegment: TabSegment?) {
            tabSegment?.setOnTabClickListener {
                when (it) {
                    STUDENT_SIGN_PAGE -> {
                        NavUtil.navController?.popBackStack()
                        NavUtil.navController?.navigate(R.id.locationFragmentForStudent)
                    }
                    STUDENT_SIGN_DATA_PAGE -> {
                        NavUtil.navController?.popBackStack()
                        NavUtil.navController?.navigate(R.id.signDataForStudent)
                    }
                    STUDENT_MINE_PAGE -> {
                        NavUtil.navController?.popBackStack()
                        NavUtil.navController?.navigate(R.id.mineForStudent)
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
                false
            )
        }

        private fun buildTabForSignData(): TabSegment.Tab {
            return TabSegment.Tab(
                getDrawable(R.drawable.data),
                getDrawable(R.drawable.data_chosen),
                "统计",
                false
            )
        }

        private fun buildTabForMine(): TabSegment.Tab {
            return TabSegment.Tab(
                getDrawable(R.drawable.mine),
                getDrawable(R.drawable.mine_chosen),
                "我的",
                false
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
    }
}