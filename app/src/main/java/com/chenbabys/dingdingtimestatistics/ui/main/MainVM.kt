package com.chenbabys.dingdingtimestatistics.ui.main

import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.LogUtils
import com.chenbabys.dingdingtimestatistics.base.BaseViewModel
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
    var isNeedScroll2CurrentDatePosition = true//默认每次打开程序是需要
    //当前tab选中的月份
    var mCurrentShowDataMonth = CalenderUtil.getThisMonth()//默认给当前月份


    /**
     * 获取日历
     */
    fun getCalendarEntities() {
        val thisMonth = CalenderUtil.getThisMonth()//当前月
        dateList = if (thisMonth != CacheUtil.getMonth()) {//如果月份不同了就更新
            CacheUtil.setMonth(thisMonth)//同步当前月
            //清除掉上上上个月的旧数据，只保留三个月（当前月和上月以及上上月）
            CacheUtil.removeDdtsCache(thisMonth.minus(3))
            CalenderUtil.getDateEntities()
        } else {
            //如果是空的则还是填充获取日历的，否则才给缓存的
            if (CacheUtil.getDdtsCache(thisMonth).isNullOrEmpty()) {
                //默认是当前系统时间的所在月份日历数据
                CalenderUtil.getDateEntities()
            } else {
                CacheUtil.getDdtsCache(thisMonth)
            }
        }
        dateListChange.value = true
    }

    /**
     * 选择相应月份的数据，并刷新数据
     */
    fun chooseMonthEntities(month: Int){
        dateList.clear()
        dateList.addAll(CacheUtil.getDdtsCache(month))
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