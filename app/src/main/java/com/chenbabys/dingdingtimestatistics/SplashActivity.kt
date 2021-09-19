package com.chenbabys.dingdingtimestatistics

import android.content.Intent
import com.chenbabys.dingdingtimestatistics.base.BaseActivity
import com.chenbabys.dingdingtimestatistics.base.BaseViewModel
import com.chenbabys.dingdingtimestatistics.databinding.ActivitySplashBinding

class SplashActivity : BaseActivity<BaseViewModel,ActivitySplashBinding>() {


    override fun initView() {
       startActivity(Intent(mContext,MainActivity::class.java))
    }

    override fun initVm() {

    }
}