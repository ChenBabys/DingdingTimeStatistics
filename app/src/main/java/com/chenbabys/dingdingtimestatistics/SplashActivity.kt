package com.chenbabys.dingdingtimestatistics

import android.content.Intent
import android.os.Handler
import android.view.View
import com.blankj.utilcode.util.BarUtils
import com.chenbabys.dingdingtimestatistics.base.BaseActivity
import com.chenbabys.dingdingtimestatistics.base.BaseViewModel
import com.chenbabys.dingdingtimestatistics.databinding.ActivitySplashBinding

class SplashActivity : BaseActivity<BaseViewModel, ActivitySplashBinding>() {

    override fun initView() {
        BarUtils.transparentStatusBar(this)
        BarUtils.isNavBarLightMode(this)
        Handler(mainLooper).postDelayed({
            startActivity(Intent(mContext, MainActivity::class.java))
            finish()
        }, 500)
    }

    override fun initVm() {

    }
}