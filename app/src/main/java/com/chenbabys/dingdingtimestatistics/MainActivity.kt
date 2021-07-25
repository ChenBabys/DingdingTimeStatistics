package com.chenbabys.dingdingtimestatistics
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.LogUtils
import com.chenbabys.dingdingtimestatistics.base.BaseActivity
import com.chenbabys.dingdingtimestatistics.base.BaseViewModel
import com.chenbabys.dingdingtimestatistics.databinding.ActivityMainBinding
import com.chenbabys.dingdingtimestatistics.databinding.ItemMainListHeadViewBinding

class MainActivity : BaseActivity<BaseViewModel,ActivityMainBinding>() {
    private val adapter by lazy { MainListAdapter() }

    override fun initView() {
        with(binding){
            rvContent.layoutManager= LinearLayoutManager(mContext)
            rvContent.adapter = adapter
        }
        val dateList = CalenderUtil.getDateEntities()
        adapter.addHeaderView(ItemMainListHeadViewBinding.inflate(layoutInflater).root)
        adapter.setList(dateList)
    }

    override fun initVm() {

    }

}