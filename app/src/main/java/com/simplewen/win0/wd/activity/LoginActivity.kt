package com.simplewen.win0.wd.activity
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.simplewen.win0.wd.R
import kotlinx.android.synthetic.main.activity_login_main.*

/**
 * 云朵图书馆：CloudLibrary
 * @author iwh
 * 策划：Yao
 * 该项目为校园实验项目：安信工适配版
 * 非盈利开发
 */
class LoginActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_main)
        setSupportActionBar(toolbar)




    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
         menuInflater.inflate(R.menu.login_axg_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.itemId){
            //获取登录账号
            R.id.login_how_menu ->{

            }
        }
        return super.onOptionsItemSelected(item)
    }

}





