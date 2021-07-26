package com.chenbabys.dingdingtimestatistics

import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.SpanUtils
import com.chenbabys.dingdingtimestatistics.base.BaseActivity
import com.chenbabys.dingdingtimestatistics.base.BaseViewModel
import com.chenbabys.dingdingtimestatistics.databinding.ActivityMainBinding
import com.chenbabys.dingdingtimestatistics.ui.main.MainListAdapter
import com.chenbabys.dingdingtimestatistics.ui.viewmodel.MainVM
import com.chenbabys.dingdingtimestatistics.util.CacheUtil
import com.chenbabys.dingdingtimestatistics.util.CalenderUtil

/**
 *主页
 */
class MainActivity : BaseActivity<MainVM, ActivityMainBinding>() {
    private val adapter by lazy {
        MainListAdapter {
            viewModel.dateListChange.value = true
        }
    }

    override fun initView() {
        title = "打卡统计（本月是：${CalenderUtil.getThisMonth()}月）"
        with(binding) {
            rvContent.layoutManager = LinearLayoutManager(mContext)
            rvContent.adapter = adapter
        }
        //获取日历数据
        viewModel.getCalendarEntities()
    }

    override fun initVm() {
        viewModel.dateListChange.observe(this, Observer {
            adapter.setList(viewModel.dateList)
            totalCount()//统计
        })
    }

    /**
     * 做时间统计
     */
    private fun totalCount() {
        var totalHour: Float = 0.0f
        viewModel.dateList.forEach {
            it.dayWorkHour?.let { hour ->
                totalHour += hour
            }
        }
        SpanUtils.with(binding.tvCountTotal)
            .append("(输入后回车键统计)")
            .setForegroundColor(ContextCompat.getColor(mContext, R.color.qing))
            .appendLine()
            .append("本月打卡统计时间为：${totalHour}小时").create()
        //统计完之后保存到本地cache
        LogUtils.d("实体",viewModel.dateList.toString())

        CacheUtil.setDdtsCache(viewModel.dateList)
    }
}