package com.example.android_qin

import android.app.Dialog
import android.app.DialogFragment
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog

class LoadingStateFragment :DialogFragment(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_loading_state, container, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        var loadingDialog = AlertDialog.Builder(this.context)
        val loadingProgress = ProgressBar(this.context)
        loadingDialog.setView(loadingProgress)
        loadingDialog.setTitle("正在登陆...")
        return loadingDialog.create()

    }

    companion object {

    }
}