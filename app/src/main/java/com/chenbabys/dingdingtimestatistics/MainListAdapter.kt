package com.chenbabys.dingdingtimestatistics

import android.annotation.SuppressLint
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder

class MainListAdapter: BaseQuickAdapter<DateEntity,BaseViewHolder>(R.layout.item_main_list_view) {

    @SuppressLint( "SetTextI18n")
    override fun convert(holder: BaseViewHolder, item: DateEntity) {
        val date = holder.getView<TextView>(R.id.tv_date)
        item.date?.let {
            date.text = item.getFotMatMonthDay(it)+"\n"+item.getWeekFormat()
        }
    }
}