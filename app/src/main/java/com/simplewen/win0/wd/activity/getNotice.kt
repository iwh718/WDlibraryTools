package com.simplewen.win0.wd.activity

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.support.v4.widget.SwipeRefreshLayout
import android.util.Log
import android.view.*
import android.widget.*
import com.simplewen.win0.wd.R
import kotlinx.android.synthetic.main.activity_get_notice.*
import request.requestManage

class getNotice :AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_notice)
        val expandListView = findViewById<ExpandableListView>(R.id.notice_listView)
        val request = requestManage(this@getNotice)
        val refresh = findViewById<SwipeRefreshLayout>(R.id.notice_refresh)//下拉刷新
        refresh?.setColorSchemeResources(R.color.colorAccent)

        setSupportActionBar(toolbar)
        supportActionBar?.title = "通知公告"

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        // 创建一个BaseExpandableListAdapter对象

        val adapter = object : BaseExpandableListAdapter()
        {


            private val textView: TextView
                get()
                {
                    val textView = TextView(this@getNotice)
                    val lp = AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT)
                    textView.layoutParams = lp
                    textView.gravity = Gravity.CENTER_VERTICAL or Gravity.START
                    textView.setPadding(36, 10, 0, 10)
                    textView.textSize = 15f

                    return textView
                }

            // 获取指定组位置、指定子列表项处的子列表项数据
            override fun getChild(groupPosition: Int, childPosition: Int): Any
            {
                Log.d("@@getChild:",request.notices[groupPosition][childPosition])
                return request.notices[groupPosition][childPosition]
            }

            override fun getChildId(groupPosition: Int, childPosition: Int): Long
            {
                return childPosition.toLong()
            }

            override fun getChildrenCount(groupPosition: Int): Int
            {     Log.d("@@getChildCount:",request.notices[groupPosition].size.toString())
                return request.notices[groupPosition].size
            }

            // 该方法决定每个子选项的外观
            override fun getChildView(groupPosition: Int, childPosition: Int,
                                      isLastChild: Boolean, convertView: View?, parent: ViewGroup): View
            {


                val textView: TextView = this.textView
                    textView.text = getChild(groupPosition, childPosition).toString()


                return textView
            }

            // 获取指定组位置处的组数据
            override fun getGroup(groupPosition: Int): Any
            {        Log.d("@@getGroup:",request.noticeTitle[groupPosition])
                return request.noticeTitle[groupPosition]
            }

            override fun getGroupCount(): Int
            {     Log.d("@@getGroupCount:",request.noticeTitle.size.toString())
                return request.noticeTitle.size
            }

            override fun getGroupId(groupPosition: Int): Long
            {
                return groupPosition.toLong()
            }



            // 该方法决定每个组选项的外观
            override fun getGroupView(groupPosition: Int, isExpanded: Boolean,
                                      convertView: View?, parent: ViewGroup): View
            {
                val ll: LinearLayout
                if (convertView == null)
                {
                    ll = layoutInflater.inflate(R.layout.ex_listview_parent,null) as LinearLayout

                    val textView = this.textView
                    textView.setTextColor(Color.BLACK)
                    textView.setSingleLine(true)
                    textView.maxEms = 20
                    textView.text = getGroup(groupPosition).toString()
                    ll.addView(textView)
                }
                else
                {
                    ll = convertView as LinearLayout
                }
                return ll
            }

            override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean
            {
                return true
            }

            override fun hasStableIds(): Boolean
            {
                return true
            }
        }
        val handle = object:Handler(Looper.getMainLooper()){
            override fun handleMessage(msg: Message?) {
                super.handleMessage(msg)
                when(msg?.what){
                    0 ->{
                            Toast.makeText(this@getNotice,"请求失败",Toast.LENGTH_SHORT).show()
                    }
                    0x14->{
                        //
                        if(refresh.isRefreshing){
                            Toast.makeText(this@getNotice,"刷新成功",Toast.LENGTH_SHORT).show()
                            refresh.isRefreshing = false
                        }

                        Log.d("request",request.noticeTitle.toString())
                        adapter.notifyDataSetChanged()


                    }
                    2 ->{

                    }
                }
            }


        }


        refresh?.setOnRefreshListener {
            request.getNotice(handle)

        }
        expandListView.setAdapter(adapter)
        request.getNotice(handle)
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        //
        when(item?.itemId){
            android.R.id.home ->{
                finish()
            }
        }
        return true
    }


}
