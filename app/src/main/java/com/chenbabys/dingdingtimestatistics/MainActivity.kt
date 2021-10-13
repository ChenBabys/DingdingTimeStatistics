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
import com.chenbabys.dingdingtimestatistics.ui.main.MainVM
import com.chenbabys.dingdingtimestatistics.util.CacheUtil
import com.chenbabys.dingdingtimestatistics.util.CalenderUtil
import com.chenbabys.dingdingtimestatistics.util.DialogUtils
import com.chenbabys.dingdingtimestatistics.util.MethodUnit
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
                // 这里直接拿的数组的，没有使用上面的item,不止为何用item的话导致一些无数据的项会有一个错乱的问题，也会有显示dayWorkHour不空或者不为0.0f
                val currentItem = viewModel.dateList[position]//仅用于下面的判断。填充时候不用他，因为只有它不会影响到无数据的项。
                if (currentItem.dayWorkHour != CacheUtil.defaultFloat && currentItem.dayWorkHour != null) {
                    DialogUtils.showMoreChooseDialog(
                        mutableListOf(if (item.vacation == null || item.vacation
                            == CacheUtil.defaultFloat) "添加请假时间" else "修改请假时间","标记休息日加班"),
                        textView, listener = { pos,text->
                            when(pos){
                                0-> {
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
                                }
                                1-> {
                                    item.isRestWorking = true
                                    viewModel.dateListChange.value = true
                                }
                            }
                        }, -32f, -15f
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
        //下面这段代码只有第一次打开新版本的时候有作用(属于一个适配旧数据的代码，于主流程没有关系，
        // 如果不需要适配旧key数据,或者说不需要保留当前月之前选择过的数据则可以直接不要)
        //-----------------开始-----------------------///
        if (CacheUtil.getOldDdtsCache().isNotEmpty()){
            val thisMonth = CalenderUtil.getThisMonth()
            //把当前月的旧数据存到新数据key中
            CacheUtil.setDdtsCache(thisMonth,CacheUtil.getOldDdtsCache())
            CacheUtil.removeOldDdtsCache()//删除掉OldDdtsCache的数据，从此不会再起作用
        }
        //-----------------结束-----------------------///

        with(binding) {
            //给状态栏的占用重新定义高度
            BarUtils.addMarginTopEqualStatusBarHeight(titleBar.placeHolderView)
            //rv的样式初始化和设置适配器
            rvContent.layoutManager = LinearLayoutManager(mContext)
            rvContent.adapter = adapter
            //给统计的文字加个点击时动画
            ClickUtils.applyPressedViewAlpha(tvCountTotal)
            ClickUtils.applyPressedViewScale(tvCountTotal)
            setFiltrateTitle(CalenderUtil.getThisMonth())//填充当前月为筛选标题
            //顶部的月份筛选点击事件
            titleBar.llFiltrate.setOnClickListener{
                val thisMonth = CalenderUtil.getThisMonth()
                val lastMonth = CalenderUtil.getThisMonth().minus(1)//减去1
                val lastInLastMonth = CalenderUtil.getThisMonth().minus(2)//减去2
                DialogUtils.showMoreChooseDialog(
                    mutableListOf("${lastInLastMonth}月工时", "${lastMonth}月工时","${thisMonth}月工时"),
                    titleBar.llFiltrate,listener = { pos,text ->
                        val monthInStrs = text.split("月")
                        val month = monthInStrs[0].toInt()
                        //临时保存当前选中的月份备用。
                        viewModel.mCurrentShowDataMonth = month
                        //根据当前月份选择数据
                        viewModel.chooseMonthEntities(month)
                        //设置筛选标题
                        setFiltrateTitle(month)
                }, 30f, -6f)
            }
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
        val timeStrArray = format.format(Date(System.currentTimeMillis())).split(" ")//用空格切割时间。
        SpanUtils.with(binding.titleBar.tvTitle)
            .append(CalenderUtil.getWeekCurrent())
            .setFontSize(24,true)
            .appendSpace(20)
            .append(timeStrArray[0])
            .setFontSize(14,true)
            .appendSpace(20)
            .append(timeStrArray[1])
            .setFontSize(21,true)
            .create()
        //延时1s从新赋值后又开始延时，周而复始
        Handler(mainLooper).postDelayed({
            initTime()
        },1000)
    }


    override fun initVm() {
        viewModel.dateListChange.observe(this, Observer {
            adapter.setList(viewModel.dateList)
            if (adapter.hasEmptyView()) adapter.removeEmptyView()
            if (viewModel.dateList.isEmpty()){
                adapter.setEmptyView(R.layout.layout_empty_no_data)
            }
            if (viewModel.isNeedScroll2CurrentDatePosition) {
                Handler(Looper.getMainLooper()).postDelayed({ scroll2TodayPos() }, 100)
                viewModel.isNeedScroll2CurrentDatePosition = false//跳转后本次使用不再有效
            }
        })
        //所有视图可见时调用(必须在视图可见完成后或者用延时去统计，否则出现统计不准确不完全的问题~)
        adapter.recyclerView.viewTreeObserver.addOnGlobalLayoutListener {
            totalCount()//统计
        }
    }

    /**
     * 设置筛选的月份标题
     */
    private fun setFiltrateTitle(month:Int?){
        SpanUtils.with(binding.titleBar.tvFiltrate)
            .append("$month")
            .setFontSize(25,true)
            .appendSpace(10)
            .append("月")
            .setFontSize(16,true)
            .create()
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
        var totalHour = 0.0f//总工时
        //总加班时间,超过7小时并且再满三小时算加班（还得去除吃饭时间0.5），
        // 所以是上班到晚上9.30及以上算加班
        var overTime = 0.0f
        viewModel.dateList.forEach {
            it.dayWorkHour?.let { hour ->
                totalHour += hour
                overTime +=
//                    if (it.isWeekEnd()) hour else {
                    if (hour > 7) hour -7 else 0f
//                }
            }
        }
        binding.tvCountTotal.text = ("本月打卡统计时间为：${MethodUnit.formatNumberTwoDigits(totalHour)}小时")
        if (overTime == CacheUtil.defaultFloat){
            binding.tvLastMonthCountTotal.visibility  = View.GONE
        }else{
            binding.tvLastMonthCountTotal.text = ("本月加班时长：${MethodUnit.formatNumberTwoDigits(overTime)}小时")
            binding.tvLastMonthCountTotal.visibility  = View.VISIBLE
        }
        //统计完之后根据当前tab选择的月份保存相关数据到本地cache
        CacheUtil.setDdtsCache(viewModel.mCurrentShowDataMonth,viewModel.dateList)
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