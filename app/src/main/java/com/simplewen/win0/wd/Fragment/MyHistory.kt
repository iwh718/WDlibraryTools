package com.simplewen.win0.wd.Fragment


import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.SimpleAdapter
import com.simplewen.win0.wd.R
import com.simplewen.win0.wd.app.WdTools
import com.simplewen.win0.wd.base.BaseActivity
import kotlinx.coroutines.ExperimentalCoroutinesApi

/**
 * 我的借阅历史
 */
@ExperimentalCoroutinesApi
class MyHistory : Fragment() {
    private lateinit var coroutines:BaseActivity
    private val b_info = arrayOf("h_name", "h_number")
    private val b_id = intArrayOf(R.id.h_name, R.id.h_number)
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val ly = inflater!!.inflate(R.layout.activity_history, null)
        val historyListView=ly.findViewById<ListView>(R.id.history_list)//借阅历史列表
        val historyListViewAdapter = SimpleAdapter(activity, WdTools.MainRequest.allHistoryBooks, R.layout.history_list, b_info, b_id)
        historyListView.adapter = historyListViewAdapter //listview适配器
        WdTools.MainRequest.myHisory(historyListViewAdapter,coroutines)
        return ly
    }
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        this.coroutines = context as BaseActivity
    }

}