package com.simplewen.win0.wd

import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.widget.ImageView

import android.view.animation.ScaleAnimation
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import android.os.Build
import java.sql.Time
import java.util.*


class MainActivity : AppCompatActivity() {

    var ass = AnimationSet(false)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       //requestWindowFeature(Window.FEATURE_NO_TITLE)

      // window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT >= 21) {
            val decorView = window.decorView
            val option = (View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
            decorView.systemUiVisibility = option
            window.navigationBarColor = Color.TRANSPARENT
            window.statusBarColor = Color.TRANSPARENT
        }

        val login_img = findViewById<ImageView>(R.id.login_img)//获取首页login

        Timer().schedule(object:TimerTask(){
            override fun run() {
                startActivity(Intent(this@MainActivity,WDMain::class.java))
                finish()
            }
        } ,2000)


    }





}





