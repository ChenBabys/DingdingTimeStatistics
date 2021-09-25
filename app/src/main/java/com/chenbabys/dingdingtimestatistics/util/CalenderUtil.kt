package com.chenbabys.dingdingtimestatistics.util

import android.annotation.SuppressLint
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.TimeUtils
import com.chenbabys.dingdingtimestatistics.ui.main.DateEntity
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
        //val monthLength = calendar.getMaximum(Calendar.DAY_OF_MONTH)//获取一个月可能存在的最大天数，也即是31
        val monthLength = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)//获取当前月份最大天数，与上不同

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
            val thisDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1//减一适配当前星期，让0变成星期天
            val entity = DateEntity().apply {
                date = thisDate
                dayOfWeek = thisDayOfWeek
                month = calendar.get(Calendar.MONTH) + 1
                day = calendar.get(Calendar.DAY_OF_MONTH)
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
    fun getThisMonth(): Int {
        return calendar.get(Calendar.MONTH) + 1
    }

    /**
     * 获取是否是月内的最后一天
     */
    fun getIsMonthLastDay(): Boolean {
        val currentMonthLength = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)//获取当前月份天数
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
        return currentDay == currentMonthLength//如果相等就是最后一天
    }

    /**
     * 获取当前月的当前天的int返回
     */
    fun getCurrentDay(): Int {
        return calendar.get(Calendar.DAY_OF_MONTH)
    }

    /**
     * 获取当前的小时（按照24小时制返回）
     */
    fun getCurrentHour(): Int {
        return calendar.get(Calendar.HOUR_OF_DAY)
    }

    /**
     * 求差值时间
     * 值得格式必须是HH:mm或者HH.mm
     * startTime:上班时间
     * endTime：下班时间
     * isWeeHours：下班时间是否超过了夜里12点的凌晨
     */
    fun getDifferenceTime(startTime: String, endTime: String, isWeeHours: Boolean): Float {
        val startStr = if (startTime.contains(".")) startTime.replace(".", ":") else startTime
        val endStr = if (endTime.contains(".")) endTime.replace(".", ":") else endTime
        val format = SimpleDateFormat("HH:mm:ss")
        val startDate = format.parse("$startStr:00")//获取开始时间date
        val endDate = format.parse("$endStr:00")//获取结束时间date
        val startLong = startDate.time//获取开始时间毫秒
        val endLong = endDate.time//获取结束时间毫秒

        val middleLong = endLong - startLong//获取差值毫秒
        //val 相隔天数: Long = (timeNow - timeOld) / (1000 * 60 * 60 * 24) //化为天
        //h:小时，s:秒，ms:毫秒
        //1h=3600s=3600*1000=3600000ms
        val h = middleLong / (1000 * 60 * 60).toFloat() //毫秒转换为小时，记得返回float类型数据。
        return if (isWeeHours) h + 24.0f else h//如果是凌晨则加上一个周期
    }

    /**
     * 获取当前是否在月里的一号
     */
    fun getCalenderInOne(): Boolean {
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        return day == 1
    }

    /**
     * 字符时间变成Float按照小时返回
     * 时间戳不知为何是负数，排查后确定time是拿到了1970年的时间戳，所以负数。暂时不知怎么解决~取消这种做法了
     */
    fun getTimeStr2MillLong(time: String): Float {
        LogUtils.d("查看", time)
        val timeStr = if (time.contains(".")) time.replace(".", ":") else time
        val format = SimpleDateFormat("HH:mm:ss")
        val currentDate = format.parse("${timeStr}:00")//获取开始时间date
        val dateLong = currentDate.time
        val hour = dateLong / (1000 * 60 * 60)//转换成小时
        LogUtils.d("查看", TimeUtils.millis2String(hour))
        LogUtils.d("查看", dateLong)
        LogUtils.d("查看", hour)
        return hour.toFloat()
    }

    /**
     * 获取时间字符中的小时
     * //默认值给0.0f
     */
    fun getTimeFilterHour(time: String): Float {
        var timeHour: Float = 0.0f
        if (time.contains(".")) {
            val timeList = time.split(".")
            timeHour = timeList[0].toFloat()
        } else if (time.contains(":")) {
            val timeList = time.split(":")
            timeHour = timeList[0].toFloat()
        }
        return timeHour
    }

    /**
     * 获取星期几
     * 减一以适配样式
     * 不减一的话就按照西方的星期样式，第一天是1也就是星期天。以此递增
     * 减一就把0当天星期天了，而后的数字就刚好适配到星期的数目
     */
    fun getWeekCurrent():String{
        return when (calendar.get(Calendar.DAY_OF_WEEK) - 1) {
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

}