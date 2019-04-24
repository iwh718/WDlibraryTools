package com.simplewen.win0.wd.Adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

//继承fragmentPageAdapter，实现两个方法
class ViewPageAdapter(fm: FragmentManager, fg_list:ArrayList<Fragment>): FragmentPagerAdapter(fm){
    private var listFg = arrayListOf<Fragment>()
    init {listFg = fg_list}
    override fun getCount(): Int {
        return listFg.size

    }

    override fun getItem(position: Int): Fragment {
        //返回当前页面的fragment页面
        return listFg[position]
    }

}