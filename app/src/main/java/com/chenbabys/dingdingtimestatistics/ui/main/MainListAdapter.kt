package com.chenbabys.dingdingtimestatistics.ui.main

import android.widget.TextView
import androidx.core.content.ContextCompat
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.chenbabys.dingdingtimestatistics.R
import com.chenbabys.dingdingtimestatistics.util.CalenderUtil
import com.chenbabys.dingdingtimestatistics.util.DialogUtils

/**
 * 日历打卡表适配器
 */
class MainListAdapter(
    private val onTextViewClickListener: (textView: TextView, item: DateEntity, position: Int, isStartTime: Boolean) -> Unit,
    private val onTextRemoveListener: () -> Unit
) :
    BaseQuickAdapter<DateEntity, BaseViewHolder>(R.layout.item_main_list_view) {

    override fun convert(holder: BaseViewHolder, item: DateEntity) {
        val date = holder.getView<TextView>(R.id.tv_date)
        item.date?.let {
            date.text = ("${item.getFotMatMonthDay(it)}\n${item.getWeekFormat()}")
            if (item.isWeekEnd()) {
                date.setTextColor(ContextCompat.getColor(context, R.color.yellow_deep))
            } else {
                date.setTextColor(ContextCompat.getColor(context, R.color.purple_500))
            }
        }
        val startTime = holder.getView<TextView>(R.id.tv_start_time)
        val endTime = holder.getView<TextView>(R.id.tv_end_time)
        val countTime = holder.getView<TextView>(R.id.tv_count)
        startTime.setOnClickListener {
            onTextViewClickListener.invoke(startTime, item, holder.adapterPosition, true)
        }
        startTime.setOnLongClickListener {
            DialogUtils.showConfirmDialog(
                "操作删除",
                "清除${date.text},上班时间：${startTime.text}项？",
                listener = { dialog, which ->
                    startTime.text = ""
                    item.startTime = null
                    onTextRemoveListener.invoke()
                })
            true
        }

        endTime.setOnClickListener {
            onTextViewClickListener.invoke(startTime, item, holder.adapterPosition, false)
        }
        endTime.setOnLongClickListener {
            DialogUtils.showConfirmDialog(
                "操作删除",
                "清除${date.text},下班时间：${endTime.text}项？",
                listener = { dialog, which ->
                    endTime.text = ""
                    item.endTime = null
                    onTextRemoveListener.invoke()
                })
            true
        }

        if (item.startTime.isNullOrEmpty()) {
            when (item.isWeekEnd()) {
                true -> startTime.text = ""
                false -> startTime.text = "9:00"//空的时候把上班时间设置为默认值
            }
        } else {
            startTime.text = item.startTime//空的时候把上班时间设置为默认值
        }
        endTime.text = item.endTime//下班时间
        if (!item.startTime.isNullOrEmpty() && !item.endTime.isNullOrEmpty()) {
            val hour =
                CalenderUtil.getDifferenceTime(startTime.text.toString(), endTime.text.toString())
            item.dayWorkHour = if (hour > 4) hour - 2 else hour//如果超过了上午的四个小时的上班时间，则减去中午休息的两个小时
        } else {
            item.dayWorkHour = 0.0f//没填满的时候赋值为0
        }
        item.dayWorkHour?.let {//大于或者等于10小时则显示黑色文字，否则是红色文字
            if (it >= 10) {
                countTime.setTextColor(ContextCompat.getColor(context, R.color.black))
            } else {
                countTime.setTextColor(ContextCompat.getColor(context, R.color.red))
            }
        } ?: let {
            item.dayWorkHour = 0.0f//空的时候赋值为0
        }
        countTime.text = if (item.dayWorkHour == null) "0h" else item.dayWorkHour.toString() + "h"
    }
}