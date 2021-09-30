package com.chenbabys.dingdingtimestatistics.base

import android.os.Bundle
import android.os.PersistableBundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.blankj.utilcode.util.BarUtils
import com.jiuxiaoer.ctms.api.error.ErrorResult
import java.lang.reflect.ParameterizedType

abstract class BaseActivity<VM: BaseViewModel,VB: ViewBinding>() :AppCompatActivity(),RequestLifecycle,BaseInit{
    lateinit var mContext: FragmentActivity
    lateinit var viewModel: VM
    lateinit var binding: VB
    val TAG = javaClass.name


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //透明状态栏，它也会把状态栏的原来高度删除掉，底下的布局会并上去
        BarUtils.transparentStatusBar(this)
        //高亮状态栏
        BarUtils.setStatusBarLightMode(this,true)
        //注意 type.actualTypeArguments[0]=BaseViewModel，type.actualTypeArguments[1]=ViewBinding
        val type =javaClass.genericSuperclass as ParameterizedType
        val clazz1 = type.actualTypeArguments[0] as Class<VM>
        viewModel  = ViewModelProvider(this).get(clazz1)

        val clazz2 = type.actualTypeArguments[1] as Class<VB>
        val method =clazz2.getMethod("inflate", LayoutInflater::class.java)
        binding = method.invoke("",layoutInflater) as VB
        setContentView(binding.root)
        mContext = this
        init()
    }
    private fun init(){
        initView()
        initVm()
        //loading
        viewModel.loadingMessage.observe(this, Observer {
            if (it.isShowLoading) showLoading(it.message) else dismissLoading()
        })
        //错误信息
        viewModel.errorData.observe(this, Observer {
            //if (it.show) OnlyToast.showToast(mContext, it.errMsg)
            errorResult(it)
        })
    }
    open fun showLoading(message: String) {

    }

    open fun dismissLoading() {

    }

    /**
     * 接口请求错误回调
     */
    open fun errorResult(errorResult: ErrorResult) {
        dismissLoading()
    }

    override fun startLoading() {

    }

    override fun loadFinished() {

    }

    override fun loadFailed(msg: String?) {

    }


}