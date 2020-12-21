package com.example.android_qin

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.marginLeft
import com.xuexiang.xui.widget.textview.supertextview.SuperTextView

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SignDataForStudent.newInstance] factory method to
 * create an instance of this fragment.
 */
class SignDataForStudent : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_data_for_student, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SignDataForStudent.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SignDataForStudent().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        configSwipeRefresh()
    }
    private fun configSwipeRefresh(){
        val swipe= view?.findViewById<androidx.swiperefreshlayout.widget.SwipeRefreshLayout>(R.id.sign_data_swipe)
        swipe?.setOnRefreshListener {
            Log.i("swipe","sign_data")
            val signDataList= view?.findViewById<LinearLayout>(R.id.sign_data_list)
            val signDataView= SuperTextView(context)
            signDataView.setLeftBottomString("2020-12-21 20:00")
            signDataView.setRightString("未打卡")
            var layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dip2px(100f))
            layoutParams.setMargins(dip2px(10f),dip2px(10f),dip2px(10f),0)
            signDataView.layoutParams = layoutParams
            signDataList?.addView(signDataView)
            swipe.isRefreshing=false
        }
    }
    private fun dip2px(dpValue:Float): Int {
        val scale = context?.resources?.displayMetrics?.density;
        return (dpValue * scale!! + 0.5f).toInt()
    }
    private fun px2dip(pxValue:Float):Int{
        val scale = context?.resources?.displayMetrics?.density;
        return (pxValue / scale!! + 0.5f).toInt()
    }
}