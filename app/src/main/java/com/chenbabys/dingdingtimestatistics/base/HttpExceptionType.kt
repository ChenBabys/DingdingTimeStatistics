package com.chenbabys.dingdingtimestatistics.base

open class HttpExceptionType{
    class TokenInvalidException(msg: String? = "token失效，请重新登录",status:Int?=0) : Exception(msg)
    class NoPermissionsException(msg: String? = "您没有操作权限，请联系管理员开通",status:Int?=0) : Exception(msg)
    class NotFoundException(msg: String? = "请求的地址不存在",status:Int?=0) : Exception(msg)
    class InterfaceErrException(msg: String? = "接口请求出错",status:Int?=0) : Exception(msg)
    class TimeOutErrException(msg: String? = "连接超时",status:Int?=0) : Exception(msg)
    class RequestErrException(msg: String? = "错误的请求，请修改客户端",status:Int?=0) : Exception(msg)
    class FieldEmptyException(msg: String? = "请检查接口返回的相应实体出现的空字段",status:Int?=0) : Exception(msg)
}