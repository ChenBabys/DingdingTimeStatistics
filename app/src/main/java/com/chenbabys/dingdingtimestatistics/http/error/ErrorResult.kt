package com.jiuxiaoer.ctms.api.error

class ErrorResult @JvmOverloads constructor(
    var code: Int = 0,
    var errMsg: String? = "",
    var show: Boolean = false,
    var apiCode: Int = 0,//表示api类型（确定是哪个api）
    var status: Int = 0,//表示api类型（确定是哪个api）
    var params: MutableMap<String,String> = mutableMapOf()//参数
)