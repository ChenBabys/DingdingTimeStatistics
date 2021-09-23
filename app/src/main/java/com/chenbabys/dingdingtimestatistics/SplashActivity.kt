package com.chenbabys.dingdingtimestatistics

import android.content.Intent
import android.os.Handler
import com.blankj.utilcode.util.BarUtils
import com.chenbabys.dingdingtimestatistics.base.BaseActivity
import com.chenbabys.dingdingtimestatistics.base.BaseViewModel
import com.chenbabys.dingdingtimestatistics.databinding.ActivitySplashBinding
import java.text.SimpleDateFormat
import java.util.*

class SplashActivity : BaseActivity<BaseViewModel, ActivitySplashBinding>() {
    private val format = SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss")

    override fun initView() {
        BarUtils.transparentStatusBar(this)
        BarUtils.isNavBarLightMode(this)
        Handler(mainLooper).postDelayed({
            startActivity(Intent(mContext, MainActivity::class.java))
            finish()
        }, 5000)
        initTime()
    }

    private fun initTime() {
        val timeStr = format.format(Date(System.currentTimeMillis()))
        binding.tvDate.text = timeStr
        //延时1s从新赋值后又开始延时，周而复始
        Handler(mainLooper).postDelayed({
            initTime()
        },1000)
    }



    override fun initVm() {

    }
}