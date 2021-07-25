package com.chenbabys.dingdingtimestatistics

import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.LogUtils
import com.chenbabys.dingdingtimestatistics.base.BaseActivity
import com.chenbabys.dingdingtimestatistics.base.BaseViewModel
import com.chenbabys.dingdingtimestatistics.databinding.ActivityMainBinding
import com.chenbabys.dingdingtimestatistics.ui.main.MainListAdapter
import com.chenbabys.dingdingtimestatistics.util.CalenderUtil

/**
 *
 */
class MainActivity : BaseActivity<BaseViewModel, ActivityMainBinding>() {
    private val adapter by lazy {
        MainListAdapter {
            totalCount()
        }
    }

    override fun initView() {
        title = "打卡统计（本月是：${CalenderUtil.getThisMonth()}月）"
        with(binding) {
            rvContent.layoutManager = LinearLayoutManager(mContext)
            rvContent.adapter = adapter
        }
        val dateList = CalenderUtil.getDateEntities()
        adapter.setList(dateList)
    }

    override fun initVm() {

    }

    /**
     * 做时间统计
     */
    private fun totalCount() {
        var totalHour: Long = 0
        adapter.data.forEach {
            it.dayWorkHour?.let { hour ->
                totalHour += hour
            }
        }
        binding.tvCountTotal.text = "本月打卡统计时间为：${totalHour}小时"
    }
}