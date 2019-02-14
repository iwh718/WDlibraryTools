package com.simplewen.win0.wd.activity


import android.app.Activity

import android.content.Intent


import android.support.design.widget.NavigationView

import android.support.v4.view.GravityCompat

import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.*

import kotlinx.android.synthetic.main.activity_wd_main.*
import request.requestManage

import android.graphics.Color
import android.os.*
import android.support.design.widget.TextInputEditText
import android.support.v4.widget.SwipeRefreshLayout
import android.text.TextUtils
import android.util.Log
import android.widget.*
import com.simplewen.win0.wd.*

import com.simplewen.win0.wd.libraryweb.libraryQk

import com.simplewen.win0.wd.libraryweb.libraryweb
import com.simplewen.win0.wd.modal.iwhDataOperator
import com.simplewen.win0.wd.util.Utils
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_wd_main2.*
import java.util.*
import kotlin.collections.ArrayList

/**
 * @author:IWH
 * QQ:2868579699
 * 云朵图书馆 安徽信息工程学院适配版
 * 校园工具，非盈利项目，非礼勿扰
 * Yao 2019.02.14
 */

class AXGmain : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    val iwh = "校园工具，非盈利项目，非礼勿扰"
    val slideNotice = Timer()//定时器
    val request = requestManage(this@AXGmain)//实例化请求对象
    var b_id = request.b_id//listview id
    var logined: Int = 0//是否历史登录标志
    private var boreUser: String = ""
    private var borePw: String = ""
    var temDialog: AlertDialog? = null//临时dialog对象，用来调用dismiss
    var temLoad: AlertDialog? = null //加载组件
    var userNameText: TextView? = null
    lateinit var mainFresh: SwipeRefreshLayout //下拉刷新
    //lateinit var noticeTextSwitcher: TextSwitcher  //公告滚动
    lateinit var noticeTitle: ArrayList<String>//公告内容
    //handler接收网络线程数据
    val hand: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            when (msg?.what) {
                0 -> {
                    temLoad?.dismiss()
                    mainFresh.isRefreshing = false

                }
                1 -> {
                    temDialog?.dismiss()
                    Utils.Tos("登录成功，欢迎您:${request.userName}")
                    userNameText?.text = "欢迎你,${request.userName}"
                    request.myBrow(this)
                    temLoad?.dismiss()//移除加载
                    request.loginFlag = 1
                    logined = 1
                    if (getSharedPreferences("noticeFlag", Activity.MODE_PRIVATE).getInt("upflag", 0) <= 7) {
                        AlertDialog.Builder(this@AXGmain)
                                .setTitle("测试1.7").setTitle("本次更新").setMessage(R.string.upDesc)
                                .setCancelable(false).setPositiveButton("了解") { _, _ ->
                                    iwhDataOperator.setSHP("upflag", 8, "noticeFlag")
                                }.create().show()
                    }
                    //存储登录信息到本地私有目录
                    iwhDataOperator
                            .setSHP("user", msg.data.getString("user"), "wd")
                            .setSHP("pw", msg.data.getString("pw"), "wd")

                }
                2 -> {
                    Utils.Tos("登录失败：账号或密码有误")
                    logined = 0
                    temLoad?.dismiss()
                }
                3 -> {
                    temDialog?.dismiss()
                    Utils.Tos("请先登录校园网")
                    logined = 0
                    val dialog = AlertDialog.Builder(this@AXGmain)
                    dialog.setTitle("图书馆访问失败，请稍后再试！")
                            .setCancelable(false)
                            .setPositiveButton("确认") { _, _ ->

                            }
                            .create()
                    dialog.show()
                    noticeTitle = arrayListOf("网络环境异常！")
                }
                4 -> {
                    temDialog?.dismiss()
                    Toast.makeText(this@AXGmain, "网络异常", Toast.LENGTH_SHORT).show()
                    logined = 0
                    val dialog = AlertDialog.Builder(this@AXGmain)
                    dialog.setTitle("图书馆访问失败，请稍后再试！")
                            .create()
                    dialog.show()

                }
                5 -> {

                    Toast.makeText(this@AXGmain, "请补全信息！", Toast.LENGTH_SHORT).show()
                    temLoad?.dismiss()
                }
                6 -> {
                    logined = 1
                }
                7 -> {
                    Toast.makeText(this@AXGmain, "挂失失败", Toast.LENGTH_SHORT).show()
                    temDialog!!.dismiss()
                }
                //挂失图书卡
                0x11 -> {
                    Toast.makeText(this@AXGmain, "该图书证，已经挂失", Toast.LENGTH_SHORT).show()
                    temDialog!!.dismiss()
                }
                0x12 -> {
                    Toast.makeText(this@AXGmain, "信息不匹配，重新输入", Toast.LENGTH_SHORT).show()
                }
                0x13 -> {
                    Toast.makeText(this@AXGmain, "挂失成功，请到图书馆解除！", Toast.LENGTH_SHORT).show()
                    temDialog!!.dismiss()
                }
                0x14 -> {
                    //获取公告
                    noticeTitle = request.noticeTitle
                    Tools().startNotice()
                    if (mainFresh.isRefreshing) {
                        mainFresh.isRefreshing = false

                    }
                }//更换公告
                0x144 -> {
                    Log.d("@@_arg1:", noticeTitle[msg.arg1])
                    noticeTextSwitcher.setText(noticeTitle[msg.arg1])

                }
                //查询借阅失败
                0x15 -> {
                    Utils.Tos("查询借阅失败")
                }
                //查询借阅成功
                0x16 -> {

                    request.checkBook(request.books, this)
                }
                //图书到期提醒
                0x17 -> {
                    // Toast.makeText(this@AXGmain,"你有图书即将超期！",Toast.LENGTH_SHORT).show()
                    AlertDialog.Builder(this@AXGmain)
                            .setTitle("你有图书即将超期!")
                            .setMessage("请注意及时续借或还书。").setPositiveButton("去看看") { _, _ ->
                                startActivity(Intent(this@AXGmain, brow::class.java))


                            }.setNegativeButton("知道了") { _, _ ->

                            }.create().show()

                }
                //修改密码成功
                0x18 -> {
                    Utils.Tos("修改成功，请重新登录")
                    logined = 0
                    temDialog!!.dismiss()


                }
                //检测更新

                0x21 -> {
                    if (msg.arg1 > Utils.getVersion(this@AXGmain)) {

                        val updateView = layoutInflater.inflate(R.layout.update_view, null)
                        updateView.findViewById<Button>(R.id.updateBtn)
                                .setOnClickListener {
                                    temDialog!!.dismiss()
                                    Utils.downNew()//下载更新
                                    Utils.Tos("请稍后查看通知栏进度！")
                                }
                        temDialog = AlertDialog.Builder(this@AXGmain).setCancelable(true)
                                .setView(updateView)
                                .create()
                        temDialog!!.show()
                    } else {
                        Utils.Tos("当前是最新版本")
                    }

                }
                0x22 -> {
                    Utils.Tos("检查失败！")
                }

            }
        }

    }


    /**内部类：检查登录，调出加载组件**/
    inner class Tools {
        fun loadDialog() {
            val dialog = AlertDialog.Builder(this@AXGmain)
            val login_layout = layoutInflater.inflate(R.layout.load, null)
            dialog.setView(login_layout).setCancelable(true).create()
            temLoad = dialog.show()
        }

        fun loginDialog() {
            val dialog = AlertDialog.Builder(this@AXGmain)
            val login_layout = layoutInflater.inflate(R.layout.login_form, null)
            val login_btn = login_layout.findViewById<Button>(R.id.sign)
            login_btn.setOnClickListener {
                val myuser: String = login_layout.findViewById<TextInputEditText>(R.id.user).text.toString()
                val mypw: String = login_layout.findViewById<TextInputEditText>(R.id.pw).text.toString()
                Tools().loadDialog()
                Toast.makeText(this@AXGmain, "登录中", Toast.LENGTH_SHORT).show()
                request.myLogin(myuser, mypw, hand)

            }
            dialog.setView(login_layout).create()
            temDialog = dialog.show()
        }

        //检测登录状态
        fun checkLogined(): Boolean {
            val temUser: String = iwhDataOperator.getSHP("user", "wd", "0")
            val temPw: String = iwhDataOperator.getSHP("pw", "wd", "0")
            if (temPw.isNotEmpty() && temPw.isNotEmpty()) {
                logined = 1
                borePw = temPw
                boreUser = temUser
                return true

            } else {
                logined = 0
                return false
            }


        }

        fun startNotice() {
            var i = 0
            //定时器自动更换公告
            slideNotice.schedule(object : TimerTask() {

                override fun run() {

                    if (i < noticeTitle.size) {
                        val message = Message()
                        message.arg1 = i
                        i++
                        message.what = 0x144
                        hand.sendMessage(message)

                    } else {
                        i = 0
                    }
                }
            }, 0, 2000)

        }


    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wd_main)
        setSupportActionBar(toolbar)
        toolbar.setTitleTextColor(Color.WHITE)
        userNameText = findViewById<NavigationView>(R.id.nav_view)//获取导航
                .getHeaderView(0)//获取头部
                .findViewById<LinearLayout>(R.id.nav_header)//获取头部布局
                .findViewById(R.id.userName)//获取登录名
        var searchMode = "1"//默认搜索模式
        var searchSort = "正题名"//默认排列
        val searchModeSpinner = findViewById<Spinner>(R.id.ModeSpinner)//获取搜索模式组件
        val searchSortSpinnerData = arrayOf("正题名", "出版日期", "作者", "出版社", "索取号")
        val searchModeSpinnerData = arrayOf("默认搜索", "模糊搜索", "精确搜索")
        val searchModeSpinnerAdapter = ArrayAdapter(this, R.layout.spinnerlayout, searchModeSpinnerData )
        val searchSortSpinnerAdapter = ArrayAdapter(this, R.layout.spinnerlayout, searchSortSpinnerData)
        //公告滚动
        noticeTextSwitcher.setFactory {
            TextView(this).apply {
                maxEms = 20
                setSingleLine(true)
                ellipsize = TextUtils.TruncateAt.MARQUEE
            }
        }
        noticeTitle = arrayListOf("2019.01.10 图书馆")

        val searchBox = findViewById<LinearLayout>(R.id.search_box)//获取搜素模式盒子
        mainFresh = findViewById(R.id.main_fresh)//下拉刷新

        //开始程序数据初始化
        request.getNotice(hand)//获取公告
        //每次打开，检测是否登录过

        if (Tools().checkLogined()) {
            request.myLogin(boreUser, borePw, hand)//登录

        } else {
            Toast.makeText(this, "你还未登录，或登录失效！", Toast.LENGTH_SHORT).show()
            Tools().loginDialog()
        }
        //获取更新
        Utils.requestUpVersion(hand)


        /**设置适配器和监听器**/
        searchModeSpinner.adapter = searchModeSpinnerAdapter
        sortSpinner.adapter = searchSortSpinnerAdapter
        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        nav_view.setNavigationItemSelectedListener(this)
        //搜索模式：设置监听器
        searchModeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                searchMode = (position + 1).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                searchMode = 1.toString()
            }
        }
        //排列方式：设置监听器
        sortSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                searchSort = searchSortSpinnerData[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                searchSort = searchSortSpinnerData[0]
            }
        }
        //查看公告
        noticeLayout.setOnClickListener {
            startActivity(Intent(this, getNotice::class.java))
        }
        //绑定搜索按钮
        search_btn.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            var s_key: String? = "bore test"
            override fun onQueryTextSubmit(query: String?): Boolean {
                with(Intent(this@AXGmain, search_login::class.java)) {
                    putExtra("search_mode", searchMode)
                    putExtra("search_sort", searchSort)
                    putExtra("search_key", s_key)
                    startActivity(this)
                }

                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                s_key = newText
                return true
            }
        })
        //搜索模式打开
        search_btn.setOnQueryTextFocusChangeListener { _, _ ->

            if (searchBox.visibility == View.GONE) {
                searchBox.visibility = View.VISIBLE

            } else {
                searchBox.visibility = View.GONE
            }

        }
        //fab按钮
        fab.setOnClickListener {
            AlertDialog.Builder(this@AXGmain)
                    .setTitle("加入图书馆交流群")
                    .setMessage("期待你的到来！")
                    .setPositiveButton("确认") { _, _ ->
                        Utils.joinQQGroup()
                    }
                    .setNegativeButton("算了", null)
                    .create().show()
        }


        //绑定index功能按钮
        grid_library.setOnClickListener {
            startActivity(Intent(this@AXGmain, libraryweb::class.java))
        }
        grid_mylibrary.setOnClickListener {
            if (logined != 1) {
                Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show()
                Tools().loginDialog()

            } else {
                AlertDialog.Builder(this@AXGmain).setItems(arrayOf("我的借阅", "我的借阅历史")) { _, which ->
                    when (which) {
                        0 -> {
                            startActivity(Intent(this, brow::class.java))//切换我的借阅
                        }
                        1 -> {
                            startActivity(Intent(this, history::class.java))//切换我的借阅历史
                        }

                    }

                }.create().show()

            }


        }

        //绑定下拉刷新
        mainFresh.setColorSchemeColors(resources.getColor(R.color.colorAccent))
        mainFresh.setOnRefreshListener {
            request.getNotice(hand)//获取公告
        }



    }


    override fun onBackPressed() {
        with(Intent()) {
            action = android.content.Intent.ACTION_MAIN
            addCategory(android.content.Intent.CATEGORY_HOME)
            startActivity(intent)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {

            //登录状态检测
            R.id.menu_my -> {
                if (logined != 1) {
                    Toast.makeText(this, "您还未登录！", Toast.LENGTH_SHORT).show()
                    Tools().loginDialog()
                } else {
                    val dialog = AlertDialog.Builder(this)
                    dialog.setTitle("要重新登录吗？")
                            .setNegativeButton("取消") { _, _ ->

                            }
                            .setPositiveButton("确认") { _, _ ->
                                Tools().loginDialog()
                            }
                            .create()
                    dialog.show()
                }
            }
            R.id.menu_modify -> {
                val modifyLayout = layoutInflater.inflate(R.layout.modifypass, null)
                val modifyBtn = modifyLayout.findViewById<Button>(R.id.menu_modify_submit)
                temDialog = AlertDialog.Builder(this)
                        .setTitle("修改密码")
                        .setView(modifyLayout)
                        .create()
                temDialog!!.show()
                modifyBtn.setOnClickListener {
                    val (user, pwNew, pwOld) = arrayOf(modifyLayout.findViewById<EditText>(R.id.menu_modify_user).text.toString(),
                            modifyLayout.findViewById<EditText>(R.id.menu_modify_mewPass).text.toString(),
                            modifyLayout.findViewById<EditText>(R.id.menu_modify_oldPass).text.toString())
                    if (user.isNotEmpty() || pwNew.isNotEmpty() || pwOld.isNotEmpty()) {
                        Toast.makeText(this@AXGmain, "请完善信息", Toast.LENGTH_SHORT).show()
                    } else {
                        request.modifyPass(user, pwNew, pwOld, hand)//提交修改
                    }
                }


            }
            R.id.nav_send -> {
                with(Intent(Intent.ACTION_SEND)) {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, "文院移动图书馆:https://www.coolapk.com/apk/com.simplewen.win0.wd")
                    startActivity(Intent.createChooser(this, "分享文院移动图书馆给小伙伴！"))
                }

            }

        }

        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {

            //关于应用
            R.id.nav_about -> {
                startActivity(Intent(this@AXGmain, about::class.java))
            }
            //检查更新
            R.id.nav_updata -> {
                Utils.requestUpVersion(hand)

            }

            //期刊检索
            R.id.nav_qk -> {
                startActivity(Intent(this@AXGmain, libraryQk::class.java))
            }



            //图书证挂失
            R.id.nav_gs -> {

                val gs_ly = layoutInflater.inflate(R.layout.guashi, null)
                val gs_name = gs_ly.findViewById<EditText>(R.id.gs_name).text
                val gs_number = gs_ly.findViewById<EditText>(R.id.gs_number).text
                val gs_password = gs_ly.findViewById<EditText>(R.id.gs_password).text
                val gs_btn = gs_ly.findViewById<Button>(R.id.gs)
                gs_btn.setOnClickListener {
                    if (gs_number.isNotEmpty() && gs_password.isNotEmpty()) {
                        request.gsCard(gs_number.toString(), gs_password.toString(), gs_name.toString(), hand)
                    } else {
                        Utils.Tos("请补全信息！")
                    }

                }

                temDialog = AlertDialog.Builder(this@AXGmain)
                        .setTitle("挂失图书证").setView(gs_ly).create()
                temDialog!!.show()
            }

        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }


}