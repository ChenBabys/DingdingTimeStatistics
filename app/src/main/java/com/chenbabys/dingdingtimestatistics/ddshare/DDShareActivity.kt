package com.chenbabys.dingdingtimestatistics.ddshare

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.android.dingtalk.share.ddsharemodule.IDDAPIEventHandler
import com.android.dingtalk.share.ddsharemodule.message.BaseReq
import com.android.dingtalk.share.ddsharemodule.message.BaseResp
import com.chenbabys.dingdingtimestatistics.R
import android.widget.Toast

import com.android.dingtalk.share.ddsharemodule.message.SendAuth

import com.android.dingtalk.share.ddsharemodule.ShareConstant

import android.util.Log
import android.widget.Button
import com.android.dingtalk.share.ddsharemodule.DDShareApiFactory
import com.android.dingtalk.share.ddsharemodule.IDDShareApi


class DDShareActivity : AppCompatActivity(), IDDAPIEventHandler {
    private val appId = "dingoaymyqysn7ydjo1ywj"
    private lateinit var iddShareApi: IDDShareApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ddshare)
        try {
            //todo: Constant.APP_ID 需要更新为用户测试的app_id
            iddShareApi = DDShareApiFactory.createDDShareApi(this, appId, true)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("lzc", "e===========>$e")
        }
        findViewById<Button>(R.id.btnConfirm).setOnClickListener {
            sendAuth()
        }
    }

    private fun sendAuth() {
        System.out.println(iddShareApi.ddSupportAPI)
        val req = SendAuth.Req()
        req.scope = SendAuth.Req.SNS_LOGIN
        req.state = "test"
        if (req.supportVersion > iddShareApi.ddSupportAPI) {
            Toast.makeText(this, "${req.supportVersion}--${iddShareApi.ddSupportAPI}" +
                    "\n钉钉版本过低，不支持登录授权,或版本获取失败，请检查是否有appid授权（创建了app才有）", Toast.LENGTH_SHORT).show()
            return
        }
        iddShareApi.sendReq(req)
    }


    override fun onReq(p0: BaseReq?) {

    }

    override fun onResp(baseResp: BaseResp?) {
        val errCode: Int = baseResp?.mErrCode!!
        println(errCode)
        Log.d("lzc", "errorCode==========>$errCode")
        val errMsg: String = baseResp.mErrStr
        Log.d("lzc", "errMsg==========>$errMsg")
        if (baseResp.getType() == ShareConstant.COMMAND_SENDAUTH_V2 && baseResp is SendAuth.Resp) {
            val authResp = baseResp as SendAuth.Resp
            when (errCode) {
                BaseResp.ErrCode.ERR_OK -> Toast.makeText(
                    this,
                    "授权成功，授权码为:" + authResp.code,
                    Toast.LENGTH_SHORT
                ).show()
                BaseResp.ErrCode.ERR_USER_CANCEL -> Toast.makeText(this, "授权取消", Toast.LENGTH_SHORT)
                    .show()
                else -> Toast.makeText(this, "授权异常" + baseResp.mErrStr, Toast.LENGTH_SHORT).show()
            }
        } else {
            when (errCode) {
                BaseResp.ErrCode.ERR_OK -> Toast.makeText(this, "分享成功", Toast.LENGTH_SHORT).show()
                BaseResp.ErrCode.ERR_USER_CANCEL -> Toast.makeText(this, "分享取消", Toast.LENGTH_SHORT)
                    .show()
                else -> Toast.makeText(this, "分享失败" + baseResp.mErrStr, Toast.LENGTH_SHORT).show()
            }
        }
        finish()
    }
}