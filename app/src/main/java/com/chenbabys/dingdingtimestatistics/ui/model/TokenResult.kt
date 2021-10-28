package com.chenbabys.dingdingtimestatistics.ui.model

/**
 * Created by ChenYesheng On 2021/10/28 17:33
 * Desc:
 */
data class TokenResult(
    val accessToken: String? = null,
    val corpId: String? = null,
    val expireIn: Int? = null,
    val refreshToken: String? = null
)