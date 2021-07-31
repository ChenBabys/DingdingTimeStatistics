package com.chenbabys.dingdingtimestatistics.util

import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.LogUtils
import com.chenbabys.dingdingtimestatistics.ui.main.DateEntity
import com.tencent.mmkv.MMKV
import java.lang.Exception

/**
 * Created by ChenYesheng On 2021/7/26 18:20
 * Desc:
 */
object CacheUtil {
    //private val cache = MMKV.defaultMMKV()//默认初始化方式
    private val cache = MMKV.mmkvWithID("calender_data")//初始化
    const val defaultFloat: Float = 0.0f

    fun setDdtsCache(dateEntity: MutableList<DateEntity>) =
        cache.encode("calender_cache", GsonUtils.toJson(dateEntity))

    fun getDdtsCache(): MutableList<DateEntity> {
        var records = mutableListOf<DateEntity>()
        val cacheDatesString = cache.decodeString("calender_cache", "")
        try {
            records =
                GsonUtils.fromJson(cacheDatesString, GsonUtils.getListType(DateEntity::class.java))
        } catch (e: Exception) {
            e.printStackTrace()
            LogUtils.e("出错了", e)
        }
        return records
    }

    fun removeDdtsCache() = cache.removeValueForKey("calender_cache")

    /**
     * 设置保存上个月总工时
     */
    fun setLastMonthHours(hours: Float) {
        cache.encode("calender_last_month_hours", hours)
    }

    fun getLastMonthHours(): Float = cache.decodeFloat("calender_last_month_hours", defaultFloat)

    fun removeLastMonthHours() = cache.removeValueForKey("calender_last_month_hours")

    /**
     * 设置保存这个月总工时
     */
    fun setThisMonthHours(hours: Float) {
        cache.encode("calender_this_month_hours", hours)
    }

    /**
     *获取
     */
    fun getCurrentSaveMonthHours(): Float =
        cache.decodeFloat("calender_this_month_hours", defaultFloat)

    fun removeCurrentSaveMonthHours() = cache.removeValueForKey("calender_this_month_hours")



    /**
     * 设置月
     */
    fun setMonth(month: Int) {
        cache.encode("calender_month", month)
    }

    fun getMonth(): Int = cache.decodeInt("calender_month", -1)

    fun removeMonth() = cache.removeValueForKey("calender_month")


}