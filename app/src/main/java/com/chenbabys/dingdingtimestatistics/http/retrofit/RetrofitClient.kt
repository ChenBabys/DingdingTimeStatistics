package com.jiuxiaoer.ctms.api.retrofit

import com.blankj.utilcode.util.Utils
import com.chenbabys.dingdingtimestatistics.http.ApiService
import com.chenbabys.dingdingtimestatistics.http.UrlConstant
import com.jiuxiaoer.ctms.api.interceptor.LoggingInterceptor
import com.franmontiel.persistentcookiejar.PersistentCookieJar
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.X509TrustManager
import kotlin.jvm.Throws

class RetrofitClient {

    companion object {
        fun getInstance() =
            SingletonHolder.INSTANCE

        private lateinit var retrofit: Retrofit
    }

    private object SingletonHolder {
        val INSTANCE = RetrofitClient()
    }

    private var cookieJar: PersistentCookieJar = PersistentCookieJar(
        SetCookieCache(),
        SharedPrefsCookiePersistor(Utils.getApp())
    )

    init {
        retrofit = Retrofit.Builder()
            .client(getOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(UrlConstant.BASE_URL)
            .build()
    }

    private fun getOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .cookieJar(cookieJar)
            .addInterceptor(LoggingInterceptor())
            .sslSocketFactory(SSLContextSecurity.createIgnoreVerifySSL("TLS"),
                MyX509TrustManager()
            )
            .build()
    }

    fun create(): ApiService = retrofit.create(
        ApiService::class.java
    )

    fun <T>  createService(classT:Class<T>):T = retrofit.create(classT)

    class MyX509TrustManager : X509TrustManager {
        @Throws(CertificateException::class)
        override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
        }

        @Throws(CertificateException::class)
        override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
        }

        override fun getAcceptedIssuers(): Array<X509Certificate> {
            return arrayOf()
        }
    }
}