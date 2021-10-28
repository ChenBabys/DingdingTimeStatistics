package com.chenbabys.dingdingtimestatistics.ui.model

import com.google.gson.annotations.SerializedName

/**
 * Created by ChenYesheng On 2021/10/28 17:32
 * Desc:
 */
data class TokenRequest(
    @SerializedName("clientId") val clientId: String? = null,
    @SerializedName("clientSecret") val clientSecret: String? = null,
    @SerializedName("code") val code: String? = null,
    @SerializedName("grantType") val grantType: String? = null,
    @SerializedName("refreshToken") val refreshToken: String? = null
)