package com.simplewen.win0.wd.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout
import android.widget.TextView
import com.simplewen.win0.wd.R
import com.simplewen.win0.wd.app.CloudApp


/**
 * 当前借阅状态:Adapter
 */
class BrowListViewAdapter(private val BrowData:ArrayList<Map<String,Any>>): BaseAdapter(){
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var vi:View? = null
        if(convertView!=null){
            vi = convertView
        }else{
             vi = LinearLayout.inflate(CloudApp._context,R.layout.brow_list,null)
        }
        with(vi){

            this!!.findViewById<TextView>(R.id.b_title).text = BrowData[position]["b_title"].toString()
           findViewById<TextView>(R.id.b_author).text = "作者：${BrowData[position]["b_author"].toString()}"
           findViewById<TextView>(R.id.b_status).text = "续借次数：${BrowData[position]["b_status"].toString()}"
           findViewById<TextView>(R.id.b_startTime).text = "借于：${BrowData[position]["b_startTime"].toString()}"
            findViewById<TextView>(R.id.b_stopTime).text = "限还：${BrowData[position]["b_stopTime"].toString()}"

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
        return BrowData.size
    }
}