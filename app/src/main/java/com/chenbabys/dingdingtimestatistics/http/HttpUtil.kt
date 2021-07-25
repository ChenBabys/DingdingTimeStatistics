package com.chenbabys.dingdingtimestatistics.http

import com.jiuxiaoer.ctms.api.retrofit.RetrofitClient

class HttpUtil {
    //可以根据不同的功能模块创建不同的Service
    private val mService by lazy { RetrofitClient.getInstance().createService(ApiService::class.java) }

    companion object {
        @Volatile
        private var httpUtil: HttpUtil? = null

        fun getInstance() = httpUtil ?: synchronized(this) {
            httpUtil ?: HttpUtil().also { httpUtil = it }
        }
    }

    //可以直接在BaseViewModel中获取取ApiService对象，简化接口调用
    fun apiService(): ApiService {
        return mService
    }
}
