package com.chenbabys.dingdingtimestatistics.util

import java.math.RoundingMode
import java.text.DecimalFormat

/**
 * Created by ChenYesheng On 2021/10/7 17:09
 * Desc:
 */
object MethodUnit {

    /**
     * 格式化数目，保留小数点后两位数
     */
    fun formatNumberTwoDigits(number: Float?): String {
        val format = DecimalFormat("0.##")
        //未保留小数的舍弃规则，RoundingMode.FLOOR表示直接舍弃。HALF_UP,四舍五入
        format.roundingMode = RoundingMode.HALF_UP
        return format.format(number)
    }

    /**
     * 根据相关的条件计算工时。
     * startHour：开始时间
     * endHour：结束时间
     * hour: 根据开始时间和结束时间以及其他条件计算得来，算是未做以下操作之前的总工时。
     * vacation：请假时间(这里直接初始化默认为0，那么那边就可以不用传也可以，悉听尊便)
     * 0.5f：dinnerTime:是吃晚饭的时间，要减去，现在已经不算进工时了
     * 21.5：是晚上九点半，因为要到晚上九点半开始才算是加班，不等于或者超过就不算加班。因为未满3小时就不算加班了
     * 14：下午两点
     * 12：中午12点
     * isOverSixPmHour：是否是超过下午6点后并且21.5之前的时间。是的话就减掉并返回，不是就返回原值
     *
     * 要是以后吃晚饭时间不再扣掉以及晚上不再固定要到9.30才算加班的时候，去掉dinnerTime和isOverSixPmHour（用hour替代回去）即可。
     */
    fun mathCalTimeCount(startHour: Float, endHour: Float, hour: Float, vacation: Float = 0f): Float {
        val dinnerTime = if (endHour >= 21.5) 0.5f else 0f
        val isOverSixPmHour = if (endHour > 18 && endHour < 21.5) hour-(endHour-18) else hour
        return when {
            startHour < 14 -> {
                if (startHour > 12) {
                    ((isOverSixPmHour - (14 - startHour)) - vacation) - dinnerTime
                } else {
                    ((isOverSixPmHour - 2) - vacation) - dinnerTime
                }
            }
            else -> (isOverSixPmHour - vacation) - dinnerTime
        }
    }
}