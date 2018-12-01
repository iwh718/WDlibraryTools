package com.simplewen.win0.wd

import android.content.Intent
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


class MainActivity : AppCompatActivity() {

    var ass = AnimationSet(false)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       requestWindowFeature(Window.FEATURE_NO_TITLE)
      // window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_main)
        val login_img = findViewById<ImageView>(R.id.login_img)//获取首页login
        startAni(login_img)
        ass.setAnimationListener(object: Animation.AnimationListener {
            override fun onAnimationStart(Ani:Animation) {}
            override fun onAnimationRepeat(ani:Animation) {}
            //监听动画播放完
            override fun onAnimationEnd(ani:Animation){
                val intent=Intent(this@MainActivity,WDMain::class.java)
                startActivity(intent)//主界面
                finish()
            }
        })

    }


    fun startAni(img:View) {

        val sa = ScaleAnimation(
                1f, 1.1f, 1.0f, 1.1f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f)

        sa.duration = 2500
        sa.fillAfter = true
        ass.addAnimation(sa)
        img.startAnimation(ass)//启动动画
    }



}





