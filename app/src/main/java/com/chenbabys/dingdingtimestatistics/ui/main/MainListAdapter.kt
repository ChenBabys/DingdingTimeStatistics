package com.chenbabys.dingdingtimestatistics.ui.main

import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.chenbabys.dingdingtimestatistics.R
import com.chenbabys.dingdingtimestatistics.util.CalenderUtil

/**
 * 日历打卡表适配器
 */
class MainListAdapter(private val onTextChangeListener: () -> Unit) :
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
        val startTime = holder.getView<EditText>(R.id.tv_start_time)
        val endTime = holder.getView<EditText>(R.id.tv_end_time)
        val countTime = holder.getView<TextView>(R.id.tv_count)
        startTime.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_DONE) {   // 按下完成按钮，这里和上面imeOptions对应
                    item.startTime = startTime.text.toString()
                    onTextChangeListener.invoke()
                    return false   //返回true，保留软键盘。false，隐藏软键盘
                }
                return false
            }
        })
        endTime.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_DONE) {   // 按下完成按钮，这里和上面imeOptions对应
                    item.startTime = startTime.text.toString()
                    item.endTime = endTime.text.toString()
                    onTextChangeListener.invoke()
                    return false   //返回true，保留软键盘。false，隐藏软键盘
                }
                return false
            }
        })
        if (item.startTime.isNullOrEmpty()) {
            when (item.isWeekEnd()) {
                true -> startTime.setText("")
                false -> startTime.setText("9:00")//空的时候把上班时间设置为默认值
            }
        } else {
            startTime.setText(item.startTime)//空的时候把上班时间设置为默认值
        }
        endTime.setText(item.endTime)//下班时间
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