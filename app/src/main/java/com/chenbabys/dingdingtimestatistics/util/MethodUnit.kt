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
    fun formatNumberTwoDigits(number:Float?):String{
        val format  = DecimalFormat("0.##")
        //未保留小数的舍弃规则，RoundingMode.FLOOR表示直接舍弃。HALF_UP,四舍五入
        format.roundingMode = RoundingMode.HALF_UP
        return format.format(number)
    }
}