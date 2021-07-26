package com.chenbabys.dingdingtimestatistics.base

import android.text.TextUtils
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jiuxiaoer.ctms.api.error.ErrorResult
import com.jiuxiaoer.ctms.api.error.ErrorUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 基类viewModel
 */
open class BaseViewModel:ViewModel() , LifecycleObserver {
    var loadingMessage = MutableLiveData<LoadingMessage>()//是否显示loading
    var errorData = MutableLiveData<ErrorResult>()//错误信息
    private fun showError(error: ErrorResult) {
        errorData.value = error
    }

    private fun showLoading(message:String = "加载中") {
        loadingMessage.value = LoadingMessage(message,true)
    }

    private fun dismissLoading() {
        loadingMessage.value = LoadingMessage("",false)
    }

    /**
     * 请求接口，可定制是否显示loading和错误提示（一次调用一个）
     * apiIndex 主要用于区分哪个api返回的错误
     */
    fun <T> launch(
        block: suspend CoroutineScope.() -> BaseResult<T>,//请求接口方法，T表示data实体泛型，调用时可将data对应的bean传入即可
        liveData: MutableLiveData<T>,
        isShowLoading: Boolean = false,
        isShowError: Boolean = true,
        apiCode:Int = 0,
        params:  MutableMap<String,String> =mutableMapOf(),
        loadingMessage:String = "加载中"
    ) {
        if (isShowLoading) showLoading(loadingMessage)
        viewModelScope.launch {
            var status = 0
            try {
                val result = withContext(Dispatchers.IO) { block() }
                if (result.status == 200) {//请求成功
                    liveData.value = result.data
                    if(isShowLoading){
                        dismissLoading()
                    }
                } else {
                    var showMessage:String? =   null
                    if (!TextUtils.isEmpty(result.prompt)) {
                        showMessage = result.prompt
                    }
                    status = result.status
                    when (result.status) {
                        //一般0和200是请求成功，直接返回数据
                        0, 200 -> this
                        403 -> throw HttpExceptionType.NoPermissionsException(showMessage)
                        404,570404 -> throw HttpExceptionType.NotFoundException(showMessage)
                        406 -> throw HttpExceptionType.RequestErrException(showMessage)
                        500 -> throw HttpExceptionType.InterfaceErrException(showMessage)
                        504 -> throw HttpExceptionType.TimeOutErrException(showMessage)
                        else -> throw Exception(showMessage)
                    }
                }
            } catch (e: Throwable) {//接口请求失败
                val errorResult = ErrorUtil.getError(apiCode,e,status,params)
                errorResult.show = isShowError
                showError(errorResult)
            } finally {//请求结束
                if(isShowLoading) {
                    dismissLoading()
                }
            }
        }
    }
}