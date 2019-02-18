package com.simplewen.win0.wd.activity

import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v4.view.ViewPager
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import com.simplewen.win0.wd.R
import com.simplewen.win0.wd.adapter.ViewPageAdapter
import com.simplewen.win0.wd.app.CloudApp
import com.simplewen.win0.wd.modal.PreData
import com.simplewen.win0.wd.modal.iwhDataOperator
import com.simplewen.win0.wd.view.CL_ViewPageFragment
import kotlinx.android.synthetic.main.activity_cloud_library_main.*

/**
 * 云朵图书馆小工具
 * @author IWH
 * time：2019
 * 策划：Yao
 * 校园工具，非盈利项目
 */
class CloudLibraryMain : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cloud_library_main)
        setSupportActionBar(toolbar)
        val handler = Handler{
            when(it.what){
                //数据获取完成
                0x1 ->{

                }
                0x2 ->{

                }
            }
            true
        }
        nav_view.getHeaderView(0).findViewById<TextView>(R.id.userName).text = CloudApp.requestAll!!.userName
        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        nav_view.setNavigationItemSelectedListener(this@CloudLibraryMain)
        val viewPageFglists: ArrayList<Fragment> = ArrayList<Fragment>().apply {
            add(CL_ViewPageFragment().apply {
                val Bundle = Bundle()
                Bundle.putInt("type",0)
                arguments = Bundle
            })
            add(CL_ViewPageFragment().apply {
                val Bundle = Bundle()
                Bundle.putInt("type",1)
                arguments = Bundle
            })

        }
        //viewpage初始化
        val indexViewPgeAdapter = ViewPageAdapter(supportFragmentManager, viewPageFglists)
        indexViewPage.adapter = indexViewPgeAdapter
        indexViewPage.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int)=Unit
            override fun onPageScrollStateChanged(state: Int) = Unit
            override fun onPageSelected(position: Int) {
                //同步tab
                indexTab.getTabAt(position)?.select()
            }
        })
        //tab初始化
        with(indexTab) {
            addTab(this.newTab().setText("当前借阅"))
            addTab(this.newTab().setText("历史借阅"))
            addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    indexTab.getTabAt(tab.position)?.select()
                    indexViewPage.setCurrentItem(tab.position, true)
                }
                override fun onTabReselected(tab: TabLayout.Tab) {}
                override fun onTabUnselected(tab: TabLayout.Tab?) {}
            })
        }

    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            with(Intent(Intent.ACTION_MAIN)){
               flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                addCategory(Intent.CATEGORY_HOME)
                startActivity(this)
            }

        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.menu_search -> startActivity(Intent(this@CloudLibraryMain,SearchActivity::class.java))

        }
        return true
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_camera -> {
                // Handle the camera action
            }

            R.id.nav_slideshow -> {

            }
            R.id.nav_manage -> {

            }
            R.id.nav_share -> {

            }
            //注销登录
            R.id.nav_outSign -> {
               AlertDialog.Builder(this@CloudLibraryMain)
                       .setTitle("确定要注销吗？").setMessage("注销后将会重新登陆！")
                       .setPositiveButton("确定"){
                           _,_ ->
                           iwhDataOperator.setSHP("userAccount","","CloudUser")
                                   .setSHP("userPassword","","CloudUser")

                           startActivity(Intent(this@CloudLibraryMain,LoginActivity::class.java))
                           finish()
                       }
                       .setNegativeButton("取消",null)
                       .create().show()
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        Thread.sleep(300)
        return true
    }



}
