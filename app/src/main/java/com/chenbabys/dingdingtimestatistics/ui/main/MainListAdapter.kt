package com.chenbabys.dingdingtimestatistics.ui.main

import android.annotation.SuppressLint
import android.text.TextUtils
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
import com.blankj.utilcode.util.ToastUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.chenbabys.dingdingtimestatistics.R
import com.chenbabys.dingdingtimestatistics.util.CalenderUtil
import java.text.SimpleDateFormat
import java.util.*

class MainListAdapter(private val onTextChangeListener: () -> Unit) :
    BaseQuickAdapter<DateEntity, BaseViewHolder>(R.layout.item_main_list_view) {

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    override fun convert(holder: BaseViewHolder, item: DateEntity) {
        val date = holder.getView<TextView>(R.id.tv_date)
        item.date?.let {
            date.text = item.getFotMatMonthDay(it) + "\n" + item.getWeekFormat()
        }
        val startTime = holder.getView<EditText>(R.id.tv_start_time)
        val endTime = holder.getView<EditText>(R.id.tv_end_time)
        val countTime = holder.getView<TextView>(R.id.tv_count)
        countTime.text = item.dayWorkHour.toString() + "h"
        if (startTime.text.isNotBlank() && endTime.text.isNotEmpty()) {
            val hour =
                CalenderUtil.getDifferenceTime(startTime.text.toString(), endTime.text.toString())
            item.dayWorkHour = hour
        }
        endTime.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_DONE) {   // 按下完成按钮，这里和上面imeOptions对应
                    onTextChangeListener.invoke()
                    return true   //返回true，保留软键盘。false，隐藏软键盘
                }
                return false
            }
        })
    }
}