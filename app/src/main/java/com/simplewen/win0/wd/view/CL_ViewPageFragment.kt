package com.simplewen.win0.wd.view

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.widget.ListView
import com.simplewen.win0.wd.R
import com.simplewen.win0.wd.adapter.BrowListViewAdapter
import com.simplewen.win0.wd.app.CloudApp

/**
 *ViewPage布局
 */
class CL_ViewPageFragment:Fragment(){
    var _context: CallBack? = null
    /**
     * 主界面回调
     * 处理主活动与Fragment通信
     */
    interface CallBack {
        var indexData:ArrayList<ArrayList<Map<String,Any>>>?

    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var indexLy:View? = null
        val type = arguments!!.getInt("type")
        when(type){
            //借阅
            0 ->{
               indexLy = inflater.inflate(R.layout.my_brow_fragment, null)
                val browListView = indexLy!!.findViewById<ListView>(R.id.myBrowListView)
                browListView.divider = null
                browListView.adapter = BrowListViewAdapter(CloudApp.temArrayData!!)
            }
            //历史
            1 ->{
                indexLy = inflater.inflate(R.layout.my_history_fragment, null)
                val historyListView = indexLy!!.findViewById<ListView>(R.id.myHistoryListView)
                historyListView.divider = null
            }

        }


        return indexLy
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
       // _context = context as CallBack

    }
}