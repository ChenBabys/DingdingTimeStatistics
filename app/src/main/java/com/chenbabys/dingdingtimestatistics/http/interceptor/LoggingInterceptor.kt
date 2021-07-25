package com.jiuxiaoer.ctms.api.interceptor

import com.chenbabys.dingdingtimestatistics.http.LogUtil.showHttpApiLog
import com.chenbabys.dingdingtimestatistics.http.LogUtil.showHttpHeaderLog
import com.chenbabys.dingdingtimestatistics.http.LogUtil.showHttpLog
import com.chenbabys.dingdingtimestatistics.http.UrlConstant
import okhttp3.Interceptor
import okhttp3.RequestBody
import okhttp3.Response
import okio.Buffer
import java.io.IOException
import java.net.URLDecoder
import kotlin.jvm.Throws


class LoggingInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val httpUrl = request.url
        val newBuilder = request.newBuilder()
//        newBuilder.addHeader("APP_VERSION",BuildConfig.VERSION_NAME)
//        newBuilder.addHeader("jxe-token", SettingUtil.getAccessToken())
//        newBuilder.addHeader("n-d-version", BuildConfig.N_D_VERSION)
       // newBuilder.addHeader("jxe-token", "eyJhbGciOiJIUzUxMiJ9.eyJ1c2VySWQiOjE0MDQ2NDA4MDU3MjA5NjkyMTcsImJ5IjoiMTgwNzc3OTU3NDgiLCJuYW1lIjoi5ZGo54Wc54SVIiwidHlwZSI6IkNUTVMiLCJleHAiOjE2MjM4MzQwNzV9.OdUoHoW3vgaDZv6uksN0y4eW3BF3TK4aVU1AC0psYXkjCQnD5pfhpABTu_R-IAu80kBAZkOaVMhOhcyu-PelhA")
//        newBuilder.addHeader("n-d-version",
//                "{\"v2-app-ctms\":\"1.0\",\"v2-common\":\"1.0\",\"v2-stock\":\"1.0\",\"v2-user\":\"1.0\",\"v2-goods\":\"1.0\",\"v2-channel\":\"1.0\",\"v2-delivery\":\"1.0\",\"v2-trade\":\"1.0\"}")
//        newBuilder.addHeader("n-d-version",
//                "{\"v2-user\":\"1.0\",\"v2-stock\":\"984567\",\"v2-delivery\":\"1.0\",\"v2-goods\":\"1.0\",\"v2-trade\":\"1.0\",\"v2-channel\":\"1.0\",\"v2-common\":\"1.0\",\"v2-count\":\"1.0\",\"v2-customer\":\"1.0\"}")
        val t1 = System.nanoTime() //请求发起的时间
        val response = chain.proceed(newBuilder.build())
        val t2 = System.nanoTime() //收到响应的时间
        if (response.code==401){
            //EventBus.getDefault().post(EventMessage(code = EventCode.LOGIN_OUT))
            return response
        }
        val responseBody = response.peekBody(1024 * 1024.toLong())
        if (httpUrl.toString().contains(".png")
            || httpUrl.toString().contains(".jpg")
            || httpUrl.toString().contains(".jpeg")
            || httpUrl.toString().contains(".gif")
        ) {
            return response
        }

        var api = httpUrl.toString().replace(UrlConstant.BASE_URL, "")
        if (api.contains("?")) {
            api = api.substring(0, api.indexOf("?"))
        }
        val result = responseBody.string()
        showHttpHeaderLog(
            String.format(
                "%n%s%n%s",
                " ",
                request.headers.toString()
            )
        )
        if (request.method == "POST" || request.method == "PUT") {
            if (api.contains("uploadPic") || api.contains("uploadFile")) showHttpApiLog(
                String.format(
                    "%s%n%s%n%s%n%s%n%s%n",
                    "请求URL>>$httpUrl",
                    "API>>$api",
                    "请求方法>>" + request.method,
                    "请求参数>>" + request.body.toString(),
                    "请求耗时>>" + String.format("%.1f", (t2 - t1) / 1e6) + "ms"
                )
            ) else showHttpApiLog(
                java.lang.String.format(
                    "%s%n%s%n%s%n%s%n%s%n",
                    "请求URL>>$httpUrl",
                    "API>>$api",
                    "请求方法>>" + request.method,
                    "请求参数>>" + URLDecoder.decode(bodyToString(request.body), "UTF-8"),
                    "请求耗时>>" + String.format("%.1f", (t2 - t1) / 1e6) + "ms"
                )
            )
        } else {
            showHttpApiLog(
                String.format(
                    "%s%n%s%n%s%n%s%n",
                    "请求URL>>$httpUrl",
                    "API>>$api",
                    "请求方法>>" + request.method,
                    "请求耗时>>" + String.format("%.1f", (t2 - t1) / 1e6) + "ms"
                )
            )
        }

        if (result.length > 4000) {
            val chunkCount = result.length / 4000 // integer division
            for (i in 0..chunkCount) {
                val max = 4000 * (i + 1)
                if (max >= result.length) {
                    showHttpLog(
                        String.format(
                            "%s%n%s%n%s%n",
                            "请求结果>>>" + result.substring(4000 * i),
                            " ",
                            " "
                        )
                    )
                } else {
                    showHttpLog(
                        String.format(
                            "%s%n%s%n%s%n",
                            "请求结果>>>" + result.substring(4000 * i, max),
                            " ",
                            " "
                        )
                    )
                }
            }
        } else {
            showHttpLog(String.format("%s%n%s%n%s%n", "请求结果>>>$result", " ", ""))
        }
        return response
    }

    fun bodyToString(request: RequestBody?): String? {
        return try {
            val buffer = Buffer()
            if (request != null) request.writeTo(buffer) else return ""
            buffer.readUtf8()
        } catch (e: IOException) {
            "did not work"
        }
    }
}