package com.chenbabys.dingdingtimestatistics

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Handler
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.SpanUtils
import com.chenbabys.dingdingtimestatistics.base.BaseActivity
import com.chenbabys.dingdingtimestatistics.base.BaseViewModel
import com.chenbabys.dingdingtimestatistics.databinding.ActivitySplashBinding
import com.chenbabys.dingdingtimestatistics.util.CalenderUtil
import java.text.SimpleDateFormat
import java.util.*

class SplashActivity : BaseActivity<BaseViewModel, ActivitySplashBinding>() {
    @SuppressLint("SimpleDateFormat")
    private val format = SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss")

    override fun initView() {
        Handler(mainLooper).postDelayed({
            startActivity(Intent(mContext, MainActivity::class.java))
            finish()
        }, 3000)
        initTime()
    }

    private fun initTime() {
        val timeStr = format.format(Date(System.currentTimeMillis()))
            SpanUtils.with(binding.tvDate)
                .append(timeStr)
                .setFontSize(22,true)
                .appendLine()
                .append(CalenderUtil.getWeekCurrent())
                .setFontSize(30,true)
                .create()
        //延时1s从新赋值后又开始延时，周而复始
        Handler(mainLooper).postDelayed({
            initTime()
        },1000)
    }



    override fun initVm() {

    }
}