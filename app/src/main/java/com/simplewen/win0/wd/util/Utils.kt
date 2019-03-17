package com.simplewen.win0.wd.util

import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.os.Handler
import android.os.Message


import android.view.Gravity
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import com.simplewen.win0.wd.R
import com.simplewen.win0.wd.app.WdTools
import com.simplewen.win0.wd.modal.PreData
import okhttp3.*
import java.io.IOException
import java.lang.Exception
import kotlin.concurrent.thread

class Utils{



    companion object {
        val versionUrl = "https://www.borebooks.top/wd/wdVersion.php"
        val versionWd =  "https://www.borebooks.top/wd/wd.apk"
        /**自定义 Toast
         * @param str 自定义文本
        **/
        fun Tos(str:String,gravity: Int = Gravity.BOTTOM){
            val iwhToast = Toast.makeText(WdTools.getContext(),str,Toast.LENGTH_SHORT)
            val iwhLyout = LayoutInflater.from(WdTools.getContext()).inflate(R.layout.iwh_toast,null)
            iwhToast.setGravity(Gravity.FILL_HORIZONTAL or gravity,0,0)
            iwhToast.view = iwhLyout
            iwhLyout.findViewById<TextView>(R.id.iwh_toast_text).text = str
            iwhToast.show()
        }
        /**检查版本**/
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
                        //Log.d("@@versionWD:",versionOld.toString())
                        val msg = Message()
                        msg.what = PreData.CHECK_UPDATE_OK
                        msg.arg1 = versionOld
                        hand.sendMessage(msg)

                    }

                    override fun onFailure(call: Call, e: IOException) {
                        val msg = Message()
                        msg.what = PreData.NET_CODE_DATA_ERROR
                        hand.sendMessage(msg)
                    }
                })


            }

        }

        /**下载新版本**/
        fun downNew():Long{


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
        fun joinQQGroup(): Boolean {
            val intent = Intent()
            val key="ylQNSD_I5zOdD7zjgp4iHN0KUN4TKbJx"
            intent.data = Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D$key")
            // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面 //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            try {

                WdTools.getContext().startActivity(intent)
                return true
            } catch (e: Exception) {
                Toast.makeText(WdTools.getContext(),"未安装QQ或版本不支持，请手动添加",Toast.LENGTH_LONG).show()
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



        }






    }