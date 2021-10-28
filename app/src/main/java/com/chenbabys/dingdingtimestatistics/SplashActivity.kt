package com.chenbabys.dingdingtimestatistics

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Handler
import com.android.dingtalk.share.ddsharemodule.message.SendAuth
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.SpanUtils
import com.blankj.utilcode.util.ToastUtils
import com.chenbabys.dingdingtimestatistics.base.BaseActivity
import com.chenbabys.dingdingtimestatistics.base.BaseViewModel
import com.chenbabys.dingdingtimestatistics.databinding.ActivitySplashBinding
import com.chenbabys.dingdingtimestatistics.ddshare.DDShareActivity
import com.chenbabys.dingdingtimestatistics.ui.viewmodel.SplashVM
import com.chenbabys.dingdingtimestatistics.util.CalenderUtil
import java.text.SimpleDateFormat
import java.util.*

class SplashActivity : BaseActivity<SplashVM, ActivitySplashBinding>() {
    @SuppressLint("SimpleDateFormat")
    private val format = SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss")

    override fun initView() {
        //这两个是钉钉相关的，一个是登录授权，一个是获取token，都没成功
        //startActivity(Intent(mContext, DDShareActivity::class.java))
        //viewModel.getUserToken()
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
            .setFontSize(22, true)
            .appendLine()
            .append(CalenderUtil.getWeekCurrent())
            .setFontSize(30, true)
            .create()
        //延时1s从新赋值后又开始延时，周而复始
        Handler(mainLooper).postDelayed({
            initTime()
        }, 1000)
    }


    override fun finish() {
        super.finish()
        overridePendingTransition(
            R.anim.dialog_raise_center_in,
            R.anim.dialog_raise_center_out
        )
    }


    override fun initVm() {
        viewModel.tokenLiveData.observe(this, androidx.lifecycle.Observer {
            ToastUtils.showLong(it.accessToken)
        })
    }
}