package com.chenbabys.dingdingtimestatistics.util

import android.animation.Animator
import android.content.Context
import android.content.DialogInterface
import android.os.Looper
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.LinearLayoutCompat
import com.blankj.utilcode.util.KeyboardUtils
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.SizeUtils
import com.blankj.utilcode.util.ToastUtils
import com.chenbabys.dingdingtimestatistics.R
import com.chenbabys.dingdingtimestatistics.databinding.DialogValueTipsChildBinding
import com.chenbabys.dingdingtimestatistics.databinding.DialogViewSettingWeekSingleOrDoubleRestBinding
import per.goweii.anylayer.ActivityHolder
import per.goweii.anylayer.AnyLayer
import per.goweii.anylayer.Layer
import per.goweii.anylayer.dialog.DialogLayer
import per.goweii.anylayer.ktx.doOnDismiss
import per.goweii.anylayer.ktx.doOnShow
import per.goweii.anylayer.popup.PopupLayer
import per.goweii.anylayer.utils.AnimatorHelper
import per.goweii.anylayer.widget.SwipeLayout

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

    /**
     * 显示更多选择的pop
     */
    fun showMoreChooseDialog(
        tabValues: MutableList<String>,
        view: View,
        listener: (position:Int, text: String) -> Unit,
        offsetXdp: Float = -5f,//预设默认x偏移值
        offsetYdp: Float = -10f//预设默认y偏移值
    ): DialogLayer {
        val tipsContentView: View =
            LayoutInflater.from(ActivityHolder.getCurrentActivity())
                .inflate(R.layout.dialog_value_tips, null)
        val container = tipsContentView.findViewById<LinearLayout>(R.id.ll_container_item)
        val dialogLayer: DialogLayer = AnyLayer.popup(view)
            .align(
                PopupLayer.Align.Direction.VERTICAL,
                PopupLayer.Align.Horizontal.ALIGN_RIGHT,
                PopupLayer.Align.Vertical.BELOW,
                false
            )
            .offsetXdp(offsetXdp)
            .offsetYdp(offsetYdp)
            .contentView(tipsContentView)
            .gravity(Gravity.CENTER)
            .contentAnimator(object : Layer.AnimatorCreator {
                override fun createInAnimator(target: View): Animator {
                    return AnimatorHelper.createAlphaInAnim(target)
                }

                override fun createOutAnimator(target: View): Animator {
                    return AnimatorHelper.createAlphaOutAnim(target)
                }
            })
            .cancelableOnTouchOutside(true)
            .cancelableOnClickKeyBack(true)
        container.removeAllViews()//清除所有view
        for (index in 0 until tabValues.size){
            val childView =
                DialogValueTipsChildBinding.inflate(LayoutInflater.from(ActivityHolder.getCurrentActivity()))
            childView.content.text = tabValues[index]
            container.addView(childView.root)
            childView.root.setOnClickListener {
                listener.invoke(index,childView.content.text.toString())
                dialogLayer.dismiss()
            }
        }
        dialogLayer.show()
        return dialogLayer
    }

    /**
     * 显示输入请假时间dialog
     */
    fun showInputHourDialog(
        context: Context, onConfirmClick: (number: Float) -> Unit,
        onShowOrHideListener: ((isShow: Boolean) -> Unit)? = null//可空
    ): DialogLayer {
        val contentView = LayoutInflater.from(ActivityHolder.requireCurrentActivity())
            .inflate(R.layout.dialog_vacation_hours_input, null)
        val inputBox = contentView.findViewById<EditText>(R.id.et_number)
        val tvComfirm = contentView.findViewById<LinearLayoutCompat>(R.id.fl_settlement)
        val dialog =
            AnyLayer.dialog(context).contentView(contentView).backgroundDimDefault()
                .compatSoftInput(true)//适应输入弹框上移
                .gravity(Gravity.BOTTOM).swipeDismiss(SwipeLayout.Direction.BOTTOM)//底部划出
                .doOnShow {
                    onShowOrHideListener?.invoke(true)
                }
                .doOnDismiss {
                    onShowOrHideListener?.invoke(false)
                }
                .contentAnimator(object : Layer.AnimatorCreator {
                    override fun createInAnimator(target: View): Animator {
                        return AnimatorHelper.createBottomInAnim(contentView)
                    }

                    override fun createOutAnimator(target: View): Animator {
                        return AnimatorHelper.createBottomOutAnim(contentView)
                    }

                }).cancelableOnTouchOutside(true).cancelableOnClickKeyBack(true)
        dialog.show()
        //获取焦点并弹出键盘
        //弹框延时，解决弹框测量未完成就弹出导致弹框中间大缺块的问题
        android.os.Handler(Looper.getMainLooper()).postDelayed({
            KeyboardUtils.showSoftInput(inputBox)
        }, 200)
        contentView.findViewById<FrameLayout>(R.id.fl_close).setOnClickListener {
            dialog.dismiss()
            KeyboardUtils.hideSoftInput(it)
        }
        tvComfirm.setOnClickListener {
            val inputBoxStr = inputBox.text.toString()
            if (inputBoxStr.isEmpty()) {
                ToastUtils.showShort("请输入请假时间")
                return@setOnClickListener
            }
            //如果包含两个..，或者直接等于一个.则
            if (inputBoxStr.contains("..") || inputBoxStr == ".") {
                ToastUtils.showShort("请输入合法的请假时间")
                return@setOnClickListener
            }
            onConfirmClick.invoke(inputBoxStr.toFloat())
            dialog.dismiss()
            KeyboardUtils.hideSoftInput(it)
        }
        //不加这段布局就扭曲~
        val layoutParams = dialog.contentView!!.layoutParams
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
        layoutParams.width = ScreenUtils.getScreenWidth()
        return dialog
    }


