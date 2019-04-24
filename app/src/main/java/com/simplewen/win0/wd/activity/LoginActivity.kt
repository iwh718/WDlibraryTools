package com.simplewen.win0.wd.activity

import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.os.Build
import android.support.design.widget.TextInputEditText
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.simplewen.win0.wd.R
import com.simplewen.win0.wd.app.WdTools
import com.simplewen.win0.wd.base.BaseActivity
import com.simplewen.win0.wd.modal.iwhDataOperator
import com.simplewen.win0.wd.request.RequestManage
import com.simplewen.win0.wd.util.Utils
import kotlinx.android.synthetic.main.activity_brow.*
import kotlinx.android.synthetic.main.login_main.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.withTimeoutOrNull
import okhttp3.internal.Util
import java.lang.Exception
import java.util.*

/**
 * 登录页
 */
@ExperimentalCoroutinesApi
class LoginActivity : BaseActivity() {

    //网络管理
    lateinit var requestManage: RequestManage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_main)
        //初始化网络请求
        requestManage = RequestManage(this@LoginActivity)
        //开始登陆
        login_sign.setOnClickListener {
            val myuser: String = findViewById<TextInputEditText>(R.id.user).text.toString()
            val mypw: String = findViewById<TextInputEditText>(R.id.pw).text.toString()

            //开启登陆协程
            if (myuser.isNotEmpty() && mypw.isNotEmpty()) {
                login_sign.visibility = View.GONE
                login_progressbar.visibility = View.VISIBLE

                launch {
                    requestManage.myLogin(myuser, mypw, this@LoginActivity)


                }

            } else {
                Utils.Tos("请补全信息！")
            }


        }
        if (iwhDataOperator.getSHP("flag", "wd", "") !== "") {
            //自动登录
            login_sign.visibility = View.GONE
            user.visibility = View.GONE
            pw.visibility = View.GONE
            login_progressbar.visibility = View.VISIBLE
            requestManage.myLogin(iwhDataOperator.getSHP("user", "wd", ""), iwhDataOperator.getSHP("pw", "wd", ""), this@LoginActivity)
        }

    }


}





