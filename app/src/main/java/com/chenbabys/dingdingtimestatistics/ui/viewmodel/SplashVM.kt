package com.chenbabys.dingdingtimestatistics.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import com.chenbabys.dingdingtimestatistics.base.BaseViewModel
import com.chenbabys.dingdingtimestatistics.ui.model.TokenRequest
import com.chenbabys.dingdingtimestatistics.ui.model.TokenResult
import com.chenbabys.dingdingtimestatistics.ui.repository.MainRepository

/**
 * Created by ChenYesheng On 2021/10/28 17:42
 * Desc:
 */
class SplashVM :BaseViewModel(){
    private val repository = MainRepository()
    val tokenLiveData = MutableLiveData<TokenResult>()
    private val appId = "dingoaymyqysn7ydjo1ywj"
    private val appSecret = "ciIjrnbO3nPdVnbzIFqRr6Wiu3HA6MClsBaOYCqVcqtKvAlZruBZ3rNKzmdXxt4k"



    fun getUserToken(){
        launch({repository.getUserToken(
            TokenRequest(clientId = appId,clientSecret = appSecret,grantType = "authorization_code")
        )},tokenLiveData,isShowError = true,isShowLoading = true)
    }



}