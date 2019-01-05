package com.simplewen.win0.wd


import android.app.Activity

import android.content.Intent


import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.view.GravityCompat

import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.*
import android.widget.*
import kotlinx.android.synthetic.main.activity_wd_main.*
import request.requestManage
import kotlinx.android.synthetic.main.content_wd_main.*
import android.graphics.Color
import android.net.Uri
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.TextInputEditText
import android.support.v4.widget.SwipeRefreshLayout

import com.simplewen.win0.wd.libraryweb.libraryQk

import com.simplewen.win0.wd.libraryweb.libraryweb
import com.simplewen.win0.wd.libraryweb.searchOthers
import com.simplewen.win0.wd.util.Utils
import kotlinx.android.synthetic.main.app_bar_main.*


//author:bore初夏
//QQ:2868579699
//time:18.09.02
//校园工具，非盈利项目，非礼勿扰
//喜欢那个文，2018.09.20
class WDMain : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    val request = requestManage(this@WDMain)//实例化请求对象
    var b_id = request.b_id//listview id
    var logined: Int = 0//是否历史登录标志
    private var boreUser: String = ""
    private var borePw: String = ""
    var temDialog:AlertDialog? = null//临时dialog对象，用来调用dismiss
    var temLoad:AlertDialog? =null //加载组件
    var userNameText:TextView? = null
    lateinit var mainFresh:SwipeRefreshLayout //下拉刷新
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
                   Utils.Tos( "登录成功，欢迎您:${request.userName}")
                    userNameText?.text = "欢迎你,${request.userName}"
                    request.myBrow(this)
                    temLoad?.dismiss()//移除加载
                    request.loginFlag = 1
                    logined = 1
                    if(getSharedPreferences("noticeFlag",Activity.MODE_PRIVATE).getInt("upflag",0) <= 3){
                        AlertDialog.Builder(this@WDMain)
                                .setTitle("测试1.5").setTitle("本次更新").setMessage(R.string.upDesc)
                                .setCancelable(false).setPositiveButton("了解"){
                                    _,_ ->
                                    val shareNotice = getSharedPreferences("noticeFlag", Activity.MODE_PRIVATE)
                                    shareNotice.edit().putInt("upflag",4).apply()
                                }.create().show()
                    }
                    val shareP = getSharedPreferences("wd", Activity.MODE_PRIVATE)
                    val edit = shareP.edit()
                    edit.putString("user", msg.data.getString("user"))
                    edit.putString("pw", msg.data.getString("pw"))
                    edit.apply()
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
                    val dialog = AlertDialog.Builder(this@WDMain)
                    dialog.setTitle("图书馆访问失败，请稍后再试！")
                            .setCancelable(false)
                            .setPositiveButton("确认") { _, _ ->

                            }
                            .create()
                    dialog.show()
                }
                4 -> {
                    temDialog?.dismiss()
                    Toast.makeText(this@WDMain, "网络异常", Toast.LENGTH_SHORT).show()
                    logined = 0
                    val dialog = AlertDialog.Builder(this@WDMain)
                    dialog.setTitle("图书馆访问失败，程序即将退出！")
                            .setCancelable(false)
                            .setPositiveButton("确认") { _, _ ->

                            }
                            .create()
                    dialog.show()
                }
                5 -> {

                    Toast.makeText(this@WDMain, "请补全信息！", Toast.LENGTH_SHORT).show()
                    temLoad?.dismiss()
                }
                6 -> {
                    logined = 1
                }
                7 ->{
                    Toast.makeText(this@WDMain,"挂失失败",Toast.LENGTH_SHORT).show()
                    temDialog!!.dismiss()
                }
                //挂失图书卡
                0x11 ->{
                    Toast.makeText(this@WDMain,"该图书证，已经挂失",Toast.LENGTH_SHORT).show()
                    temDialog!!.dismiss()
                }
                0x12 ->{
                    Toast.makeText(this@WDMain,"信息不匹配，重新输入",Toast.LENGTH_SHORT).show()
                }
                0x13 ->{
                    Toast.makeText(this@WDMain,"挂失成功，请到图书馆解除！",Toast.LENGTH_SHORT).show()
                    temDialog!!.dismiss()
                }
                0x14 ->{
                    //获取公告

                    noticeTextView.text = request.noticeTitle[0]
                    if(mainFresh.isRefreshing){
                        mainFresh.isRefreshing = false

                    }
                }
                //查询借阅失败
                0x15 ->{
                    Utils.Tos("查询借阅失败")
                }
                //查询借阅成功
                0x16 ->{

                    request.checkBook(request.books,this)
                }
                //图书到期提醒
                0x17 ->{
                   // Toast.makeText(this@WDMain,"你有图书即将超期！",Toast.LENGTH_SHORT).show()
                    val warnBooks = AlertDialog.Builder(this@WDMain)
                            .setTitle("你有图书即将超期!")
                            .setMessage("请注意及时续借或还书。").setPositiveButton("去看看"){
                                _,_ ->
                                startActivity(Intent(this@WDMain,brow::class.java))


                            }.setNegativeButton("知道了"){
                                _,_ ->

                            }.create().show()

                }
                //修改密码成功
                0x18 ->{
                    Utils.Tos("修改成功，请重新登录")
                    logined =  0
                    temDialog!!.dismiss()


                }
                //失败
                0x19 ->{

                }
                //检测更新

                0x21 ->{
                    if(msg.arg1 >  Utils.getVersion(this@WDMain)){
                        Utils.Tos("新版本")

                            temDialog = AlertDialog.Builder(this@WDMain).setCancelable(false)
                                    .setTitle("检测到新版本！").setMessage("是否立即更新?")
                                    .setPositiveButton("确定"){
                                        _,_ ->
                                        Utils.DownNew()//下载更新
                                        Utils.Tos("请稍后查看通知栏进度！")

                                    }.setNegativeButton("取消"){
                                        _,_ ->
                                    }.create()
                            temDialog!!.show()
                        } else{
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
    inner class Tools{
        fun joinQQGroup(): Boolean {
            val intent = Intent()
            val key="ylQNSD_I5zOdD7zjgp4iHN0KUN4TKbJx"
            intent.data = Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D$key")
            // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面 //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            try {
                startActivity(intent)
                return true
            } catch (e: Exception) {
                Toast.makeText(this@WDMain,"未安装QQ或版本不支持，请手动添加",Toast.LENGTH_LONG).show()
                return false
            }

        }
        fun loadDialog(){
            val dialog = AlertDialog.Builder(this@WDMain)
            val login_layout = layoutInflater.inflate(R.layout.load,null)
            dialog.setView(login_layout).setCancelable(true).create()
            temLoad = dialog.show()
        }
        fun loginDialog(){
            val dialog = AlertDialog.Builder(this@WDMain)
            val login_layout = layoutInflater.inflate(R.layout.login_form,null)
            val login_btn = login_layout.findViewById<Button>(R.id.sign)
            login_btn.setOnClickListener{
                val myuser: String = login_layout.findViewById<TextInputEditText>(R.id.user).text.toString()
                val mypw: String = login_layout.findViewById<TextInputEditText>(R.id.pw).text.toString()
                Tools().loadDialog()
                Toast.makeText(this@WDMain,"登录中",Toast.LENGTH_SHORT).show()
                request.myLogin(myuser, mypw, hand)

            }
            dialog.setView(login_layout).create()
            temDialog = dialog.show()
        }
        //检测登录状态
        fun checkLogined(): Boolean {
            val shareP = getSharedPreferences("wd", Activity.MODE_PRIVATE)
            val temUser: String = shareP.getString("user", "")
            val temPw: String = shareP.getString("pw", "")
            if (temPw.isNotEmpty() && temPw.isNotEmpty()) {
                logined = 1
                borePw = temPw
                boreUser = temUser
                return true

            } else {
                logined == 0
                return false
            }


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
        var searchMode:String = "1"//默认搜索模式
        var searchSort:String = "正题名"//默认排列
        val fab = findViewById<FloatingActionButton>(R.id.fab)
        val beginTips = findViewById<LinearLayout>(R.id.beginTips)//获取提示布局
        val closeBtn = findViewById<Button>(R.id.closeTips)//关闭按钮
        val login_layout = layoutInflater.inflate(R.layout.load,null)//加载布局
        val searchBtn = findViewById<SearchView>(R.id.search_btn)//搜索按钮
        val noticeLayout = findViewById<LinearLayout>(R.id.noticeLayout)//公告区
        val searchSortSpinner = findViewById<Spinner>(R.id.sortSpinner)//获取搜索排序组件
        val searchModeSpinner = findViewById<Spinner>(R.id.ModeSpinner)//获取搜索模式组件
        val searchSortSpinnerData = arrayOf("正题名","出版日期","作者","出版社","索取号")
        val searchModeSpinnerData  = arrayOf("默认搜索","模糊搜索","精确搜索")
        val searchModeSpinnerAdapter = ArrayAdapter(this,R.layout.spinnerlayout,searchModeSpinnerData)
        val searchSortSpinnerAdapter = ArrayAdapter(this,R.layout.spinnerlayout, searchSortSpinnerData)


        val gridLIbrary =  findViewById<LinearLayout>(R.id.grid_library)//动态
        val gridMyLibrary =  findViewById<LinearLayout>(R.id.grid_mylibrary)//我的图书馆

        val noticeTextView = findViewById<TextView>(R.id.noticeTextView)//公告
         val searchBox =   findViewById<LinearLayout>(R.id.search_box)//获取搜素模式盒子
         mainFresh = findViewById(R.id.main_fresh)//下拉刷新



        /**电子资源**/
        /**@param searchOthers_cx 超星期刊搜索**/
        /**@param searchOthers_dx 读秀搜索**/

        val searchOthers_cx = findViewById<LinearLayout>(R.id.grid_others_cx)
        val searchOthers_dx = findViewById<LinearLayout>(R.id.grid_others_dx)
        val searchOthers_mw = findViewById<LinearLayout>(R.id.grid_others_mw)
        val searchOthers_zw = findViewById<LinearLayout>(R.id.grid_others_zw)

        val intent_search = Intent(this@WDMain,searchOthers::class.java)
        //读秀
        searchOthers_cx.setOnClickListener{

            intent_search.putExtra("webUrl","http://www.duxiu.com/")
            startActivity(intent_search)
        }
        //超星
        searchOthers_dx.setOnClickListener{
            intent_search.putExtra("webUrl","http://qikan.chaoxing.com/")
            startActivity(intent_search)
        }
        //美文
        searchOthers_mw.setOnClickListener{
            intent_search.putExtra("webUrl","http://wendaedu.com.cn/tsg/m/info_109.html")
            startActivity(intent_search)
        }
        //知网
        searchOthers_zw.setOnClickListener{
            intent_search.putExtra("webUrl","http://www.cnki.net/")
            startActivity(intent_search)
        }

        /****/
        //检测提示信息
        if(!Utils.checkFLagTips()){
            beginTips.visibility = View.GONE
        }
        request.getNotice(hand)//获取公告

        searchModeSpinner.adapter = searchModeSpinnerAdapter
        searchSortSpinner.adapter = searchSortSpinnerAdapter
        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        nav_view.setNavigationItemSelectedListener(this)
        //搜索模式：设置监听器

        searchModeSpinner.onItemSelectedListener = object :AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                searchMode = (position+1).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
               searchMode = 1.toString()
            }
        }
        //排列方式：设置监听器
        searchSortSpinner.onItemSelectedListener = object:AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                //
                searchSort = searchSortSpinnerData[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                //
                searchSort = searchSortSpinnerData[0]
            }
        }
        noticeLayout.setOnClickListener{
            startActivity(Intent(this,getNotice::class.java))
        }

        //每次打开，检测是否登录过
        if (Tools().checkLogined()) {
            request.myLogin(boreUser, borePw, hand)//登录


        } else {
            Toast.makeText(this,"你还未登录，或登录失效！",Toast.LENGTH_SHORT).show()
            Tools().loginDialog()
        }


        //绑定搜索按钮
        searchBtn.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            var s_key: String? = "bore test"
            override fun onQueryTextSubmit(query: String?): Boolean {

                val intent5 = Intent(this@WDMain, search_login::class.java)
                intent5.putExtra("search_mode",searchMode)
                intent5.putExtra("search_sort",searchSort)
                intent5.putExtra("search_key", s_key)
                startActivity(intent5)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                s_key = newText
                return true
            }
        })
        searchBtn.setOnQueryTextFocusChangeListener{
            _,_ ->
            if(searchBox.visibility == View.GONE){
                searchBox.visibility = View.VISIBLE


            }else{
                searchBox.visibility = View.GONE
            }

        }
        //fab按钮
        fab.setOnClickListener{
            Tools().joinQQGroup()
        }

        //绑定提示按钮
        closeBtn.setOnClickListener{
            beginTips.visibility = View.GONE
            Utils.closeTips()
        }
        //绑定grid按钮
        gridLIbrary.setOnClickListener{
            startActivity(Intent(this@WDMain,libraryweb::class.java))
        }
        gridMyLibrary.setOnClickListener{
            if (logined != 1) {
                Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show()
                Tools().loginDialog()

            }else{
                AlertDialog.Builder(this@WDMain).setItems(arrayOf("我的借阅","我的借阅历史","图书证挂失")){
                    _,which  ->
                    when(which){
                        0 -> {

                            startActivity( Intent(this, brow::class.java))//切换我的借阅
                        }
                        1 ->{
                            startActivity( Intent(this, history::class.java))//切换我的借阅历史
                        }
                        2-> {
                            val gs_ly = layoutInflater.inflate(R.layout.guashi,null)
                            val gs_name =  gs_ly.findViewById<EditText>(R.id.gs_name).text
                            val gs_number = gs_ly.findViewById<EditText>(R.id.gs_number).text
                            val gs_password = gs_ly.findViewById<EditText>(R.id.gs_password).text
                            val gs_btn = gs_ly.findViewById<Button>(R.id.gs)
                            gs_btn.setOnClickListener{

                                request.gsCard(gs_number.toString(),gs_password.toString(),gs_name.toString(),hand)
                            }

                            temDialog  = AlertDialog.Builder(this@WDMain)
                                    .setTitle("挂失图书证").setView(gs_ly).create()
                            temDialog!!.show()
                        }
                    }

                }.setTitle("选择功能").create().show()

            }


        }

        //绑定下拉刷新
        mainFresh.setColorSchemeColors(resources.getColor(R.color.colorAccent))
        mainFresh.setOnRefreshListener {
                request.getNotice(hand)//获取公告
        }


    }
    override fun onBackPressed() {
                val intent = Intent()
                intent.action = Intent.ACTION_MAIN
                intent.addCategory(Intent.CATEGORY_HOME)
                startActivity(intent)
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
            R.id.menu_modify ->{
                val modifyLayout = layoutInflater.inflate(R.layout.modifypass,null)

                val modifyBtn = modifyLayout.findViewById<Button>(R.id.menu_modify_submit)
                temDialog = AlertDialog.Builder(this)
                        .setTitle("修改密码")
                        .setView(modifyLayout)
                        .create()
                temDialog!!.show()
                modifyBtn.setOnClickListener{
                    val (user,pwNew,pwOld) = arrayOf(modifyLayout.findViewById<EditText>(R.id.menu_modify_user).text.toString(),
                            modifyLayout.findViewById<EditText>(R.id.menu_modify_mewPass).text.toString(),
                            modifyLayout.findViewById<EditText>(R.id.menu_modify_oldPass).text.toString())
                    if(user.length < 1 || pwNew.length <1 || pwOld.length <1){
                        Toast.makeText(this@WDMain,"请完善信息",Toast.LENGTH_SHORT).show()
                    }else{
                        request.modifyPass(user,pwNew,pwOld,hand)//提交修改
                    }
                }


            }
            R.id.nav_send -> {
               // Toast.makeText(this@WDMain,"内测阶段。",Toast.LENGTH_LONG).show()
            }

        }

        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        menuInflater.inflate(R.menu.menu, menu)


        return true
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.


        when (item.itemId) {


            //关于应用
            R.id.nav_about -> {
               startActivity(Intent(this@WDMain,about::class.java))
            }
            //检查更新
            R.id.nav_updata ->{
                Utils.requestUpVersion(hand)

            }

            //期刊检索
            R.id.nav_qk ->{
                startActivity(Intent(this@WDMain,libraryQk::class.java))
            }
            //开放时间
            R.id.nav_openTime ->{
                Utils.Tos("正在完善！")
            }
            //读者互动
            R.id.nav_reader->{
                Utils.Tos("正在完善！")
            }
            //图书架规则
            R.id.nav_bookshelf ->{
                Utils.Tos("正在完善！")
            }
            //常见问题
            R.id.nav_questions->{
                Utils.Tos("正在完善！")
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }






}
