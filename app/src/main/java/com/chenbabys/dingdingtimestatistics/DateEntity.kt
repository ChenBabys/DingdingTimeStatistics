package com.chenbabys.dingdingtimestatistics

import java.text.SimpleDateFormat
import java.util.*

data class DateEntity(   var date: Date? = null,//具体日期
                         var day: String? = null,//一个月的某天
                         var monthStr: String? = null,//月份
                         var dayOfWeek: Int? = null,//星期几
                         var isToday:Boolean = false //是否是时间上的今天
){
   val monthAndDayFormat =  SimpleDateFormat("MM-dd")

    /**
     * 获取星期的格式化
     */
    fun getWeekFormat():String{
       return when(dayOfWeek) {
            0->"星期天"
            1->"星期一"
            2->"星期二"
            3->"星期三"
            4->"星期四"
            5->"星期五"
            6->"星期六"
            else->"不在范围"
        }
    }

    /**
     * 格式化成mm:dd日期
     */
    fun getFotMatMonthDay(myDate: Date):String{
        return monthAndDayFormat.format(myDate)
    }
}