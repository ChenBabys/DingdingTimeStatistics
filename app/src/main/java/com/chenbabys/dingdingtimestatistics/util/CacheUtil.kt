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

    /**
     * 根据月份保存日历（以前是只保存当前月份的，现在改成按传入的月份存和取）
     */
    fun setDdtsCache(month: Int, dateEntity: MutableList<DateEntity>) =
        cache.encode("calender_cache_$month", GsonUtils.toJson(dateEntity))

    fun getDdtsCache(month:Int): MutableList<DateEntity> {
        var records = mutableListOf<DateEntity>()
        val cacheDatesString = cache.decodeString("calender_cache_$month", "")
        try {
            records = GsonUtils.fromJson(cacheDatesString, GsonUtils.getListType(DateEntity::class.java))
        } catch (e: Exception) {
            e.printStackTrace()
            LogUtils.e("出错了", e)
        }
        return records
    }

    /**
     * 删除指定月份的数据
     */
    fun removeDdtsCache(month: Int) = cache.removeValueForKey("calender_cache_$month")


    /**
     * 设置月
     */
    fun setMonth(month: Int) {
        cache.encode("calender_month", month)
    }

    fun getMonth(): Int = cache.decodeInt("calender_month", -1)

    fun removeMonth() = cache.removeValueForKey("calender_month")


    /**
     * 获取旧版本的当前月数据，这个方法只为了吧旧的key的数据转换到当前月份
     * 这个方法在以后适配完旧用户之后完全可以删除掉。
     */
    fun getOldDdtsCache(): MutableList<DateEntity> {
        var records = mutableListOf<DateEntity>()
        val cacheDatesString = cache.decodeString("calender_cache", "")
        try {
            records = GsonUtils.fromJson(cacheDatesString, GsonUtils.getListType(DateEntity::class.java))
        } catch (e: Exception) {
            e.printStackTrace()
            LogUtils.e("旧数据获取出错了，可能不存在", e)
        }
        return records
    }
    /**
     * 删除旧key当月份的数据
     */
    fun removeOldDdtsCache() = cache.removeValueForKey("calender_cache")

}