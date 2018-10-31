package request

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.View
import android.widget.*
import com.simplewen.win0.Utils.PersistentCookieStore
import com.simplewen.win0.wd.R
import okhttp3.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.io.IOException

import kotlin.concurrent.thread

class requestManage(val context: Context) {

    val cookieJar: CookieJar = object : CookieJar {
        private val map = PersistentCookieStore(context)
        override fun saveFromResponse(url: HttpUrl, cookies: MutableList<Cookie>) {
            map[url.host()] = cookies

        }

        override fun loadForRequest(url: HttpUrl): MutableList<Cookie> {

            return map[url.host()] ?: ArrayList()
        }
    }//自定义cookieJar
    var contextThis: Context = context
    val logUrl = "http://172.16.1.43/dzjs/login.asp"//登录url
    val myBrowUrl = "http://172.16.1.43/dzjs/jhcx.asp"//我的借阅
    val myContinueUrl = "http://172.16.1.43/dzxj/dzxj.asp"//我的图书续借
    val myHiStoryUrl = "http://172.16.1.43/dzjs/dztj.asp"//我的借阅历史
    val myLikeUrl = ""//我的书架
    val bookSearchUrl = "http://172.16.1.43/wxjs/tmjs.asp"//搜索地址
    val isWdLogin = Regex(".*?http:172.16.1.101:80/portalowa.*?")//判断是否校园网
    val isFailLogin = Regex(".*window.history.back.*")//判断是否失败
    val isSuccLogin = Regex(".*dzjs.login_form.*")//判断是否成功
    val b_info = arrayOf("b_name", "b_last", "b_next", "b_continue","b_id")
    val b_id = intArrayOf(R.id.b_name, R.id.b_last, R.id.b_next, R.id.b_continue,R.id.b_id)
    val client = OkHttpClient.Builder().cookieJar(cookieJar).build() //初始化请求
    var books = ArrayList<Map<String, Any>>()
    var h_books = ArrayList<Map<String, Any>>()
    var s_books = ArrayList<Map<String, Any>>()
    var loginFlag: Int = 0



    fun myBrow(hand: Handler,listadapter:SimpleAdapter) {
        var res: String? = ""
        var tem_res: String? = ""
        if (this.loginFlag == 0) {


            thread {
                var request = Request.Builder().url(this.myBrowUrl).build()
                var response = this.client.newCall(request).enqueue(object : Callback {
                    override fun onResponse(call: Call, response: Response) {
                        res = response.body()?.string()
                        tem_res = res//临时存放string
                        var doc2 = Jsoup.parse(tem_res)
                        var temText = doc2.select("table[width=\"98%\"] tbody tr:not(table[width=\"98%\"] tbody tr:first-child)")
                        var bname: String = ""
                        var blast: String = ""
                        var bnext: String = ""
                        var bid: String = ""//图书续借id

                        Log.d("返回：", temText.toString())

                        for (item: Element in temText) {
                            bname = item.select("td:nth-child(2)").html().toString()
                            blast = item.select("td:nth-child(4)").html().toString()
                            bnext = item.select("td:nth-child(5)").html().toString()
                            bid = item.select("td:nth-child(8) a").attr("href").toString()//提取续借编号
                            bid=bid.replace("../dzxj/dzxj.asp?nbsl=","")//提取编号
                            //Log.d("bid:",bid)
                            val tem = LinkedHashMap<String, Any>()
                            tem.put("b_name", bname)
                            tem.put("b_last", "借阅:" + blast)
                            tem.put("b_next", "限还：" + bnext)
                            tem.put("b_continue", "续借")
                            tem.put("bid",bid)
                            books.add(tem)


                        }
                        var msg = Message()
                        msg.what = 6
                        hand.sendMessage(msg)
                    }

                    override fun onFailure(call: Call, e: IOException) {
                        var msg = Message()
                        msg.what = 3
                        hand.sendMessage(msg)
                    }
                })

            }

        } else {
            Toast.makeText(contextThis, "请先登录", Toast.LENGTH_SHORT).show()
        }
    }

    fun myHisory(hand: Handler) {
        var res: String? = ""
        var tem_res: String? = ""
        thread {

            var request = Request.Builder().url(this.myHiStoryUrl).build()
            var response = this.client.newCall(request).execute()

            res = response.body()?.string()
            tem_res = res

            var doc = Jsoup.parse(tem_res)
            var r1 = doc.select(".pmain table:nth-of-type(4) tbody tr")
            var h_name: String = ""
            var h_number: String = ""
            for (item: Element in r1) {
                h_name = item.select("td:nth-of-type(3)").html().toString()
                h_number = item.select("td:nth-of-type(2)").html().toString()
                val tem = LinkedHashMap<String, Any>()
                tem.put("h_name", h_name)
                tem.put("h_number", "索取号:" + h_number)
                this.h_books.add(tem)
            }
            this.h_books.removeAt(0)
            this.h_books.removeAt(h_books.size - 1)
            var msg: Message = Message()
            msg.what = 6
            hand.sendMessage(msg)



            Log.d("history:", r1.toString())


        }
    }



