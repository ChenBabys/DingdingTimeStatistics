package com.chenbabys.dingdingtimestatistics

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

/**
 * 获取相应返回的日期
 */
object CalenderUtil {

    /**
     * 获取日历
     */
    fun getDateEntities():MutableList<DateEntity>{
        val dateEntities: MutableList<DateEntity> = mutableListOf()
        val calendar = Calendar.getInstance()
        //日期格式化
        //日期格式化
        @SuppressLint("SimpleDateFormat") val formatMonthDay = SimpleDateFormat("MM-dd")
        @SuppressLint("SimpleDateFormat") val formatYear = SimpleDateFormat("yyyy")
        //calendar.set(Calendar.MONTH,6)//不设置月份就是默认当前月份
        val monthLength = calendar.getMaximum(Calendar.DAY_OF_MONTH)

        for (index in 1..monthLength){
            calendar.set(Calendar.DAY_OF_MONTH,index)
            val thisDate = calendar.time
            val thisDayOfWeek  = calendar.get(Calendar.DAY_OF_WEEK)-1//星期也是从0开始的，所以这样
            val entity = DateEntity().apply {
                date = thisDate
                dayOfWeek = thisDayOfWeek
            }
            dateEntities.add(entity)
        }
        return dateEntities
    }

}