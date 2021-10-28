package com.chenbabys.dingdingtimestatistics.ui.repository

import com.chenbabys.dingdingtimestatistics.http.HttpUtil
import com.chenbabys.dingdingtimestatistics.ui.model.TokenRequest

/**
 * Created by ChenYesheng On 2021/10/28 17:24
 * Desc:
 */
class MainRepository {

    suspend fun getUserToken(request: TokenRequest) =
        HttpUtil.getInstance().apiService().getUserToken(request)
}