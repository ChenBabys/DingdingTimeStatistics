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
    private val cache = MMKV.mmkvWithID("calender_data")//初始化
    //private val cache = MMKV.defaultMMKV()//默认初始化方式

    fun setDdtsCache(dateEntity: MutableList<DateEntity>) =
        cache.encode("calender_cache", GsonUtils.toJson(dateEntity))

    fun getDdtsCache(): MutableList<DateEntity> {
        var records = mutableListOf<DateEntity>()
        val cacheDatesString = cache.decodeString("calender_cache","")
        try {
            records = GsonUtils.fromJson(cacheDatesString, GsonUtils.getListType(DateEntity::class.java))
        } catch (e: Exception) {
            e.printStackTrace()
            LogUtils.e("出错了",e)
        }
        return records
    }

    fun removeDdtsCache() = cache.removeValueForKey("calender_cache")
}