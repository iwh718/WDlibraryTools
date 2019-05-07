package com.simplewen.win0.wd.activity

import android.os.Bundle
import android.view.View
import android.os.Build
import android.support.design.widget.TextInputEditText

import com.simplewen.win0.wd.R

import com.simplewen.win0.wd.base.BaseActivity
import com.simplewen.win0.wd.modal.iwhDataOperator

import com.simplewen.win0.wd.request.WorkWd
import com.simplewen.win0.wd.util.Utils

import kotlinx.android.synthetic.main.login_main.*
import kotlinx.coroutines.ExperimentalCoroutinesApi


/**
 * 登录页
 */
@ExperimentalCoroutinesApi
class LoginActivity : BaseActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_main)


        //开始登陆
        login_sign.setOnClickListener {
            val myuser: String = findViewById<TextInputEditText>(R.id.user).text.toString()
            val mypw: String = findViewById<TextInputEditText>(R.id.pw).text.toString()

            //开启登陆协程
            if (myuser.isNotEmpty() && mypw.isNotEmpty()) {
                login_sign.visibility = View.GONE
                login_progressbar.visibility = View.VISIBLE
               WorkWd.myLogin(myuser, mypw, this@LoginActivity)

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
            WorkWd.myLogin(iwhDataOperator.getSHP("user", "wd", ""), iwhDataOperator.getSHP("pw", "wd", ""), this@LoginActivity)
        }

    }


}





