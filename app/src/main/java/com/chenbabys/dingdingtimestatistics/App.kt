package com.chenbabys.dingdingtimestatistics

import android.app.Application
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

}