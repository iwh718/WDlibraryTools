package com.simplewen.win0.wd.view

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.widget.ListView
import android.widget.TextView
import com.simplewen.win0.wd.R
import com.simplewen.win0.wd.activity.CloudLibraryMain
import com.simplewen.win0.wd.adapter.BrowListViewAdapter
import com.simplewen.win0.wd.adapter.HistoryListViewAdapter
import com.simplewen.win0.wd.app.CloudApp
import com.simplewen.win0.wd.util.Utils
import kotlinx.android.synthetic.main.my_brow_fragment.*
import request.requestCLoudLibrary

/**
 *ViewPage布局
 */
class CL_ViewPageFragment:Fragment(){
    lateinit var browAdapter:BrowListViewAdapter
    lateinit var historyAdapter:HistoryListViewAdapter
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val handVPG = Handler{
            when(it.what){
                0x1->{
                    Utils.Tos("刷新完成！")
                        browSwipe?.isRefreshing = false
                        historySwipe?.isRefreshing = false
                        browAdapter.notifyDataSetChanged()
                        historyAdapter.notifyDataSetChanged()
                }
                0x2 ->{
                    Utils.Tos("刷新失败！")
                    browSwipe?.isRefreshing = false
                    historySwipe?.isRefreshing = false
                }
            }
            true
        }

        val type = arguments!!.getInt("type")
        val indexLy = inflater.inflate(R.layout.my_brow_fragment, null)
        val browSwipeRefreshLayout = indexLy.findViewById<SwipeRefreshLayout>(R.id.browSwipe).apply {
            this.setColorSchemeColors(CloudApp.getContext().getColor(R.color.colorPrimary))
        }
        val historyRefreshLayout = indexLy.findViewById<SwipeRefreshLayout>(R.id.historySwipe).apply {
            this.setColorSchemeColors(CloudApp.getContext().getColor(R.color.colorPrimary))
        }
        //处理我的借阅
        val browListView = indexLy!!.findViewById<ListView>(R.id.myBrowListView)
        browListView.divider = null
        browAdapter = BrowListViewAdapter(CloudApp.requestAll!!.currentBooks)
        browListView.adapter = browAdapter
        if(CloudApp.requestAll!!.currentBooks.size == 0){
            indexLy.findViewById<TextView>(R.id.browFlagText).visibility = View.VISIBLE
        }
        //处理历史借阅
        val historyListView = indexLy.findViewById<ListView>(R.id.myHistoryListView)
        historyAdapter = HistoryListViewAdapter(CloudApp.requestAll!!.historyBooks)
        historyListView.adapter = historyAdapter
        historyListView.divider = null
        if(CloudApp.requestAll!!.historyBooks.size == 0){
            indexLy.findViewById<TextView>(R.id.historyFlagText).visibility = View.VISIBLE
        }
        when(type){
            //借阅
            0 ->{
                historyRefreshLayout.visibility = View.GONE
            }
            //历史
            1 ->{
                browSwipeRefreshLayout.visibility = View.GONE
            }

        }
        val swipeOnlistener = SwipeRefreshLayout.OnRefreshListener{
            requestCLoudLibrary().Login(handVPG,CloudApp.requestAll!!.userInfo["userAccount"].toString(),CloudApp.requestAll!!.userInfo["userPassword"].toString())
        }
        browSwipeRefreshLayout.setOnRefreshListener(swipeOnlistener)
        historyRefreshLayout.setOnRefreshListener(swipeOnlistener)

        return indexLy
    }


}