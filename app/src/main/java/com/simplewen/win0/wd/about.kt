package com.simplewen.win0.wd

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.content.pm.PackageManager
import android.content.pm.ApplicationInfo
import android.content.Intent
import android.didikee.donate.AlipayDonate
import android.net.Uri
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import android.widget.Toolbar
import kotlinx.android.synthetic.main.activity_about.*


class about : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        setSupportActionBar(toolbar)
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)


    }

    fun joinQQGroup(view: View): Boolean {
        val intent = Intent()
        var key="hKgBCQNgklW4c2dHwinkN85CCq-Fvyyg"
        intent.data = Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D$key")
        // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            startActivity(intent)
            return true
        } catch (e: Exception) {
           Toast.makeText(this@about,"未安装QQ或版本不支持，请手动添加",Toast.LENGTH_LONG).show()
            return false
        }

    }

    fun donateAli(view:View){
        val payCode="FKX03272QHJKIU7YQ2VS68"
        val hasInstalledAlipayClient = AlipayDonate.hasInstalledAlipayClient(this)
        if (hasInstalledAlipayClient) {
            AlipayDonate.startAlipayClient(this, payCode)
        }

    }
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }


}
