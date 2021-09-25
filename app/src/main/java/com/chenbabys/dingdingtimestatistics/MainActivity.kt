package com.chenbabys.dingdingtimestatistics

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bigkoo.pickerview.builder.TimePickerBuilder
import com.bigkoo.pickerview.view.TimePickerView
import com.blankj.utilcode.util.*
import com.chenbabys.dingdingtimestatistics.base.BaseActivity
import com.chenbabys.dingdingtimestatistics.databinding.ActivityMainBinding
import com.chenbabys.dingdingtimestatistics.ui.main.DateEntity
import com.chenbabys.dingdingtimestatistics.ui.main.MainListAdapter
import com.chenbabys.dingdingtimestatistics.ui.viewmodel.MainVM
import com.chenbabys.dingdingtimestatistics.util.CacheUtil
import com.chenbabys.dingdingtimestatistics.util.CalenderUtil
import com.chenbabys.dingdingtimestatistics.util.DialogUtils
import com.pgyer.pgyersdk.PgyerSDKManager
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
                //todo 这里直接拿的数组的，没有使用上面的item,不止为何用item的话导致一些无数据的项会有一个错乱的问题，也会有显示dayWorkHour不空或者不为0.0f
                val currentItem = viewModel.dateList[position]//仅用于下面的判断。填充时候不用他，因为只有它不会影响到无数据的项。
                if (currentItem.dayWorkHour != CacheUtil.defaultFloat && currentItem.dayWorkHour != null) {
                    DialogUtils.showMoreDialog(
                        mutableListOf(if (item.vacation == null || item.vacation == CacheUtil.defaultFloat) "添加请假时间" else "修改请假时间"),
                        textView, listener = {
                            DialogUtils.showInputHourDialog(mContext, onConfirmClick = {
                                if (it <= item.dayWorkHour!!) {
                                    item.vacation = it
                                    viewModel.dateListChange.value = true
                                } else {
                                    ToastUtils.showShort("请假时间不能超过今天总工时~")
                                }
                            }, onShowOrHideListener = { show ->
                                if (!show) {
                                    KeyboardUtils.hideSoftInput(this)
                                }
                            })
                        }, -40f, -15f
                    )
                }
            } else {//普通的其他操作
                showTimePicker(item, isStartTime, position)
            }
        }, onTextRemoveListener = {
            viewModel.dateListChange.value = true
        })
    }

    override fun initView() {
        //蒲公英的检测更新
        PgyerSDKManager.checkSoftwareUpdate(this)
        //测试,可以直接填充上月工时
        //CacheUtil.setLastMonthHours(254.01666f)
        with(binding) {
            //给状态栏的占用重新定义高度
            BarUtils.addMarginTopEqualStatusBarHeight(titleBar.placeHolderView)
            //rv的样式初始化和设置适配器
            rvContent.layoutManager = LinearLayoutManager(mContext)
            rvContent.adapter = adapter
            //给统计的文字加个点击时动画
            ClickUtils.applyPressedViewAlpha(tvCountTotal)
            ClickUtils.applyPressedViewScale(tvCountTotal)
        }
        //获取日历数据
        viewModel.getCalendarEntities()
        //初始化动态时间
        initTime()
    }

    /**
     * 同步系统时间展示
     */
    private fun initTime() {
        val timeStr = format.format(Date(System.currentTimeMillis()))
        SpanUtils.with(binding.titleBar.tvTitle)
            .append(CalenderUtil.getWeekCurrent())
            .setFontSize(16,true)
            .appendSpace(20)
            .append(timeStr)
            .setFontSize(22,true)
            .create()
        //延时1s从新赋值后又开始延时，周而复始
        Handler(mainLooper).postDelayed({
            initTime()
        },1000)
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
                if (pos + 1 == data.day) {
                        //这种方式是最准确的，并且微信的根据字母跳转也是用这种方式
                        //第一个参数是跳转到指定下标，第二个是偏移多少像素
                    (binding.rvContent.layoutManager as LinearLayoutManager)
                        .scrollToPositionWithOffset(pos,(ScreenUtils.getScreenHeight() * 0.4).toInt())
                }
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
    @SuppressLint("SimpleDateFormat")
    private val format = SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss")

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
            val tvTitle = view?.findViewById<TextView>(R.id.tvTitle)
            tvTitle?.text = if (isStartTime) "选择上班时间" else "选择下班时间"
            tvSubmit?.setOnClickListener {
                pvTime?.returnData()
                pvTime?.dismiss()
            }
            tvCancel?.setOnClickListener {
                pvTime?.dismiss()
            }
        }.setType(booleanArrayOf(false, false, false, true, true, false)) // 默认全部显示
            .setContentTextSize(20)
            .setItemVisibleCount(5)
            .setDate(lastChooseCalendar)//设置上次时间
            .setLabel("年", "月", "日", "时", "分", "秒")
            .setLineSpacingMultiplier(2.0f)
            .setTextXOffset(0, 0, 0, 40, 0, -40)
            .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
            .setDividerColor(ContextCompat.getColor(mContext, R.color.gray_lite)).build()
        pvTime?.show()
    }


    /**
     * 活动销毁
     */
    override fun onDestroy() {
        super.onDestroy()
        PgyerSDKManager.reportException(Exception("销毁一次主应用，本次上传仅为做记录测试"))
        pvTime = null
    }

}