package com.simplewen.win0.wd.activity

import android.graphics.Color
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.util.Log
import android.view.*
import android.widget.*
import com.simplewen.win0.wd.R
import com.simplewen.win0.wd.base.BaseActivity
import com.simplewen.win0.wd.request.WorkWd
import kotlinx.android.synthetic.main.activity_get_notice.*
import kotlinx.coroutines.ExperimentalCoroutinesApi

/**
 * 获取公告
 */
@ExperimentalCoroutinesApi
class getNotice :BaseActivity(){

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_notice)
        val expandListView = findViewById<ExpandableListView>(R.id.notice_listView)

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
                Log.d("@@getChild:", WorkWd.notices[groupPosition][childPosition])
                return WorkWd.notices[groupPosition][childPosition]
            }

            override fun getChildId(groupPosition: Int, childPosition: Int): Long
            {
                return childPosition.toLong()
            }

            override fun getChildrenCount(groupPosition: Int): Int
            {     Log.d("@@getChildCount:",WorkWd.notices[groupPosition].size.toString())
                return WorkWd.notices[groupPosition].size
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
            {     //   Log.d("@@getGroup:",WorkWd.noticeTitle[groupPosition])
                return WorkWd.noticeTitle[groupPosition]
            }

            override fun getGroupCount(): Int
            {     Log.d("@@getGroupCount:",WorkWd.noticeTitle.size.toString())
                return WorkWd.noticeTitle.size
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
                    textView.text = "${groupPosition + 1}.${getGroup(groupPosition).toString()}"
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

        refresh?.setOnRefreshListener { WorkWd.getNotice(this@getNotice,adapter) }
        expandListView.setAdapter(adapter)
        WorkWd.getNotice(this@getNotice,adapter)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            android.R.id.home ->{
                finish()
            }
        }
        return true
    }


}
