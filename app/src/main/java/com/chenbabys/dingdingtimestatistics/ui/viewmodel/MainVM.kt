package com.chenbabys.dingdingtimestatistics.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.chenbabys.dingdingtimestatistics.base.BaseViewModel
import com.chenbabys.dingdingtimestatistics.ui.main.DateEntity
import com.chenbabys.dingdingtimestatistics.util.CacheUtil
import com.chenbabys.dingdingtimestatistics.util.CalenderUtil
import java.util.*

/**
 * Created by ChenYesheng On 2021/7/26 16:02
 * Desc:
 */
class MainVM : BaseViewModel() {
    val dateListChange = MutableLiveData<Boolean>()
    var dateList = mutableListOf<DateEntity>()
    val lastMonthChange = MutableLiveData<Boolean>()
    var isNeedScroll2CurrentDatePosition = true//默认每次打开程序是需要


    /**
     * 获取日历
     */
    fun getCalendarEntities() {
        dateList = if (CalenderUtil.getThisMonth() != CacheUtil.getMonth()) {//如果月份不同了就更新
            CacheUtil.setMonth(CalenderUtil.getThisMonth())//同步当前月
            val justNowLastMonthHours = CacheUtil.getCurrentSaveMonthHours()
            CacheUtil.setLastMonthHours(justNowLastMonthHours)
            CacheUtil.removeDdtsCache()//清除掉所有的旧数据
            CalenderUtil.getDateEntities()
        } else {
            if (CacheUtil.getDdtsCache().isNullOrEmpty()) {//如果是空的则还是填充获取日历的，否则才给缓存的
                CalenderUtil.getDateEntities()
            } else {
                CacheUtil.getDdtsCache()
            }
        }
        lastMonthChange.value = true//通知刷新上个月时间，如果不空则会显示
        dateListChange.value = true
    }

    /**
     * 获取上次选择的日期
     */
    fun getLastTimeChooseTime(item: DateEntity, isStartTime: Boolean): Calendar {
        val lastCalendar = Calendar.getInstance()
        (if (isStartTime) item.startTime else item.endTime)?.let {
            if (it.contains(".")) {
                val startTimeList = it.split(".")
                lastCalendar.set(Calendar.HOUR_OF_DAY, startTimeList[0].toInt())
                lastCalendar.set(Calendar.MINUTE, startTimeList[1].toInt())
            } else if (it.contains(":")) {
                val startTimeList = it.split(":")
                lastCalendar.set(Calendar.HOUR_OF_DAY, startTimeList[0].toInt())
                lastCalendar.set(Calendar.MINUTE, startTimeList[1].toInt())
            }
        }
        return lastCalendar
    }

}