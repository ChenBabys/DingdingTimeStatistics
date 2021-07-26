package com.chenbabys.dingdingtimestatistics.util

import android.content.DialogInterface
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.blankj.utilcode.util.SizeUtils
import com.chenbabys.dingdingtimestatistics.R
import per.goweii.anylayer.ActivityHolder
import per.goweii.anylayer.AnyLayer
import per.goweii.anylayer.dialog.DialogLayer

/**
 * 弹框
 */
object DialogUtils {
    /**
     * 确认 弹窗 带标题
     */
    fun showConfirmDialog(
        title: String? = "",
        message: String? = "",
        negativeText: String? = "取消",
        positiveText: String? = "确定",
        negativeColor: Int = 0,
        positiveColor: Int = 0,
        listener: DialogInterface.OnClickListener?
    ): DialogLayer {
        val contentView = LayoutInflater.from(ActivityHolder.requireCurrentActivity()).inflate(
            R.layout.dialog_confirm,
            null
        )
        val dialog = AnyLayer.dialog()
            .contentView(contentView)
            .backgroundDimDefault()
            .gravity(Gravity.CENTER)
            .cancelableOnTouchOutside(true)
            .cancelableOnClickKeyBack(true)
        dialog.show()
        val layoutParams = dialog.contentView!!.layoutParams
        layoutParams.width = SizeUtils.dp2px(300f)
        val tvTitle = contentView.findViewById<TextView>(R.id.tv_title)
        val tvMessage = contentView.findViewById<TextView>(R.id.tv_message)
        val tvCancel = contentView.findViewById<TextView>(R.id.tv_cancel)
        val tvConfirm = contentView.findViewById<TextView>(R.id.tv_confirm)
        tvTitle.text = title
        if (TextUtils.isEmpty(title)) tvTitle.visibility = View.GONE
        tvMessage.text = message
        tvCancel.text = negativeText
        tvConfirm.text = positiveText
        if (negativeColor != 0) {
            tvCancel.setTextColor(negativeColor)
        }
        if (positiveColor != 0) {
            tvConfirm.setTextColor(positiveColor)
        }
        tvCancel.setOnClickListener { v: View? -> dialog.dismiss() }
        tvConfirm.setOnClickListener { v: View? ->
            dialog.dismiss()
            listener?.onClick(null, 1)
        }
        return dialog
    }
}