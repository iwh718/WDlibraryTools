package com.simplewen.win0.wd

import android.app.ActionBar
import android.app.Activity
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.view.GravityCompat
import android.support.v4.view.MenuItemCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.*
import android.widget.*
import kotlinx.android.synthetic.main.activity_wd_main.*
import request.requestManage
import kotlinx.android.synthetic.main.content_wd_main.*
import android.didikee.donate.AlipayDonate
import android.graphics.Color
import android.net.Uri
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.TextInputEditText
import android.support.v7.widget.CardView
import kotlinx.android.synthetic.main.activity_about.*


//author:bore初夏
//QQ:2868579699
//time:18.09.02
//校园工具，非盈利项目，非礼勿扰
//喜欢那个文，2018.09.20
class WDMain : AppCompatActivity(){

    var request = requestManage(this@WDMain)//实例化请求对象
    var b_id = request.b_id//listview id
    var logined: Int = 0//是否历史登录标志
    private var bore_user: String = ""
    private var bore_pw: String = ""
    var tem_dialog:AlertDialog? = null//临时dialog对象，用来调用dismiss
    var tem_load:AlertDialog? =null //临时加载
    //handler接收网络线程数据
    val hand: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            when (msg?.what) {
                0 -> {
                   tem_load?.dismiss()

                }
                1 -> {
                    tem_dialog?.dismiss()
                    Toast.makeText(this@WDMain, "登录成功，欢迎您", Toast.LENGTH_SHORT).show()
                    tem_load?.dismiss()//移除加载
                    request.loginFlag = 1
                    logined = 1
                    val shareP = getSharedPreferences("wd", Activity.MODE_PRIVATE)
                    val edit = shareP.edit()
                    edit.putString("user", msg.data.getString("user"))
                    edit.putString("pw", msg.data.getString("pw"))
                    edit.apply()
                }
                2 -> {
                    Toast.makeText(this@WDMain, "登录失败：账号或密码有误", Toast.LENGTH_SHORT).show()
                    tem_load?.dismiss()
                }
                3 -> {
                    tem_dialog?.dismiss()
                    Toast.makeText(this@WDMain, "请先登录校园网", Toast.LENGTH_SHORT).show()
                    logined = 0
                    val dialog = AlertDialog.Builder(this@WDMain)
                    dialog.setTitle("请先登录校园网再使用本应用！")
                            .setCancelable(false)
                            .setPositiveButton("确认") { _, _ ->
                                finish()
                            }
                            .create()
                    dialog.show()
                }
                4 -> {
                    tem_dialog?.dismiss()
                    Toast.makeText(this@WDMain, "网络异常", Toast.LENGTH_SHORT).show()
                    logined = 0
                    val dialog = AlertDialog.Builder(this@WDMain)
                    dialog.setTitle("请先登录校园网再使用本应用！")
                            .setCancelable(false)
                            .setPositiveButton("确认") { _, _ ->
                                finish()
                            }
                            .create()
                    dialog.show()
                }
                5 -> {

                    Toast.makeText(this@WDMain, "请输入账号与密码", Toast.LENGTH_SHORT).show()
                    tem_load?.dismiss()
                }
                6 -> {
                    logined = 1
                }
            }
        }

    }


    //工具类：加入QQ群，支付宝捐赠
    inner class tools{
        fun joinQQGroup(): Boolean {
            val intent = Intent()
            val key="hKgBCQNgklW4c2dHwinkN85CCq-Fvyyg"
            intent.data = Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D$key")
            // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
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
            tem_load = dialog.show()
        }
        fun loginDialog(){
            val dialog = AlertDialog.Builder(this@WDMain)
            val login_layout = layoutInflater.inflate(R.layout.login_form,null)
            val login_btn = login_layout.findViewById<Button>(R.id.sign)
            login_btn.setOnClickListener{
                val myuser: String = login_layout.findViewById<TextInputEditText>(R.id.user).text.toString()
                val mypw: String = login_layout.findViewById<TextInputEditText>(R.id.pw).text.toString()
                tools().loadDialog()
                Toast.makeText(this@WDMain,"登录中",Toast.LENGTH_SHORT).show()
                request.myLogin(myuser, mypw, hand)

            }
            dialog.setView(login_layout).create()
            tem_dialog = dialog.show()
        }
        //检测登录状态
        fun checkLogined(): Boolean {
            val shareP = getSharedPreferences("wd", Activity.MODE_PRIVATE)
            val temUser: String = shareP.getString("user", "")
            val temPw: String = shareP.getString("pw", "")
            if (temPw.isNotEmpty() && temPw.isNotEmpty()) {
                logined = 1
                bore_pw = temPw
                bore_user = temUser
                return true

            } else {
                logined == 0
                return false
            }


        }
        fun hello(){
            Snackbar.make(search_btn,"你好，这里是文达！",Snackbar.LENGTH_SHORT).show()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wd_main)
        setSupportActionBar(toolbar_one)
        toolbar_one.setTitleTextColor(Color.WHITE)
        var search_mode:String = "1"//默认搜索模式
        var search_sort:String = "正题名"//默认排列
        val search_btn = findViewById<SearchView>(R.id.search_btn)//搜索按钮
        val search_ly = findViewById<LinearLayout>(R.id.search_ly)//搜索布局
        val fab = findViewById<FloatingActionButton>(R.id.fab)//获取悬浮按钮
        val search_group = findViewById<RadioGroup>(R.id.search_group)//获取搜索模式组
        val sort_group = findViewById<RadioGroup>(R.id.sort_group)//获取排列模式组
        val hello_card = findViewById<CardView>(R.id.hello_wd)//你好，文达

        //搜索模式：设置监听器
        search_group.setOnCheckedChangeListener{
          _,sid ->
            when(sid){
                R.id.group_s_def -> {
                    search_mode = "1"
                }//默认搜索
                R.id.group_s_mix -> {
                    search_mode = "2"
                }//模糊搜索
                R.id.group_s_spe -> {
                    search_mode  = "3"
                }//精确搜索

            }
        }
        //排列方式：设置监听器
        sort_group.setOnCheckedChangeListener{
            _,oid->
            when(oid){
                R.id.group_d_bname -> {
                    search_sort = "正题名"
                }//按照书名排列
                R.id.group_d_btime -> {
                    search_sort = "出版日期"
                }//按照出版时间排列
            }

        }


        //每次打开，检测是否登录过
        if (tools().checkLogined()) {
            request.myLogin(bore_user, bore_pw, hand)//登录

        } else {
            Toast.makeText(this,"你还未登录，或登录失效！",Toast.LENGTH_SHORT).show()
            tools().loginDialog()
        }


        //绑定搜索按钮
        search_btn.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            var s_key: String? = "bore test"
            override fun onQueryTextSubmit(query: String?): Boolean {

                val intent5 = Intent(this@WDMain, search_login::class.java)
                intent5.putExtra("search_mode",search_mode)
                intent5.putExtra("search_sort",search_sort)
                intent5.putExtra("search_key", s_key)
                startActivity(intent5)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                s_key = newText
                return true
            }
        })
        //绑定fab按钮
        fab.setOnClickListener{
            val dialog = AlertDialog.Builder(this@WDMain)
            dialog.setTitle("加入QQ群！")
                    .setPositiveButton("确认") { _, _ ->
                       tools().joinQQGroup()
                    }.setNegativeButton("取消"){
                        _,_->
                    }
                    .create()
            dialog.show()
        }
        hello_card.setOnClickListener{
            tools().hello()
        }

    }
    //设置初始化时间
    private  var firTime:Long = 0
    override fun onBackPressed() {

            val time = System.currentTimeMillis()
            if (time - firTime > 2000) {
                Snackbar.make(search_ly, "再按一次退出应用", Snackbar.LENGTH_LONG).show()
                firTime=time
            }else{
                System.exit(0)
            }
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            //我的借阅历史
            R.id.menu_history -> {
                if (logined != 1) {
                    Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show()
                    tools().loginDialog()
                } else {
                    val intent3 = Intent(this, history::class.java)
                    startActivity(intent3)//切换到我的借阅历史
                }
            }
            //登录状态检测
            R.id.menu_my -> {
                if (logined != 1) {
                    Toast.makeText(this, "您还未登录！", Toast.LENGTH_SHORT).show()
                    tools().loginDialog()
                } else {
                    val dialog = AlertDialog.Builder(this)
                    dialog.setTitle("要重新登录吗？")
                            .setNegativeButton("取消") { _, _ ->

                            }
                            .setPositiveButton("确认") { _, _ ->
                                tools().loginDialog()
                            }
                            .create()
                    dialog.show()
                }
            }
            //关于
            R.id.menu_about -> {

                val about_dia = AlertDialog.Builder(this@WDMain)
                        .setView(layoutInflater.inflate(R.layout.activity_about,null))
                        .setTitle("开发者：IWH").create().show()
            }
            //正在借阅的
            R.id.menu_brown -> {

                if (logined != 1) {
                    Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show()
                    tools().loginDialog()

                } else {
                    val intent = Intent(this, brow::class.java)
                    startActivity(intent)
                }
            }
        }

        return true
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)

        return true
    }

    fun donateAli(view:View) {
        val payCode = "FKX03272QHJKIU7YQ2VS68"
        val hasInstalledAlipayClient = AlipayDonate.hasInstalledAlipayClient(this@WDMain)
        if (hasInstalledAlipayClient) {
            AlipayDonate.startAlipayClient(this@WDMain, payCode)
        }
    }

}
