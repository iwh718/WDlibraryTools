package com.simplewen.win0.wd.activity


import android.content.Intent


import android.support.design.widget.NavigationView
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.view.*
import kotlinx.android.synthetic.main.activity_wd_main.*
import android.graphics.Color
import android.net.Uri
import android.os.*
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v4.view.ViewPager

import android.widget.*
import com.simplewen.win0.wd.*
import com.simplewen.win0.wd.Adapter.ViewPageAdapter

import com.simplewen.win0.wd.Fragment.LibraryIndex
import com.simplewen.win0.wd.Fragment.MyBrow
import com.simplewen.win0.wd.Fragment.MyHistory

import com.simplewen.win0.wd.base.BaseActivity
import com.simplewen.win0.wd.modal.iwhDataOperator

import com.simplewen.win0.wd.request.WorkWd
import com.simplewen.win0.wd.util.Utils
import kotlinx.android.synthetic.main.activity_libraryweb.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_wd_main.*

import kotlinx.coroutines.ExperimentalCoroutinesApi

/**
 * 该项目使用Kotlin1.3版本
 * 协程1.0
 * first time：18.09.02
 * last time： 19.04.24
 * 交流群：594869854
 * tips：项目可以自行编译修改适配：禁止商业相关使用，本项目仅为文达学院使用（分支版本为安徽信息工程学院，由于一些原因，项目暂停！）
 * @author IWH
 * 文达学院：17 软件2
 */
@ExperimentalCoroutinesApi
class WDMain : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {


    private var temDialog: AlertDialog? = null//临时dialog对象，用来调用dismiss
    private var userNameText: TextView? = null
    private lateinit var libraryWeb: LibraryIndex
    private val zw = "http://wap.cnki.net/touch/web/guide"
    private val wf = "http://www.wanfangdata.com.cn/index.html"
    private val dx = "http://www.duxiu.com/"
    private val cx ="https://m.cxstar.com/"
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wd_main)
        setSupportActionBar(toolbar)
        toolbar.setTitleTextColor(Color.WHITE)

        WorkWd.getNotice(this@WDMain)//获取公告
        //获取登录名
        userNameText = findViewById<NavigationView>(R.id.nav_view)//获取导航
                .getHeaderView(0)//获取头部
                .findViewById<LinearLayout>(R.id.nav_header)//获取头部布局
                .findViewById(R.id.userName)
        userNameText!!.text = "已登录，${WorkWd.userName}"

        //设置Tab面板
        main_tab.apply {
            setSelectedTabIndicatorColor(ContextCompat.getColor(this@WDMain, R.color.white))
            setTabTextColors(Color.WHITE, Color.WHITE)
            addTab(this.newTab().setText("图书馆动态"))
            addTab(this.newTab().setText("当前借阅"))
            addTab(this.newTab().setText("历史借阅"))

            addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabReselected(tab: TabLayout.Tab?) = Unit
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    main_viewPage.currentItem = tab!!.position
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) = Unit
            })
        }

        //设置ViewPage
        this@WDMain.libraryWeb = LibraryIndex()
        val fgList = arrayListOf(this@WDMain.libraryWeb, MyBrow(), MyHistory())
        main_viewPage.apply {
            offscreenPageLimit = 2
            this.adapter = ViewPageAdapter(supportFragmentManager, fgList)
            this.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(state: Int) = Unit
                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) = Unit
                override fun onPageSelected(position: Int) {
                    main_tab.getTabAt(position)!!.select()

                }
            })
        }

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        nav_view.setNavigationItemSelectedListener(this)

        //查看公告
        noticeLayout.setOnClickListener {
            startActivity(Intent(this, getNotice::class.java))
        }
        //绑定搜索按钮
        search_btn.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            var s_key: String? = null
            override fun onQueryTextSubmit(query: String?): Boolean {
                with(Intent(this@WDMain, searchBooks::class.java)) {
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

        //fab按钮
        fab.setOnClickListener {
            AlertDialog.Builder(this@WDMain)
                    .setTitle("加入图书馆交流群")
                    .setMessage("可以查看图书馆最近的状态与管理员交流！")
                    .setPositiveButton("确认") { _, _ ->
                        Utils.joinQQGroup()
                    }
                    .setNegativeButton("取消", null)
                    .create().show()
        }


        //设置顶层的webView
        val tv_listener = View.OnClickListener {
            val url = when (it.id) {
                R.id.tv_zw ->  zw
                R.id.tv_dx ->  dx
                R.id.tv_wf ->  wf
                R.id.tv_cx -> cx
                else ->{
                    Toast.makeText(this@WDMain,"参数有误！",Toast.LENGTH_SHORT).show()
                    ""
                }
            }
            Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                startActivity(this)
            }

        }
        tv_zw.setOnClickListener(tv_listener)
        tv_dx.setOnClickListener(tv_listener)
        tv_wf.setOnClickListener(tv_listener)
        tv_cx.setOnClickListener(tv_listener)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
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


    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            Intent().apply {
                action = Intent.ACTION_MAIN
                addCategory(Intent.CATEGORY_HOME)
                startActivity(this)
            }

        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            //关于应用
            R.id.nav_about -> {
                startActivity(Intent(this@WDMain, About::class.java))
            }
            //检查更新
            R.id.nav_updata -> {
                Utils.requestUpVersion(this@WDMain)
            }
            //期刊检索
            R.id.nav_qk -> {
                startActivity(Intent(this@WDMain, SearchJournal::class.java))
            }
            //开放时间
            R.id.nav_openTime -> {
                startActivity(Intent(this@WDMain, LibraryOpenTime::class.java))
            }
            //退出登陆
            R.id.menu_my -> {
                val dialog = AlertDialog.Builder(this)
                dialog.setTitle("要重新登录吗？")
                        .setNegativeButton("取消", null)
                        .setPositiveButton("确认") { _, _ ->
                            iwhDataOperator.setSHP("flag", "", "wd")
                            startActivity(Intent(this@WDMain, LoginActivity::class.java).apply {
                                this.flags  = Intent.FLAG_ACTIVITY_CLEAR_TOP
                            })
                            finish()
                        }
                        .create()
                dialog.show()
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
                    if (user.isNotEmpty() && pwNew.isNotEmpty() && pwOld.isNotEmpty()) {
                        iwhDataOperator.setSHP("flag", "", "wd")
                        WorkWd.modifyPass(user, pwNew, pwOld, this@WDMain)//提交修改
                    } else {
                        Toast.makeText(this@WDMain, "请完善信息", Toast.LENGTH_SHORT).show()
                    }
                }


            }

        }


        return true
    }

    //监听webview的返回事件
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && libraryWeb.webView.canGoBack()) {
            libraryWeb.webView.goBack()//返回上个页面
            return true
        }
        return super.onKeyDown(keyCode, event)//退出整个应用程序
    }


}
