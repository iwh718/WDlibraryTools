package com.simplewen.win0.wd.Adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

//继承fragmentPageAdapter，实现两个方法
class ViewPageAdapter(fm: FragmentManager,private  val fg_list:ArrayList<Fragment>): FragmentPagerAdapter(fm){

    override fun getCount(): Int {
        return fg_list.size

    }

    override fun getItem(position: Int): Fragment {
        //返回当前页面的fragment页面
        return fg_list[position]
    }

}