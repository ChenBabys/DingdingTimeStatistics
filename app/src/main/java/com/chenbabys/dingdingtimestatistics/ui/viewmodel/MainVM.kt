package com.chenbabys.dingdingtimestatistics.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.LogUtils
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


    /**
     * 获取日历
     */
    fun getCalendarEntities() {
        dateList = if (CalenderUtil.getCalenderInOne()) {//如果在当月的一号
            //如果是是一号了，就把当前保存的一号前刚刚过去的那个月的总工时保存起来
            val justNowLastMonthHours = CacheUtil.getCurrentSaveMonthHours()
            CacheUtil.setLastMonthHours(justNowLastMonthHours)
            lastMonthChange.value = true//通知上月时间变化了
            CacheUtil.removeDdtsCache()//清除掉所有的旧数据
            CalenderUtil.getDateEntities()
        } else {
            if (CacheUtil.getDdtsCache().isNullOrEmpty()) {//如果是空的则还是填充获取日历的，否则才给缓存的
                CalenderUtil.getDateEntities()
            } else {
                CacheUtil.getDdtsCache()
            }
        }
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