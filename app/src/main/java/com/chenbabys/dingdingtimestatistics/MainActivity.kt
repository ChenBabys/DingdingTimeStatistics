package com.chenbabys.dingdingtimestatistics

import android.annotation.SuppressLint
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bigkoo.pickerview.builder.TimePickerBuilder
import com.bigkoo.pickerview.view.TimePickerView
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.SpanUtils
import com.chenbabys.dingdingtimestatistics.base.BaseActivity
import com.chenbabys.dingdingtimestatistics.base.BaseViewModel
import com.chenbabys.dingdingtimestatistics.databinding.ActivityMainBinding
import com.chenbabys.dingdingtimestatistics.ui.main.DateEntity
import com.chenbabys.dingdingtimestatistics.ui.main.MainListAdapter
import com.chenbabys.dingdingtimestatistics.ui.viewmodel.MainVM
import com.chenbabys.dingdingtimestatistics.util.CacheUtil
import com.chenbabys.dingdingtimestatistics.util.CalenderUtil
import java.text.SimpleDateFormat

/**
 *主页
 */
class MainActivity : BaseActivity<MainVM, ActivityMainBinding>() {
    private val adapter by lazy {
        //textView当前点击的textview,item,当前项的相关数据和字段，position当前下标，isStartTime当前是否是选择的是添加上班时间
        MainListAdapter(onTextViewClickListener = { textView, item, position, isStartTime ->
            showTimePicker(item, isStartTime)
        }, onTextRemoveListener = {
            viewModel.dateListChange.value = true
        })
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
            .append("(长按方格可以清除时间)")
            .setForegroundColor(ContextCompat.getColor(mContext, R.color.qing))
            .appendLine()
            .append("本月打卡统计时间为：${totalHour}小时").create()
        //统计完之后保存到本地cache
        CacheUtil.setDdtsCache(viewModel.dateList)
    }

    private var pvTime: TimePickerView? = null

    @SuppressLint("SimpleDateFormat")
    private val formatTime = SimpleDateFormat("HH.mm.ss")

    /**
     * 显示时间选择器
     * https://github.com/Bigkoo/Android-PickerView
     */
    private fun showTimePicker(item: DateEntity, isStartTime: Boolean) {
        pvTime = TimePickerBuilder(mContext) { date, v ->
            when (isStartTime) {
                true -> {//HH:mm:ss,h大写代表是24小时制，小写反之
                    item.startTime = formatTime.format(date)
                }
                false -> {
                    item.endTime = formatTime.format(date)
                }
            }
            viewModel.dateListChange.value = true

        }.setLayoutRes(R.layout.pickview_costom_time) { view ->
            val tvCancel = view?.findViewById<Button>(R.id.btnCancel)
            val tvSubmit = view?.findViewById<Button>(R.id.btnSubmit)
            tvSubmit?.setOnClickListener {
                pvTime?.returnData()
                pvTime?.dismiss()
            }
            tvCancel?.setOnClickListener {
                pvTime?.dismiss()
            }
        }.setType(booleanArrayOf(false, false, false, true, true, true)) // 默认全部显示
            .setContentTextSize(18).setLabel("年", "月", "日", "时", "分", "秒")
            .setLineSpacingMultiplier(1.2f)
            .setTextXOffset(0, 0, 0, 40, 0, -40)
            .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
            .setDividerColor(ContextCompat.getColor(mContext, R.color.gray)).build()
        pvTime?.show()
    }

}