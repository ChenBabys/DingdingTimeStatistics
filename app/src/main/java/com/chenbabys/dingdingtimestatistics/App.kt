package com.chenbabys.dingdingtimestatistics

import android.app.Application
import android.content.Context
import com.pgyer.pgyersdk.PgyerSDKManager
import com.tencent.mmkv.MMKV

/**
 * Created by ChenYesheng On 2021/7/26 18:52
 * Desc:
 */
class App: Application() {

    override fun onCreate() {
        super.onCreate()
        MMKV.initialize(this)
    }

    //在attachBaseContext方法中调用初始化sdk
    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        //初始化蒲公英SDK
        PgyerSDKManager.Init().setContext(this).start()
    }


}