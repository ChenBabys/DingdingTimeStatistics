package com.chenbabys.dingdingtimestatistics.http

import com.chenbabys.dingdingtimestatistics.base.BaseResult
import com.chenbabys.dingdingtimestatistics.ui.model.TokenRequest
import com.chenbabys.dingdingtimestatistics.ui.model.TokenResult
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST(UrlConstant.USER_ACCESS_TOKEN)
    suspend fun getUserToken(@Body request: TokenRequest):BaseResult<TokenResult>

}