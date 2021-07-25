package com.chenbabys.dingdingtimestatistics.base

open class BaseResult<T> {
    val message: String? = null //响应信息
    val prompt: String? = null //响应提示
    val error: String? = null  //错误原因
    val errorCode: Int = 0
    val status: Int = 0
    val data: T? = null
    var params: MutableMap<String,String> = mutableMapOf()//参数
}