package com.simplewen.win0.wd.activity


import android.content.Intent


import android.support.design.widget.NavigationView
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.view.*
import kotlinx.android.synthetic.main.activity_wd_main.*
import android.graphics.Color
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
import com.simplewen.win0.wd.app.WdTools
import com.simplewen.win0.wd.base.BaseActivity
import com.simplewen.win0.wd.modal.iwhDataOperator
import com.simplewen.win0.wd.request.RequestManage
import com.simplewen.win0.wd.util.Utils
import kotlinx.android.synthetic.main.activity_libraryweb.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_wd_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.MainScope
import kotlin.collections.ArrayList
import kotlin.coroutines.CoroutineContext

/**
 * 该项目使用Kotlin1.3版本
 * 协程1.0
 * first time：18.09.02
 * last time： 19.04.24
 * @author IWH
 * 文达学院：17 软件2
 */
@ExperimentalCoroutinesApi
class WDMain : BaseActivity(), CoroutineScope by MainScope(), NavigationView.OnNavigationItemSelectedListener {

    override val coroutineContext: CoroutineContext
        get() = super.coroutineContext


    var temDialog: AlertDialog? = null//临时dialog对象，用来调用dismiss
    private var userNameText: TextView? = null
    private lateinit var libraryWeb: Fragment
    private lateinit var noticeTitle: ArrayList<String>//公告内容

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wd_main)
        setSupportActionBar(toolbar)
        toolbar.setTitleTextColor(Color.WHITE)
        //实例化请求对象
        val request:RequestManage?= WdTools.getRequest()

        //获取登录名
        userNameText = findViewById<NavigationView>(R.id.nav_view)//获取导航
                .getHeaderView(0)//获取头部
                .findViewById<LinearLayout>(R.id.nav_header)//获取头部布局
                .findViewById(R.id.userName)
        userNameText!!.text = "已登录，${WdTools.MainRequest.userName}"

        //设置Tab面板
        main_tab.apply {
            setSelectedTabIndicatorColor(ContextCompat.getColor(this@WDMain, R.color.colorPrimaryDark))
            setTabTextColors(Color.WHITE, Color.WHITE)
            addTab(this.newTab().setText("图书馆动态"))
            addTab(this.newTab().setText("当前借阅"))
            addTab(this.newTab().setText("历史借阅"))
            addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabReselected(tab: TabLayout.Tab?) = Unit
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    main_tab.getTabAt(tab!!.position)!!.select()
                    main_viewPage.currentItem = tab.position

                }

                override fun onTabUnselected(tab: TabLayout.Tab?) = Unit
            })
        }

        //设置ViewPage
        main_viewPage.apply {
            offscreenPageLimit = 2
            libraryWeb = LibraryIndex()
            this.adapter = ViewPageAdapter(supportFragmentManager, arrayListOf(libraryWeb, MyBrow(), MyHistory()))
            this.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(state: Int) = Unit
                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) = Unit
                override fun onPageSelected(position: Int) {
                    main_tab.getTabAt(position)!!.select()

                }
            })
        }


        //设置搜索组件
        var searchMode = "1"//默认搜索模式
        var searchSort = "正题名"//默认排列
        val searchModeSpinner = findViewById<Spinner>(R.id.ModeSpinner)//获取搜索模式组件
        val searchSortSpinnerData = arrayOf("正题名", "出版日期", "作者", "出版社", "索取号")
        val searchModeSpinnerData = arrayOf("默认搜索", "模糊搜索", "精确搜索")
        val searchModeSpinnerAdapter = ArrayAdapter(this, R.layout.spinnerlayout, searchModeSpinnerData)
        val searchSortSpinnerAdapter = ArrayAdapter(this, R.layout.spinnerlayout, searchSortSpinnerData)
        noticeTitle = arrayListOf("2019.01.10 图书馆")
        val searchBox = findViewById<LinearLayout>(R.id.search_box)//获取搜素模式盒子


        request?.getNotice(this@WDMain)//获取公告


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
                with(Intent(this@WDMain, searchBooks::class.java)) {
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
            AlertDialog.Builder(this@WDMain)
                    .setTitle("加入图书馆交流群")
                    .setMessage("期待你的到来！")
                    .setPositiveButton("确认") { _, _ ->
                        Utils.joinQQGroup()
                    }
                    .setNegativeButton("算了", null)
                    .create().show()
        }
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
        // drawer_layout.closeDrawer(GravityCompat.START)
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
                            startActivity(Intent(this@WDMain, LoginActivity::class.java))
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
                        WdTools.MainRequest.modifyPass(user, pwNew, pwOld, this@WDMain)//提交修改
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
        if (keyCode == KeyEvent.KEYCODE_BACK && libraryWeb.libraryWebView.canGoBack()) {
            libraryWeb.libraryWebView!!.goBack()//返回上个页面
            return true
        }
        return super.onKeyDown(keyCode, event)//退出整个应用程序

    }




}
