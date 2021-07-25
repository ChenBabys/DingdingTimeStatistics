package com.jiuxiaoer.ctms.api.error

import retrofit2.HttpException

object ErrorUtil {

    fun getError(e: Throwable): ErrorResult {
        val errorResult = ErrorResult()
        if (e is HttpException) {
            errorResult.code = e.code()
        }
        errorResult.errMsg = e.message
        if (errorResult.errMsg.isNullOrEmpty()) errorResult.errMsg = "网络请求失败，请重试"
        return errorResult
    }

    fun getError(apiCode: Int, e: Throwable,status:Int=0, params: MutableMap<String,String> = mutableMapOf()): ErrorResult {
        val errorResult = ErrorResult()
        errorResult.apiCode = apiCode
        if (e is HttpException) {
            errorResult.code = e.code()
        }
        errorResult.errMsg = e.message
        errorResult.status = status
        errorResult.params = params
        if (errorResult.errMsg.isNullOrEmpty()) errorResult.errMsg = "网络请求失败，请重试"
        return errorResult
    }

}