//    /**
//     * 显示确定单双休的弹框，本程序安装后只会执行一次~
//     * //weekRestType:每周的休息方式，1单休，2双休 (不做了)
//     */
//    fun showConfirmSingleWeekOrDialog(
//        context: Context, onConfirmClick: (weekRestType: Int) -> Unit,
//        onShowOrHideListener: ((isShow: Boolean) -> Unit)? = null//可空
//    ): DialogLayer {
//        val binding =
//            DialogViewSettingWeekSingleOrDoubleRestBinding.inflate(LayoutInflater.from(context))
//        val dialog = AnyLayer.dialog(context)
//            .contentView(binding.root)
//            .backgroundDimDefault()
//            .compatSoftInput(true)//适应输入弹框上移
//            .gravity(Gravity.CENTER)
//            .doOnShow {
//                onShowOrHideListener?.invoke(true)
//            }
//            .doOnDismiss {
//                onShowOrHideListener?.invoke(false)
//            }
//            .contentAnimator(object : Layer.AnimatorCreator {
//                override fun createInAnimator(target: View): Animator {
//                    return AnimatorHelper.createDelayedZoomInAnim(binding.root)
//                }
//
//                override fun createOutAnimator(target: View): Animator {
//                    return AnimatorHelper.createDelayedZoomOutAnim(binding.root)
//                }
//
//            }).cancelableOnTouchOutside(false).cancelableOnClickKeyBack(false)
//        dialog.show()
//
//        binding.tvConfirm.setOnClickListener {
//            val checkType = if (binding.gpGroup.checkedRadioButtonId == R.id.rb_single) 1 else 2
//            onConfirmClick.invoke(checkType)
//            dialog.dismiss()
//            KeyboardUtils.hideSoftInput(it)
//        }
//        //不加这段布局就扭曲~
//        val layoutParams = dialog.contentView!!.layoutParams
//        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
//        layoutParams.width = (ScreenUtils.getAppScreenWidth() * 0.8).toInt()
//        return dialog
//    }


}