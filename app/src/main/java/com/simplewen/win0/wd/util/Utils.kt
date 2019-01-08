package com.simplewen.win0.wd.util

import android.app.Activity
import android.app.AlertDialog
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.net.Uri
import android.os.Environment
import android.os.Handler
import android.os.Message
import android.support.design.widget.Snackbar
import android.text.Layout


import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.simplewen.win0.wd.R
import com.simplewen.win0.wd.WdTools
import okhttp3.*
import java.io.IOException
import java.lang.Exception
import java.lang.reflect.Parameter
import kotlin.concurrent.thread

class Utils{
    /**@param CheckUp 检查版本更新 **/


    companion object {
        val versionUrl = "https://www.borebooks.top/wd/wdVersion.php"
        val versionWd =  "https://www.borebooks.top/wd/wd.apk"
        /**自定义 Toast
         * @param str 自定义文本
        **/
        fun Tos(str:String){
            val iwhToast = Toast.makeText(WdTools.getContext(),str,Toast.LENGTH_SHORT)
            val iwhLyout = LayoutInflater.from(WdTools.getContext()).inflate(R.layout.iwh_toast,null)
            iwhToast.setGravity(Gravity.FILL_HORIZONTAL or Gravity.BOTTOM,0,0)
            iwhToast.view = iwhLyout
            iwhLyout.findViewById<TextView>(R.id.iwh_toast_text).text = str
            iwhToast.show()
        }

        fun requestUpVersion(hand:Handler){
            var versionOld = 0
            val client = OkHttpClient.Builder().build() //初始化请求
            thread {

                //构建请求
                val request = Request.Builder().url(versionUrl).build()
                client.newCall(request).enqueue(object : Callback {
                    override fun onResponse(call: Call, response: Response) {

                        val resText = response.body()?.string()
                        val temResText: String? = resText
                        versionOld = temResText!!.replace(" ","").toInt()
                        Log.d("@@versionWD:",versionOld.toString())
                        val msg = Message()
                        msg.what = 0x21
                        msg.arg1 = versionOld
                        hand.sendMessage(msg)

                    }

                    override fun onFailure(call: Call, e: IOException) {
                        val msg = Message()
                        msg.what = 0x22
                        hand.sendMessage(msg)
                    }
                })


            }

        }
        fun DownNew():Long{


            val  request = DownloadManager.Request(Uri.parse(versionWd))
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
            request.setDestinationInExternalFilesDir(WdTools.getContext(), Environment.DIRECTORY_DOWNLOADS,"wd.apk")
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            // 设置 Notification 信息
            request.setTitle("正在下载文院图书馆更新")
            request.setDescription("下载完成后请点击打开")
            request.setVisibleInDownloadsUi(true)
            request.allowScanningByMediaScanner()
            request.setMimeType("application/vnd.android.package-archive")

            // 实例化DownloadManager 对象
           val downloadManager = WdTools.getContext().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            return downloadManager.enqueue(request)
        }
        /**检测第一次启动**/
        //检查是否第一次使用
        fun checkFLagTips():Boolean{
            var flag:Boolean =false

            if(WdTools.getContext().getSharedPreferences("beginTips",Activity.MODE_PRIVATE).getInt("tipsFlag",0)== 0)    {

                flag = true
            }
            return flag
        }
        //控制用户引导
        fun closeTips(){
            val shareP2 = WdTools.getContext().getSharedPreferences("beginTips",Activity.MODE_PRIVATE)
            val edit = shareP2.edit()
            edit.putInt("tipsFlag", 1)
            edit.apply()
        }

        /**
         * 获取版本号
         */

       fun getVersion(activity:Activity):Int{
           var version:String? = null
            try {
                val manager = activity.packageManager
                val  info = manager.getPackageInfo(activity.packageName, 0)
                 version ="${info.versionCode}"

            } catch (e:Exception) {

            }
            return version!!.toInt()
        }

        /***开放时间*/
        fun dialogTime(context: Context){
            val dialog_fb = LayoutInflater.from(WdTools.getContext()).inflate(R.layout.dialog_time,null)
            AlertDialog.Builder(context)
                    .setTitle("开放时间")
                    .setView(dialog_fb)
                    .create().show()
        }





    }
}