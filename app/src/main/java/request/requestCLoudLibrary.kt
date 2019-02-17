package request

import android.os.Handler
import android.util.Log
import com.simplewen.win0.wd.app.CloudApp
import com.simplewen.win0.wd.modal.PreData
import com.simplewen.win0.wd.util.Utils
import okhttp3.*
import org.json.JSONArray
import java.io.IOException
import java.lang.Exception
import java.util.*
import kotlin.concurrent.thread

/**
 * 安徽信息工程学院图书馆工具
 */
class requestCLoudLibrary{
        //账户信息
        val userInfo = linkedMapOf<String,Any>()
        val iwh = "校园工具，非盈利项目，非礼勿扰"
        val client = OkHttpClient.Builder().build() //初始化请求
        //当前在借图书
        var currentBooks = ArrayList<Map<String, Any>>()
        //历史记录
        var historyBooks = ArrayList<Map<String, Any>>()
        //搜索图书
        var searchBooks = ArrayList<Map<String, Any>>()
        //登录标志
        var loginFlag: Int = 0
        //用户名
        var userName = ""

    /**
     * 登录图书馆
     * @param hand 主线程队列
     * @param userAccount 账号
     * @param userPassword 密码
     */
    fun Login(hand: Handler, userAccount:String = "317205010234",userPassword:String = "0000" ) {
            //发送参数
            CloudApp.temArrayData?.clear()
            this@requestCLoudLibrary.currentBooks.clear()
            val loginInfo = FormBody.Builder()
                    .add("iwhKey", "718")
                    .add("schoolId","axg")
                    .add("API_TYPE", "LOGIN")
                    .add("userAccount",userAccount)
                    .add("userPassword",userPassword)
                    .build()
            //构建请求
            var res: String?
            this.userInfo["userAccount"] = userAccount
            this.userInfo["userPassword"] = userPassword
                thread {
                    val request = Request.Builder().url(PreData.baseUrl).post(loginInfo).build()
                    this.client.newCall(request).enqueue(object : Callback {
                        override fun onResponse(call: Call, response: Response) {
                            res = response.body()?.string()
                            try {
                                val tem_res: JSONArray?
                                tem_res = JSONArray(res)
                                Log.d("@@@iwhRES_login:",tem_res.toString())
                                if( tem_res.getJSONObject(0).getString("flag") == "true"){
                                    for (i in 0 until tem_res.length()){
                                        with(LinkedHashMap<String, Any>()) {
                                            put("b_id", tem_res.getJSONObject(i).getString("b_id"))
                                            put("b_title", tem_res.getJSONObject(i).getString("b_title"))
                                            put("b_author",  tem_res.getJSONObject(i).getString("b_author"))
                                            put("b_address",  tem_res.getJSONObject(i).getString("b_address"))
                                            put("b_startTime", tem_res.getJSONObject(i).getString("b_startTime"))
                                            put("b_stopTime", tem_res.getJSONObject(i).getString("b_stopTime"))
                                            put("b_status", tem_res.getJSONObject(i).getString("b_status"))
                                            this@requestCLoudLibrary.currentBooks.add(this)
                                        }
                                    }
                                    this@requestCLoudLibrary.currentBooks.removeAt(0)
                                    this@requestCLoudLibrary.loginFlag = 1
                                    Log.d("@@@data:",this@requestCLoudLibrary.currentBooks.toString())
                                    Utils.sendMsg(0x1, hand)

                                }else{
                                    //登录失败，账户出错！
                                    Utils.sendMsg(0x2, hand)
                                }
                            }catch (e:Exception){
                                Log.d("@@@error:",e.toString())
                                Utils.sendMsg(0x2, hand)
                            }
                        }
                        override fun onFailure(call: Call, e: IOException) {
                            Utils.sendMsg(0x3, hand)

                        }
                    })

                }


        }

    /**
     * 获取登录账号
     * @param name 真实姓名
     */
    fun getAccount(name:String,hand: Handler):String{
        return ""
    }

    }
