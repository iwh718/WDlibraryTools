package com.simplewen.win0.wd.util

import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Environment
import android.os.Handler
import android.os.Message


import android.view.Gravity
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.simplewen.win0.wd.R
import com.simplewen.win0.wd.app.CloudApp
import okhttp3.*
import java.io.IOException
import java.lang.Exception
import kotlin.concurrent.thread

class Utils{



    companion object {

        /**自定义 Toast
         * @param str 自定义文本
        **/
        fun Tos(showText: String, gravity: Int = Gravity.BOTTOM, type: Int = R.color.colorAccent){
            val iwhContext = CloudApp._context
            val setParame = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            val iwhText = TextView(iwhContext).apply {
                setTextColor(Color.WHITE)
                textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                setPadding(5, 5, 5, 5)
                layoutParams = setParame

            }
            val iwhLyout = LinearLayout(iwhContext).apply {
                layoutParams = setParame
                setBackgroundResource(type)
                addView(iwhText)
            }
            with( Toast.makeText(CloudApp.getContext(), showText, Toast.LENGTH_SHORT)){
                setGravity(android.view.Gravity.FILL_HORIZONTAL or gravity, 0, 0)
                view = iwhLyout
                setMargin(0f, 0f)
                iwhText.text = showText
                show()
            }
        }


        fun joinQQGroup(): Boolean {
            val intent = Intent()
            val key="ylQNSD_I5zOdD7zjgp4iHN0KUN4TKbJx"
            intent.data = Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D$key")
            // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面 //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            try {

                CloudApp.getContext().startActivity(intent)
                return true
            } catch (e: Exception) {
                Toast.makeText(CloudApp.getContext(),"未安装QQ或版本不支持，请手动添加",Toast.LENGTH_LONG).show()
                return false
            }

        }

        /**发送到主线程消息**/
        fun sendMsg(msgId:Int,hand:Handler){
            with(Message()){
                what = msgId
                hand.sendMessage(this)
            }

        }

        fun loginCheck():Boolean{
           return true
        }

        }






    }