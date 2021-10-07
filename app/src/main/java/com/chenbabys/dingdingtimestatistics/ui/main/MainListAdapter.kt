package com.chenbabys.dingdingtimestatistics.ui.main
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import com.blankj.utilcode.util.ClickUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.SpanUtils
import com.blankj.utilcode.util.ToastUtils
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
                    date.setTextColor(ContextCompat.getColor(context, R.color.gray_deep))
                }
                item.isToday() -> {
                    date.setTextColor(ContextCompat.getColor(context, R.color.yellow_deep))
                }
                else -> {
                    date.setTextColor(ContextCompat.getColor(context, R.color.lite_blue))
                }
            }
        }
        val startTime = holder.getView<TextView>(R.id.tv_start_time)
        val endTime = holder.getView<TextView>(R.id.tv_end_time)
        val countTime = holder.getView<TextView>(R.id.tv_count)
        //添加一个缩放和透明度的动画
        ClickUtils.applyPressedViewAlpha(countTime)
        ClickUtils.applyPressedViewScale(countTime)
        //添加一个缩放和透明度的动画
        ClickUtils.applyPressedViewAlpha(startTime)
        ClickUtils.applyPressedViewScale(startTime)
        startTime.setOnClickListener {
            onTextViewClickListener.invoke(startTime, item, holder.adapterPosition, true, false)
        }
        startTime.setOnLongClickListener {
            if (startTime.text.isNotEmpty()) {
                DialogUtils.showConfirmDialog(
                    "操作删除",
                    "清除${date.text}\n上班时间为${startTime.text}的选项？",
                    listener = { dialog, which ->
                        startTime.text = null
                        item.startTime = null
                        item.vacation = null
                        item.isWeeHours = false
                        item.dayWorkHour = null
                        onTextRemoveListener.invoke()
                    })
            }
            true
        }
        //添加一个缩放和透明度的动画
        ClickUtils.applyPressedViewAlpha(endTime)
        ClickUtils.applyPressedViewScale(endTime)
        endTime.setOnClickListener {
            onTextViewClickListener.invoke(endTime, item, holder.adapterPosition, false, false)
        }
        endTime.setOnLongClickListener {
            if (endTime.text.isNotEmpty()) {
                DialogUtils.showConfirmDialog(
                    "操作删除",
                    "清除${date.text}\n下班时间为${endTime.text}的选项？",
                    listener = { dialog, which ->
                        endTime.text = null
                        item.endTime = null
                        item.vacation = null
                        item.isWeeHours = false
                        item.dayWorkHour = null
                        onTextRemoveListener.invoke()
                    })
            }
            true
        }

        if (item.startTime.isNullOrEmpty()) {
            when (item.isWeekEnd()) {
                true -> startTime.text = ""
                false -> {
                    startTime.text = ("09:00")
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
                //并且开始的时间是小于下午14点的
                item.dayWorkHour = if (startHour<14 && hour>=5) {
                   if (startHour > 12){
                       ((hour - (14 - startHour)) - vacation)
                   }else{
                       ((hour - 2) - vacation)
                   }
                } else (hour - vacation)
            } ?: let {
                //如果超过了上午加上中午到下午两点前的五个小时的上班时间，则减去中午休息的两个小时（这里默认上班时间是9.）
                //并且开始的时间是小于下午14点的
                item.dayWorkHour = if (startHour<14 && hour >= 5) {
                    if (startHour>12){
                        hour - (14 - startHour)
                    }else{
                        hour - 2
                    }
                } else hour
            }
        } else {
            item.dayWorkHour = CacheUtil.defaultFloat //没填满的时候赋值为0
        }

        item.dayWorkHour?.let {//大于或者等于10小时则显示主题色文字，否则是红色文字
            when {
                it >= 10 -> {
                    countTime.setTextColor(ContextCompat.getColor(context, R.color.purple_200))
                }
                it == CacheUtil.defaultFloat -> {
                    countTime.setTextColor(ContextCompat.getColor(context, R.color.gray_deep))
                }
                else -> {
                    countTime.setTextColor(ContextCompat.getColor(context, R.color.red))
                }
            }
        } ?: let {
            item.dayWorkHour = CacheUtil.defaultFloat //空的时候赋值为0
        }
        if (item.vacation == null || item.vacation == CacheUtil.defaultFloat) {
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
        if (item.dayWorkHour == CacheUtil.defaultFloat) {
            countTime.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null)
        } else {
            when (item.vacation) {
                CacheUtil.defaultFloat ->{
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
                //这里不能改判断 isModify,有什么判断就回到回调那边去做
                onTextViewClickListener.invoke(countTime, item, holder.adapterPosition, false, true)
            }
        }
        val parentView = holder.getView<LinearLayoutCompat>(R.id.ll_parent)
        if (item.isToday()) {//如果是今天
            parentView.background = ContextCompat.getDrawable(context, R.color.gray_few)
            item.isTodayPosition = holder.adapterPosition
        } else {
            parentView.background = null
            item.isTodayPosition = null
        }
    }
}