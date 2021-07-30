package com.chenbabys.dingdingtimestatistics.ui.main
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import com.blankj.utilcode.util.SpanUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.chenbabys.dingdingtimestatistics.R
import com.chenbabys.dingdingtimestatistics.util.CacheUtil
import com.chenbabys.dingdingtimestatistics.util.CalenderUtil
import com.chenbabys.dingdingtimestatistics.util.DialogUtils

/**
 * 日历打卡表适配器
 */
class MainListAdapter(
    private val onTextViewClickListener: (textView: TextView, item: DateEntity, position: Int, isStartTime: Boolean, isModifyTotal: Boolean) -> Unit,
    private val onTextRemoveListener: () -> Unit,
) : BaseQuickAdapter<DateEntity, BaseViewHolder>(R.layout.item_main_list_view) {

    override fun convert(holder: BaseViewHolder, item: DateEntity) {
        val date = holder.getView<TextView>(R.id.tv_date)
        item.date?.let {
            date.text = ("${item.getFotMatMonthDay(it)}\n${item.getWeekFormat()}")
            when {
                item.isWeekEnd() -> {
                    date.setTextColor(ContextCompat.getColor(context, R.color.yellow_deep))
                }
                item.isToday() -> {
                    date.setTextColor(ContextCompat.getColor(context, R.color.pink))
                }
                else -> {
                    date.setTextColor(ContextCompat.getColor(context, R.color.purple_500))
                }
            }
        }
        val startTime = holder.getView<TextView>(R.id.tv_start_time)
        val endTime = holder.getView<TextView>(R.id.tv_end_time)
        val countTime = holder.getView<TextView>(R.id.tv_count)
        startTime.setOnClickListener {
            onTextViewClickListener.invoke(startTime, item, holder.adapterPosition, true, false)
        }
        startTime.setOnLongClickListener {
            if (startTime.text.isNotEmpty()) {
                DialogUtils.showConfirmDialog(
                    "操作删除",
                    "清除${date.text},上班时间为${startTime.text}的选项？",
                    listener = { dialog, which ->
                        startTime.text = ""
                        item.startTime = null
                        item.vacation = null
                        item.isWeeHours = false
                        onTextRemoveListener.invoke()
                    })
            }
            true
        }

        endTime.setOnClickListener {
            onTextViewClickListener.invoke(endTime, item, holder.adapterPosition, false, false)
        }
        endTime.setOnLongClickListener {
            if (endTime.text.isNotEmpty()) {
                DialogUtils.showConfirmDialog(
                    "操作删除",
                    "清除${date.text},下班时间为${endTime.text}的选项？",
                    listener = { dialog, which ->
                        endTime.text = ""
                        item.endTime = null
                        item.vacation = null
                        item.isWeeHours = false
                        onTextRemoveListener.invoke()
                    })
            }
            true
        }

        if (item.startTime.isNullOrEmpty()) {
            when (item.isWeekEnd()) {
                true -> startTime.text = ""
                false -> {
                    startTime.text = ("9:00")
                    item.startTime = startTime.text.toString()
                }//空的时候把上班时间设置为默认值
            }
        } else {
            startTime.text = item.startTime//空的时候把上班时间设置为默认值
        }
        endTime.text = item.endTime//下班时间
        if (!item.startTime.isNullOrEmpty() && !item.endTime.isNullOrEmpty()) {
            val startHour = CalenderUtil.getTimeFilterHour(startTime.text.toString())
            val endHour = CalenderUtil.getTimeFilterHour(endTime.text.toString())
            val hour = if (endHour < startHour) {//如果下班时间的小时小于上班时间的小时，就证明这家伙凌晨还在上班了
                item.isWeeHours = true
                CalenderUtil.getDifferenceTime(startTime.text.toString(), endTime.text.toString(),true)
            } else {
                item.isWeeHours = false
                CalenderUtil.getDifferenceTime(startTime.text.toString(), endTime.text.toString(),false)
            }
            item.vacation?.let { vacation -> //请假时间
                ///todo 以后可以做适配方式，不必按照写死的9.上班来统计，下同
                //如果超过了上午加上中午到下午两点前的五个小时的上班时间，则减去中午休息的两个小时,和请假时间（这里默认上班时间是9.）
                item.dayWorkHour = if (hour >= 5) ((hour - 2) - vacation) else (hour - vacation)
            } ?: let {
                //如果超过了上午加上中午到下午两点前的五个小时的上班时间，则减去中午休息的两个小时（这里默认上班时间是9.）
                item.dayWorkHour = if (hour >= 5) hour - 2 else hour
            }
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
        if (item.vacation == null||item.vacation == CacheUtil.defaultFloat) {
            countTime.text = if (item.dayWorkHour == null) "0h" else item.dayWorkHour.toString() + "h"
        } else {
            when (item.dayWorkHour) {
                null -> countTime.text = ("0h")
                else -> {
                    SpanUtils.with(countTime).append(item.dayWorkHour.toString() + "h")
                        .appendLine()
                        .append("（已减请假${item.vacation}h）")
                        .setFontSize(11, true)
                        .setForegroundColor(ContextCompat.getColor(context, R.color.qing)).create()
                }
            }
        }
        //如果下班时间是凌晨的话
        if (item.isWeeHours){
            endTime.setCompoundDrawablesRelativeWithIntrinsicBounds(
                ContextCompat.getDrawable(context, R.drawable.ic_wee_hours), null, null, null)
            endTime.setPadding(50,0,50,0)
        }else{
            endTime.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null)
            endTime.setPadding(0,0,0,0)
        }
        //一天的总工时
        if (item.dayWorkHour == 0.0f) {
            countTime.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null)
        } else {
            when (item.vacation) {
                CacheUtil.defaultFloat->{
                    countTime.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        ContextCompat.getDrawable(context, R.drawable.ic_modify), null, null, null)
                }
                null -> {
                    countTime.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        ContextCompat.getDrawable(context, R.drawable.ic_modify), null, null, null)
                }
                else -> {
                    countTime.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        ContextCompat.getDrawable(context, R.drawable.ic_vacation), null, null, null)
                }
            }
            //左边图标的点击事件
            countTime.setOnClickListener {
                onTextViewClickListener.invoke(countTime, item, holder.adapterPosition, false, true)
            }
        }
        val parentView = holder.getView<LinearLayoutCompat>(R.id.ll_parent)
        if (item.isToday()) {//如果是今天
            parentView.setBackgroundColor(ContextCompat.getColor(context, R.color.lite_gay))
            item.isTodayPosition = holder.adapterPosition
        } else {
            parentView.setBackgroundColor(ContextCompat.getColor(context, R.color.white))
            item.isTodayPosition = null
        }
    }
}