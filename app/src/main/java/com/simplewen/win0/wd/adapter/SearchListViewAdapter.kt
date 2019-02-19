package com.simplewen.win0.wd.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout
import android.widget.TextView
import com.simplewen.win0.wd.R
import com.simplewen.win0.wd.app.CloudApp

/**
 * 搜索列表
 */
class SearchListViewAdapter(private val searchData:ArrayList<Map<String,Any>>):BaseAdapter(){
    override fun getCount(): Int {
       return searchData.size
    }

    override fun getItem(position: Int): Any {
        return searchData[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var vi:View? = null
        if(convertView!=null){
            vi = convertView
        }else{
            vi = LinearLayout.inflate(CloudApp._context, R.layout.search_list,null)
        }
        with(vi){

            this!!.findViewById<TextView>(R.id.b_title).text = searchData[position]["b_title"].toString()
            findViewById<TextView>(R.id.b_author).text = "作者：${searchData[position]["b_author"].toString()}"
            findViewById<TextView>(R.id.b_searchId).text = "索取号：${searchData[position]["b_searchId"].toString()}"
            findViewById<TextView>(R.id.b_by).text = "出版社：${searchData[position]["b_by"].toString()}"
            findViewById<TextView>(R.id.b_time).text = "出版日期：${searchData[position]["b_time"].toString()}"

        }

        return vi!!
    }
}