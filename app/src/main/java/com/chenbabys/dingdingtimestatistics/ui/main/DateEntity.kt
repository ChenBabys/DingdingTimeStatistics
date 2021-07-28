package com.chenbabys.dingdingtimestatistics.ui.main

import com.chenbabys.dingdingtimestatistics.util.CalenderUtil
import java.text.SimpleDateFormat
import java.util.*

data class DateEntity(
    var date: Date? = null,//具体日期
//    var day: String? = null,//一个月的某天
//    var monthStr: String? = null,//月份
    var dayOfWeek: Int? = null,//星期几
    //判断了时间和date字段相比较后，如果是今天把这个填充为时间上的今天的list数据下标
    var isTodayPosition: Int? = null,
    var vacation :Float?= null,//请假的时间
    var dayWorkHour: Float? = null,

    //在适配器中需要使用的字段
    var startTime: String? = null,//上班时间
    var endTime: String? = null,//下班时间

) {

    /**
     * 获取星期的格式化
     */
    fun getWeekFormat(): String {
        return when (dayOfWeek) {
            0 -> "星期天"
            1 -> "星期一"
            2 -> "星期二"
            3 -> "星期三"
            4 -> "星期四"
            5 -> "星期五"
            6 -> "星期六"
            else -> "不在范围"
        }
    }

    /**
     * 是否是周末：中国以周六日为周末
     */
    fun isWeekEnd(): Boolean {
        return dayOfWeek == 0 || dayOfWeek == 6
    }


    /**
     * 格式化成mm:dd日期
     * SimpleDateFormat的实例化不能放在实体和字段一起，否则做缓存的时候没法子保存抽象类（SimpleDateFormat）
     */
    fun getFotMatMonthDay(myDate: Date): String {
        val monthAndDayFormat = SimpleDateFormat("MM-dd")
        return monthAndDayFormat.format(myDate)
    }

    /**
     * 判断是否是当前时间的今天
     */
    fun isToday():Boolean{
       val currentDay = CalenderUtil.getCurrentDay()
       return date.toString().contains(currentDay)
    }

}