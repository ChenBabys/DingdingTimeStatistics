package com.chenbabys.dingdingtimestatistics.util

import com.chenbabys.dingdingtimestatistics.ui.main.DateEntity
import com.google.android.material.timepicker.TimeFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * 获取相应返回的日期
 */
object CalenderUtil {
    private val calendar = Calendar.getInstance()

    /**
     * 获取日历
     */
    fun getDateEntities(): MutableList<DateEntity> {
        val dateEntities: MutableList<DateEntity> = mutableListOf()
        //日期格式化
        //日期格式化
//        @SuppressLint("SimpleDateFormat") val formatMonthDay = SimpleDateFormat("MM-dd")
//        @SuppressLint("SimpleDateFormat") val formatYear = SimpleDateFormat("yyyy")
        //calendar.set(Calendar.MONTH,6)//不设置月份就是默认当前月份
        val monthLength = calendar.getMaximum(Calendar.DAY_OF_MONTH)//获取当前月份天数

        //--------- 第一种做法，没成功，暂时没找到缘故------------//
//        calendar.set(Calendar.DAY_OF_MONTH,1)//日历设置为本月第一天
//        var thisDay = calendar.get(Calendar.DAY_OF_MONTH)//获取日历在当前那一天（已经设置为1号）
//        while (thisDay <= monthLength){
//            LogUtils.d("测试22",thisDay.toString())
//            val thisDate = calendar.time
//            val thisDayOfWeek  = calendar.get(Calendar.DAY_OF_WEEK)-1//星期也是从0开始的，所以这样
//            val entity = DateEntity().apply {
//                date = thisDate
//                dayOfWeek = thisDayOfWeek
//            }
//            dateEntities.add(entity)
//            calendar.add(Calendar.DAY_OF_MONTH,1)//天数加一
//            thisDay = calendar.get(Calendar.DAY_OF_MONTH)//再次获取thisDay更新值，否则会一直使用上面初始化的那个值的
//
//            //（这里这是举例减一的做法，和内容无关，注释了，因为这里不需要减法）
//            //calendar.add(Calendar.DAY_OF_MONTH,-1)//天数减一的做法
//        }
        //---------- 第一种结束----------//


        //--------- 第二种做法------------//
        for (index in 1..monthLength) {
            calendar.set(Calendar.DAY_OF_MONTH, index)
            val thisDate = calendar.time
            val thisDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1//星期也是从0开始的，所以这样
            val entity = DateEntity().apply {
                date = thisDate
                dayOfWeek = thisDayOfWeek
            }
            dateEntities.add(entity)
        }
        //---------- 第二种结束----------//


        //返回结果
        return dateEntities
    }

    /**
     * 获取本月月份(+1，因为月份是从0开始)
     */
    fun getThisMonth(): String {
        return (calendar.get(Calendar.MONTH) + 1).toString()
    }

    /**
     * 求差值时间
     * 值得格式必须是HH:mm:ss
     */
    fun getDifferenceTime(startTime: String, endTime: String): Long {
        val format = SimpleDateFormat("HH:mm:ss")
        val startDate = format.parse("$startTime:00")//获取开始时间date
        val endDate = format.parse("$endTime:00")//获取结束时间date
        val startLong = startDate.time//获取开始时间毫秒
        val endLong = endDate.time//获取结束时间毫秒

        val middleLong = endLong - startLong//获取差值毫秒
        //val 相隔天数: Long = (timeNow - timeOld) / (1000 * 60 * 60 * 24) //化为天

        val h = middleLong / (1000 * 60 * 24) //转换为小时
        return h
    }
}