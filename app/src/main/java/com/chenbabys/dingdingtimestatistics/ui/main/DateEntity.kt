package com.chenbabys.dingdingtimestatistics.ui.main

import com.chenbabys.dingdingtimestatistics.util.CalenderUtil
import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.util.*

/**
 * 增加SerializedName序列化。避免混淆后Gson解析不到，造成空数据。
 * 但是这种解决办法有局限性，实体类多了就难以全部都序列化，特别是多人开发的项目。就很难统一。寻找其他办法解决更好。
 */
data class DateEntity(
    @SerializedName("date") var date: Date? = null,//具体日期
    @SerializedName("day") var day: Int? = null,//一个月的某天
    @SerializedName("month") var month: Int? = null,//月份
    @SerializedName("dayOfWeek") var dayOfWeek: Int? = null,//星期几
    //判断了时间和date字段相比较后，如果是今天把这个填充为时间上的今天的list数据下标
    @SerializedName("isTodayPosition") var isTodayPosition: Int? = null,
    @SerializedName("vacation") var vacation: Float? = null,//请假的时间
    @SerializedName("isRestWorking") var isRestWorking: Boolean = false,//是否是休息日加班
    @SerializedName("isWeeHours") var isWeeHours: Boolean = false,//是否是凌晨
    @SerializedName("dayWorkHour") var dayWorkHour: Float? = null,//当天总工时

    //在适配器中需要使用的字段
    @SerializedName("startTime") var startTime: String? = null,//上班时间
    @SerializedName("endTime") var endTime: String? = null,//下班时间

    //每周的休息方式，1单休，2双休
    @SerializedName("weekRestType") var weekRestType: Int? = null
) {

    /**
     * 获取星期的格式化
     * dayOfWeek是根据星期获取的数目减一而来。从而适配自己想要的样式
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
    fun isToday(): Boolean {
        val currentDay = CalenderUtil.getCurrentDay()
        val currentMonth = CalenderUtil.getThisMonth()
        return (day == currentDay) && (month == currentMonth)
    }

}