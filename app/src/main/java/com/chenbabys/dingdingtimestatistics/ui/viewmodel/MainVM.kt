package com.chenbabys.dingdingtimestatistics.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.LogUtils
import com.chenbabys.dingdingtimestatistics.base.BaseViewModel
import com.chenbabys.dingdingtimestatistics.ui.main.DateEntity
import com.chenbabys.dingdingtimestatistics.util.CacheUtil
import com.chenbabys.dingdingtimestatistics.util.CalenderUtil

/**
 * Created by ChenYesheng On 2021/7/26 16:02
 * Desc:
 */
class MainVM : BaseViewModel() {
    val dateListChange = MutableLiveData<Boolean>()
    var dateList = mutableListOf<DateEntity>()


    /**
     * 获取日历
     */
    fun getCalendarEntities() {
        dateList = if (CalenderUtil.getCalenderInOne()) {//如果在当月的一号
            CacheUtil.removeDdtsCache()//清除掉所有的旧数据
            CalenderUtil.getDateEntities()
        } else {
            if (CacheUtil.getDdtsCache().isNullOrEmpty()) {//如果是空的则还是填充获取日历的，否则才给缓存的
                CalenderUtil.getDateEntities()
            } else {
                CacheUtil.getDdtsCache()
            }
        }
        dateListChange.value = true
    }

}