package com.simplewen.win0.wd.Fragment


import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView
import android.widget.SimpleAdapter
import com.simplewen.win0.wd.R
import com.simplewen.win0.wd.app.WdTools
import com.simplewen.win0.wd.base.BaseActivity
import com.simplewen.win0.wd.request.WorkWd
import com.simplewen.win0.wd.util.Utils
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

/**
 * 我的借阅
 */
@ExperimentalCoroutinesApi
class MyBrow : Fragment() {

    private lateinit var Coroutines: BaseActivity
    //数据Id
    private val b_id = intArrayOf(R.id.b_name, R.id.b_last, R.id.b_next, R.id.b_continue, R.id.b_id)
    //数据key
    private val b_info = arrayOf("b_name", "b_last", "b_next", "b_continue", "b_id")



    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val ly = inflater!!.inflate(R.layout.activity_brow, null)
        val tv_booklist = ly.findViewById<ListView>(R.id.brow_list)//引用listview的书列表
        val listadapter = SimpleAdapter(activity,WorkWd.books, R.layout.booklist, b_info, b_id)
        tv_booklist.adapter = listadapter //listview适配器
        if (WorkWd.books.size <= 0) {
            Utils.Tos("你现在没有借书哦！")
        }
        //弹出续借对话框
        tv_booklist.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            AlertDialog.Builder(activity).setTitle("你最多只能续借一次（30天）哦！").setNegativeButton("取消", null).setCancelable(true)
                    .setPositiveButton("确认") { _, _ ->
                        Coroutines.launch {
                            //Log.d("@@开始续借", "---------")
                            WorkWd.myContinue(WorkWd.books[position]["b_id"].toString(), Coroutines)
                        }
                    }.create().show()
        }

        val refresh = ly.findViewById<SwipeRefreshLayout>(R.id.refresh)//下拉刷新组件
        refresh.setColorSchemeResources(R.color.colorAccent)

        refresh.setOnRefreshListener {
            //启动刷新协程
            Coroutines.launch {
                WorkWd.myBrow( Coroutines, refresh, listadapter)
            }
        }
        Coroutines.launch {
            WorkWd.myBrow( Coroutines, refresh, listadapter)
        }
        return ly
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        this.Coroutines = context as BaseActivity

    }
}