    fun myLogin(user: String, pw: String, hand: Handler) {
        val isWdLogin = Regex(".*?http:172.16.1.101:80/portalowa.*?")//判断是否校园网
        val isFailLogin = Regex(".*window.history.back.*")//判断是否失败
        val isSuccLogin = Regex(".*dzjs.login_form.*")//判断是否成功
        var res = ""
        if (user.isNotEmpty() && pw.isNotEmpty()) {
            //在主线程中开启一个网络线程

            thread {
                //发送参数
                val myinfo = FormBody.Builder()
                        .add("user", user)
                        .add("pw", pw)
                        .add("imageField.Y", "0")
                        .add("imageField.X", "0")
                        .build()
                //构建请求
                var request = Request.Builder().url(this.logUrl).post(myinfo).build()


                var response = this.client.newCall(request).enqueue(object : Callback {
                    override fun onResponse(call: Call, response: Response) {

                        var resText = response.body()?.string()
                        var temResText: String? = resText
                        var doc = Jsoup.parse(temResText)
                        res = doc.getElementsByTag("script").html().toString()
                        if (isSuccLogin.containsMatchIn(res)) {

                            var msg: Message = Message()
                            msg.what = 1
                            var temData = Bundle()
                            temData.putString("user", user)
                            temData.putString("pw", pw)
                            msg.data = temData
                            hand.sendMessage(msg)

                        } else if (isFailLogin.containsMatchIn(res)) {
                            var msg: Message = Message()
                            msg.what = 2
                            hand.sendMessage(msg)


                        } else if (isWdLogin.containsMatchIn(res)) {
                            var msg: Message = Message()
                            msg.what = 3
                            hand.sendMessage(msg)


                        } else {
                            var msg: Message = Message()
                            msg.what = 6
                            hand.sendMessage(msg)
                            Log.d("re:", res)

                        }
                    }

                    override fun onFailure(call: Call, e: IOException) {
                        var msg: Message = Message()
                        msg.what = 3
                        hand.sendMessage(msg)
                    }
                })


            }


        } else {
            var msg: Message = Message()
            msg.what = 5
            hand.sendMessage(msg)
        }


    }

    fun mySearch(hand: Handler, bname: String) {
        if (this.loginFlag == 0) {

            thread {
                //发送参数
                val search_info = FormBody.Builder()
                        .add("txtWxlx", "CN")
                        .add("hidWxlx", "spanCNLx")
                        .add("txtPY:", "HZ")
                        .add("txtTm", bname)
                        .add("txtLx", "%")
                        .add("txtSearchType", "2")
                        .add("nMaxCount", "5000")
                        .add("nSetPageSize", "50")
                        .add("cSortFld", "正题名")
                        .add("B1", "检索")
                        .build()
                //构建请求
                var request3 = Request.Builder().url(this.bookSearchUrl).post(search_info).build()
                var response = this.client.newCall(request3).execute()
                var resText = response.body()?.string()
                var temResText: String? = resText
                var doc3 = Jsoup.parse(temResText)
                var temDoc = doc3.select("table[width=\"98%\"] tbody tr:not(table[width=\"98%\"] tbody tr:first-child)")

                Log.d("con", temDoc.toString())
                var s_name: String = ""
                var s_number: String = ""
                var s_author: String = ""
                var s_time: String = ""
                for (item: Element in temDoc) {
                    s_name = item.select("td:nth-of-type(3) a").html().toString()
                    s_time = item.select("td:nth-of-type(6)").html().toString()
                    s_author = item.select("td:nth-of-type(4)").html().toString()
                    s_number = item.select("td:nth-of-type(2)").html().toString()
                    val tem = LinkedHashMap<String, Any>()
                    tem.put("search_b_name", s_name.replace("&nbsp;", "", false))
                    tem.put("search_b_time", "出版:" + s_time.replace("&nbsp;", "", false))
                    tem.put("search_b_author", "作者：" + s_author.replace("&nbsp;", "", false))
                    tem.put("search_b_number", "索取号：" + s_number.replace("&nbsp;", "", false))
                    this.s_books.add(tem)
                }
                var msg: Message = Message()
                msg.what = 6
                hand.sendMessage(msg)


            }


        } else {
            Toast.makeText(contextThis, "请先登录", Toast.LENGTH_SHORT).show()
        }
    }

    fun myContinue(b_id: String, hand: Handler) {
        val isNot = Regex(".*?\\u6700\\u9ad8\\u7eed\\u501f.*?")//判断是否校园网
        val isOk=Regex("..")
        thread {

            var request3 = Request.Builder().get().url("${this.myContinueUrl}?nbsl=${b_id}").build()
            var response = this.client.newCall(request3).execute()
            var resText = response.body()?.string()
            var temResText: String? = resText
            var doc3 = Jsoup.parse(temResText)
            var res = doc3.getElementsByTag("script").html().toString()
            if(isNot.containsMatchIn(res)){
                 var msg: Message = Message()
                 msg.what = 8
                 hand.sendMessage(msg)
            }else{
                 var msg: Message = Message()
                 msg.what = 7
                 hand.sendMessage(msg)
            }
            Log.d("续借：", res)


            var msg: Message = Message()
            msg.what = 6
            hand.sendMessage(msg)


        }
    }


}