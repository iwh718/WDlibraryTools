package com.simplewen.win0.wd.activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.support.v7.app.AlertDialog
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.simplewen.win0.wd.R
import com.simplewen.win0.wd.app.CloudApp
import com.simplewen.win0.wd.modal.iwhDataOperator
import com.simplewen.win0.wd.util.Utils
import kotlinx.android.synthetic.main.login_activity.*
import request.requestCLoudLibrary

/**
 * 云朵图书馆：CloudLibrary
 * @author iwh
 * 策划：Yao
 * 该项目为校园实验项目：安信工适配版
 * 非盈利开发
 */
class LoginActivity : AppCompatActivity() {

    lateinit var request:requestCLoudLibrary
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)
        setSupportActionBar(toolbar)
        request  = requestCLoudLibrary()
        val hand: Handler = object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message?) {
                super.handleMessage(msg)
                when (msg?.what) {
                    0x1 -> {
                        loginBar.visibility = View.GONE
                        Utils.Tos("登录完成！")
                      //  CloudApp.temArrayData = request.currentBooks
                        iwhDataOperator.setSHP("school_type","axg","CloudInfo")
                                .setSHP("userAccount",request.userInfo["userAccount"],"CloudUser")
                                .setSHP("userPassword",request.userInfo["userPassword"],"CloudUser")
                        startActivity(Intent(this@LoginActivity,CloudLibraryMain::class.java))
                        finish()
                    }

                    0x2 -> {
                        Utils.Tos("登录失败：账号或密码有误")
                        loginBar.visibility = View.GONE
                        loginLayout.visibility = View.VISIBLE

                    }

                    0x3 ->{
                        Utils.Tos("网络连接失败")
                        loginBar.visibility = View.GONE
                        loginLayout.visibility = View.VISIBLE
                    }



                }
            }

        }
        //检测是否登录过
        if(iwhDataOperator.getSHP("userAccount","CloudUser","").isNotEmpty()){
            //移除输入框
            loginLayout.visibility = View.GONE
            //添加加载圈
            loginBar.visibility = View.VISIBLE
            request.Login(
                    hand
                    ,iwhDataOperator.getSHP("userAccount","CloudUser","")
                    ,iwhDataOperator.getSHP("userPassword","CloudUser","")
            )
        }
        //开始登录
        toLogin.setOnClickListener{
            if(userAccount.text.toString().isEmpty() || userPassword.text.toString().isEmpty()){
                Utils.Tos("请输入账号或密码！")
            }else{
                //移除输入框
                loginLayout.visibility = View.GONE
                //添加加载圈
                loginBar.visibility = View.VISIBLE
                request.Login(hand,userAccount.text.toString(),userPassword.text.toString())

            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
         menuInflater.inflate(R.menu.login_axg_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.itemId){
            //获取登录账号
            R.id.login_how_menu ->{
                AlertDialog.Builder(this@LoginActivity)
                        .setIcon(R.drawable.action_warn)
                        .setTitle("不知道账号与密码？")
                        .setMessage("如果你不清楚你的图书馆账号与密码，请按确定，稍后将会帮助您获取！")
                        .setPositiveButton("好的"){
                            _,_ ->
                            startActivity(Intent(this@LoginActivity,CL_Help_Login::class.java))
                        }
                        .setNegativeButton("算了",null)
                        .create().show()

            }
        }
        return super.onOptionsItemSelected(item)
    }

}





