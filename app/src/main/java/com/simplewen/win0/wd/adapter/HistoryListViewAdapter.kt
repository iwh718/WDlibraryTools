package com.simplewen.win0.wd.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout
import android.widget.TextView
import com.simplewen.win0.wd.R
import com.simplewen.win0.wd.app.CloudApp

/**
 * 我的借阅历史:Adapter
 */
class HistoryListViewAdapter(private val historyData:ArrayList<Map<String,Any>>): BaseAdapter(){
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var vi:View? = null
        if(convertView!=null){
            vi = convertView
        }else{
            vi = LinearLayout.inflate(CloudApp._context, R.layout.history_list,null)
        }
        with(vi){
            this!!.findViewById<TextView>(R.id.b_searchId).text =  historyData[position]["b_searchId"].toString()
            findViewById<TextView>(R.id.b_title).text =  historyData[position]["b_title"].toString()
            findViewById<TextView>(R.id.b_author).text =  historyData[position]["b_author"].toString()
            findViewById<TextView>(R.id.b_address).text =  historyData[position]["b_address"].toString()
            findViewById<TextView>(R.id.b_startTime).text = historyData[position]["b_startTime"].toString()
            findViewById<TextView>(R.id.b_stopTime).text =  historyData[position]["b_stopTime"].toString()

        }

        return vi!!
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItem(position: Int): Any? {
        return null
    }

    override fun getCount(): Int {
        return historyData.size
    }
}