package com.chenbabys.dingdingtimestatistics

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bigkoo.pickerview.builder.TimePickerBuilder
import com.bigkoo.pickerview.view.TimePickerView
import com.blankj.utilcode.util.ToastUtils
import com.chenbabys.dingdingtimestatistics.base.BaseActivity
import com.chenbabys.dingdingtimestatistics.databinding.ActivityMainBinding
import com.chenbabys.dingdingtimestatistics.ui.main.DateEntity
import com.chenbabys.dingdingtimestatistics.ui.main.MainListAdapter
import com.chenbabys.dingdingtimestatistics.ui.viewmodel.MainVM
import com.chenbabys.dingdingtimestatistics.util.CacheUtil
import com.chenbabys.dingdingtimestatistics.util.CalenderUtil
import com.chenbabys.dingdingtimestatistics.util.DialogUtils
import java.text.SimpleDateFormat
import java.util.*

/**
 *主页
 */
class MainActivity : BaseActivity<MainVM, ActivityMainBinding>() {
    private val adapter by lazy {
        //textView当前点击的textview,item,当前项的相关数据和字段，position当前下标，isStartTime当前是否是选择的是添加上班时间
        //isModifyTotal:是否是修改工时
        MainListAdapter(onTextViewClickListener = { textView, item, position, isStartTime, isModifyTotal ->
            if (isModifyTotal) {//是添加请假的操作
                DialogUtils.showMoreDialog(
                    mutableListOf(if (item.vacation != null) "修改请假时间" else "添加请假时间"),
                    textView, listener = {
                        DialogUtils.showInputHourDialog(mContext, onConfirmClick = {
                            if (it <= item.dayWorkHour!!) {
                                item.vacation = it
                                viewModel.dateListChange.value = true
                            } else {
                                ToastUtils.showShort("请假时间不能超过今天总工时~")
                            }
                        })
                    }, -40f, -15f
                )
            } else {//普通的其他操作
                showTimePicker(item, isStartTime, position)
            }
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
            if (viewModel.isNeedScroll2CurrentDatePosition) {
                Handler(Looper.getMainLooper()).postDelayed({ scroll2TodayPos() }, 100)
                viewModel.isNeedScroll2CurrentDatePosition = false//跳转后本次使用不再有效
            }
        })
        //所有视图可见时调用(必须在视图可见完成后或者用延时去统计，否则出现统计不准确不完全的问题~)
        adapter.recyclerView.viewTreeObserver.addOnGlobalLayoutListener {
            totalCount()//统计
        }
        //上月时间变化
        viewModel.lastMonthChange.observe(this, Observer {
            if (CacheUtil.getLastMonthHours() == CacheUtil.defaultFloat) {
                binding.tvLastMonthCountTotal.visibility = View.GONE
            } else {
                binding.tvLastMonthCountTotal.visibility = View.VISIBLE
                binding.tvLastMonthCountTotal.text = ("上月总工时：${CacheUtil.getLastMonthHours()}小时")
            }
        })
    }

    /**
     * 跳转到“今天”的下标
     */
    private fun scroll2TodayPos() {
        adapter.data.forEach { data -> //遍历查找当前日期所在的下标,而后跳转到指定下标
            data.isTodayPosition?.let { pos ->
                binding.rvContent.smoothScrollToPosition(pos)
            }
        }
    }

    /**
     * 做时间统计
     */
    private fun totalCount() {
        var totalHour = 0.0f
        viewModel.dateList.forEach {
            it.dayWorkHour?.let { hour ->
                totalHour += hour
            }
        }
        binding.tvCountTotal.text = ("本月打卡统计时间为：${totalHour}小时")
        //统计完之后保存到本地cache
        CacheUtil.setDdtsCache(viewModel.dateList)
        //保存为当前本月总工时,到下月一号的在删除本月数据前去保存为上月总工时~
        CacheUtil.setThisMonthHours(totalHour)
    }

    private var pvTime: TimePickerView? = null

    @SuppressLint("SimpleDateFormat")
    private val formatTime = SimpleDateFormat("HH:mm")

    /**
     * 显示时间选择器
     * https://github.com/Bigkoo/Android-PickerView
     * item：适配器中正在点击操作的项的相关字段和数据
     * isStartTime：是否是开始（上班）时间
     * position：正在操作的下标
     * isModifyTotal:是否是在修改工时统计项
     */
    private fun showTimePicker(item: DateEntity, isStartTime: Boolean, position: Int) {
        val lastChooseCalendar = viewModel.getLastTimeChooseTime(item, isStartTime)
        pvTime = TimePickerBuilder(mContext) { date, v ->
            when (isStartTime) {
                true -> {//HH:mm:ss,h大写代表是24小时制，小写反之
                    item.startTime = formatTime.format(date)//方法一
                    //viewModel.dateList[position].startTime = formatTime.format(date)//方法二
                }
                false -> {
                    item.endTime = formatTime.format(date)//方法一
                    //viewModel.dateList[position].endTime = formatTime.format(date)//方法二
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
        }.setType(booleanArrayOf(false, false, false, true, true, false)) // 默认全部显示
            .setContentTextSize(18)
            .setDate(lastChooseCalendar)//设置上次时间
            .setLabel("年", "月", "日", "时", "分", "秒")
            .setLineSpacingMultiplier(1.2f)
            .setTextXOffset(0, 0, 0, 40, 0, -40)
            .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
            .setDividerColor(ContextCompat.getColor(mContext, R.color.gray)).build()
        pvTime?.show()
    }


    /**
     * 活动销毁
     */
    override fun onDestroy() {
        super.onDestroy()
        pvTime = null
    }

}