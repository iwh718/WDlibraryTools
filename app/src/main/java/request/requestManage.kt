package request

import android.content.Context
import android.os.*
import android.util.Log
import android.view.View
import android.widget.*
import com.simplewen.win0.Utils.PersistentCookieStore
import com.simplewen.win0.wd.R
import okhttp3.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.io.IOException
import java.lang.Exception
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*

import kotlin.concurrent.thread

class requestManage(val context: Context) {
    val  iwh = "校园工具，非盈利项目，非礼勿扰"
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
    private val logUrl = "http://172.16.1.43/dzjs/login.asp"//登录url
    private val myBrowUrl = "http://172.16.1.43/dzjs/jhcx.asp"//我的借阅
    private val myContinueUrl = "http://172.16.1.43/dzxj/dzxj.asp"//我的图书续借
    private val myHiStoryUrl = "http://172.16.1.43/dzjs/dztj.asp"//我的借阅历史
    private val bookInfoUrl = "http://172.16.1.43/showmarc/table.asp?nTmpKzh="//图书详情信息
    private val noticeUrl = "http://172.16.1.43/ggtz/xiaoxi.asp"//通知链接
    private val newsUrl = ""//新闻链接
    private val modifyPassUrl = "http://172.16.1.43/dzjs/modifyPw.asp"//修改密码
    private val getQkUrl = "http://172.16.1.43/wxjs/chqkjs.asp"//期刊链接
    private val gs_url = "http://172.16.1.43/dzjs/card_guashi.asp"//挂失图书证
    private val bookSearchUrl = "http://172.16.1.43/wxjs/tmjs.asp"//搜索地址
    val b_id = intArrayOf(R.id.b_name, R.id.b_last, R.id.b_next, R.id.b_continue, R.id.b_id)
    val client = OkHttpClient.Builder().cookieJar(cookieJar).build() //初始化请求
    var books = ArrayList<Map<String, Any>>()
    var qkBooks = ArrayList<Map<String, Any>>()//期刊
    var h_books = ArrayList<Map<String, Any>>()
    var s_books = ArrayList<Map<String, Any>>()
    var bookInfo = ""//存放图书详情
    var loginFlag: Int = 0
    var userName = ""
    var notices = ArrayList<ArrayList<String>>()//公告内容+时间
    var noticeTitle = ArrayList<String>()//公告标题


    fun myBrow(hand: Handler) {
        var res: String?
        var tem_res: String? = ""
        if (this.loginFlag == 0) {


            thread {
                val request = Request.Builder().url(this.myBrowUrl).build()
                this.client.newCall(request).enqueue(object : Callback {
                    override fun onResponse(call: Call, response: Response) {
                        res = response.body()?.string()
                        tem_res = res//临时存放string
                        val doc2 = Jsoup.parse(tem_res)
                        val temText = doc2.select("table[width=\"98%\"] tbody tr:not(table[width=\"98%\"] tbody tr:first-child)")
                        var (bname,blast,bnext,bid) = arrayOf("","","")
                       // Log.d("返回：", temText.toString())

                        for (item: Element in temText) {
                            bname = item.select("td:nth-child(2)").html().toString()
                            blast = item.select("td:nth-child(4)").html().toString()
                            bnext = item.select("td:nth-child(5)").html().toString()
                            bid = item.select("td:nth-child(8) a").attr("href").toString()//提取续借编号
                            bid = bid.replace("../dzxj/dzxj.asp?nbsl=", "")//提取编号
                            //Log.d("bid:",bid)
                            val tem = LinkedHashMap<String, Any>()
                            tem.put("b_name", bname)
                            tem.put("b_last", "借阅" + blast)
                            tem.put("b_next", "限还" + bnext)
                            tem.put("b_continue", "续借")
                            tem.put("bid", bid)
                            books.add(tem)


                        }
                        val msg = Message()
                        msg.what = 0x16
                        hand.sendMessage(msg)
                    }

                    override fun onFailure(call: Call, e: IOException) {
                        val msg = Message()
                        msg.what = 0x15
                        hand.sendMessage(msg)
                    }
                })

            }

        } else {
            Toast.makeText(contextThis, "请先登录", Toast.LENGTH_SHORT).show()
        }
    }

    fun myHisory(hand: Handler) {
        var res: String?
        var tem_res: String?
        thread {

            val request = Request.Builder().url(this.myHiStoryUrl).build()
            val response = this.client.newCall(request).execute()

            res = response.body()?.string()
            tem_res = res

            val doc = Jsoup.parse(tem_res)
            val r1 = doc.select(".pmain table:nth-of-type(4) tbody tr")
            var (h_name,h_number)  = arrayOf("","")
            for (item: Element in r1) {
                h_name = item.select("td:nth-of-type(3)").html().toString()
                h_number = item.select("td:nth-of-type(2)").html().toString()
                val tem = LinkedHashMap<String, Any>()
                tem.put("h_name", h_name)
                tem.put("h_number", "索取号:$h_number" )
                this.h_books.add(tem)
            }
            this.h_books.removeAt(0)
            this.h_books.removeAt(h_books.size - 1)
            val msg: Message = Message()
            msg.what = 6
            hand.sendMessage(msg)

        }
    }


    fun myLogin(user: String, pw: String, hand: Handler):Boolean{
        val isWdLogin = Regex(".*?http:172.16.1.101:80/portalowa.*?")//判断是否校园网
        val isFailLogin = Regex(".*window.history.back.*")//判断是否失败
        val isSuccLogin = Regex(".*dzjs.login_form.*")//判断是否成功
        var res = ""
        var loginFLag = false//登陆标志
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
                val request = Request.Builder().url(this.logUrl).post(myinfo).build()

                val _this = this
                this.client.newCall(request).enqueue(object : Callback {
                    override fun onResponse(call: Call, response: Response) {

                        var resText = response.body()?.string()
                        var temResText: String? = resText
                        var doc = Jsoup.parse(temResText)
                        res = doc.getElementsByTag("script").html().toString()

                        if (isSuccLogin.containsMatchIn(res)) {


                            res = res.replace("，欢迎您登录！\\n离开时,不要忘记安全退出！\");", "")
                                    .replace("window.alert(\"", "")
                                    .replace("window.location=\"../dzjs/login_form.asp\";", "")
                                    .replace("\$nbsp;", "")
                                    .replace(" ", "")

                            _this.userName = res
                            Log.d("login:", res)
                            val msg = Message()
                            msg.what = 1
                            val temData = Bundle()
                            temData.putString("user", user)
                            temData.putString("pw", pw)
                            msg.data = temData
                            hand.sendMessage(msg)
                            loginFLag = true


                        } else if (isFailLogin.containsMatchIn(res)) {
                            val msg: Message = Message()
                            msg.what = 2
                            hand.sendMessage(msg)


                        } else if (isWdLogin.containsMatchIn(res)) {
                            val msg: Message = Message()
                            msg.what = 3
                            hand.sendMessage(msg)


                        } else {
                            val msg: Message = Message()
                            msg.what = 6
                            hand.sendMessage(msg)
                            Log.d("re:", res)

                        }
                    }

                    override fun onFailure(call: Call, e: IOException) {
                        val msg = Message()
                        msg.what = 3
                        hand.sendMessage(msg)
                    }
                })


            }


        } else {
            val msg = Message()
            msg.what = 5
            hand.sendMessage(msg)
        }

        return loginFLag
    }

    /**user：账号，pw：密码，name：姓名**/
    fun gsCard(user: String, pw: String, name: String, hand: Handler) {
        val hasGs = Regex(".*?\\u5df2\\u7ecf\\u6302\\u5931\\u8fc7.*?")//已经挂失
        val noMatch = Regex(".*\\u4fe1\\u606f\\u4e0d\\u5339\\u914d.*")//信息不匹配
        val isOk = Regex(".*\\u6302\\u5931\\u6210\\u529f.*")//挂失完成
        var res = ""


            thread {
                //发送参数
                val gsinfo = FormBody.Builder()
                        .add("user", user)
                        .add("dzxm", name)
                        .add("dzkl", pw)
                        .add("cert_mode", "dzzh")
                        .add("submit1", "确认挂失登记")
                        .build()
                //构建请求
                val request = Request.Builder().url(this.gs_url).post(gsinfo).build()

                val _this = this
                var response = this.client.newCall(request).enqueue(object : Callback {
                    override fun onResponse(call: Call, response: Response) {

                        val resText = response.body()?.string()
                        val temResText: String? = resText
                        val doc = Jsoup.parse(temResText)
                        res = doc.getElementsByTag("script").html().toString()
                        Log.d("gs_card:", res)
                        if (hasGs.containsMatchIn(res)) {
                            val msg = Message()
                            msg.what = 0x11
                            hand.sendMessage(msg)
                        } else if (noMatch.containsMatchIn(res)) {
                            val msg = Message()
                            msg.what = 0x12
                            hand.sendMessage(msg)

                        } else if (isOk.containsMatchIn(res)) {
                            val msg = Message()
                            msg.what = 0x13
                            hand.sendMessage(msg)
                        }
                    }

                    override fun onFailure(call: Call, e: IOException) {
                        val msg = Message()
                        msg.what = 3
                        hand.sendMessage(msg)
                    }
                })


            }



    }

    /**@param mySearch 搜索**/
    /**@param bname 书名**/
    /**@param mode 搜索模式**/
    /**@param sort 搜索方式**/

    fun mySearch(hand: Handler, bname: String, mode: String, sort: String) {

        if (this.loginFlag == 0) {

            thread {
                //发送参数
                val search_info = FormBody.Builder()
                        .add("txtWxlx", "CN")
                        .add("hidWxlx", "spanCNLx")
                        .add("txtPY:", "HZ")
                        .add("txtTm", bname)
                        .add("txtLx", "%")
                        .add("txtSearchType", mode)
                        .add("nMaxCount", "5000")
                        .add("nSetPageSize", "50")
                        .add("cSortFld", sort)
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
                var s_name: String = ""//图书名
                var s_number: String = ""//图书借阅编号
                var s_author: String = ""//图书作者
                var s_time: String = ""//图书出版时间
                var s_id = ""//图书借阅id
                for (item: Element in temDoc) {
                    s_name = item.select("td:nth-of-type(3) a").html().toString()
                    s_time = item.select("td:nth-of-type(6)").html().toString()
                    s_author = item.select("td:nth-of-type(4)").html().toString()
                    s_number = item.select("td:nth-of-type(2)").html().toString()
                    s_id = item.select("td:nth-of-type(7) a").attr("href").toString()
                    s_id = s_id.replace("../dzyy/default.asp?nTmpKzh=", "")//分离出id
                    val tem = LinkedHashMap<String, Any>()
                    tem.put("search_b_id", s_id)
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

    /**@param myContinue 续借**/
    /**@param b_id 图书索取号**/
    /**@param hand handler**/

    fun myContinue(b_id: String, hand: Handler) {
        val isNot = Regex(".*?\\u6700\\u9ad8\\u7eed\\u501f.*?")//判断是否校园网
        val isOk = Regex("..")
        thread {

            val request3 = Request.Builder().get().url("${this.myContinueUrl}?nbsl=${b_id}").build()
            val response = this.client.newCall(request3).execute()
            var resText = response.body()?.string()
            var temResText: String? = resText
            var doc3 = Jsoup.parse(temResText)
            var res = doc3.getElementsByTag("script").html().toString()
            if (isNot.containsMatchIn(res)) {
                var msg: Message = Message()
                msg.what = 8
                hand.sendMessage(msg)
            } else {
                var msg: Message = Message()
                msg.what = 7
                hand.sendMessage(msg)
            }
            Log.d("续借：", res)


            var msg: Message = Message()
            msg.what = 6
            hand.sendMessage(msg)


        }
    }//续借


    fun bookInfo(id: String, hand: Handler) {
        val _this = this
        Log.d("bookid", id)
        thread {
            val request = Request.Builder().url("$bookInfoUrl$id").build()
            var response = this.client.newCall(request).enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    val res = response.body()?.string()
                    Log.d("res", res)

                    val tem_res = res//临时存放string
                    val doc2 = Jsoup.parse(tem_res)
                    Log.d("doc", doc2.toString())
                    val temText = doc2.select(".panelContentContainer div:nth-of-type(3) table tr:nth-of-type(7)")
                    _this.bookInfo = temText.toString().replace("&nbsp;", "").replace("<td>", "")
                            .replace("</td>", "").replace("<tr>", "")
                            .replace("</tr>", "")
                            .replace(" ", "")//移除空格编码
                    Log.d("books.Info", _this.bookInfo)
                    val msg = Message()
                    Log.d("length", _this.bookInfo.length.toString())
                    if (_this.bookInfo.length < 5) {
                        msg.what = 1

                    } else {
                        msg.what = 2
                    }
                    Log.d("返回：", temText.toString())
                    hand.sendMessage(msg)
                }

                override fun onFailure(call: Call, e: IOException) {
                    val msg = Message()
                    msg.what = 0
                    hand.sendMessage(msg)
                }
            })

        }

    }

    //获取通知
    fun getNotice(hand: Handler) {
        val _this = this
        this.noticeTitle.clear()
        this.notices.clear()
        thread {
            val request = Request.Builder().url(this.noticeUrl).build()//获取公告
            var response = this.client.newCall(request).enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    val res = response.body()?.string()
                    val tem_res = res//临时存放string
                    val doc2 = Jsoup.parse(tem_res)
                    Log.d("doc", doc2.toString())
                    val temText = doc2.select(".pmain table")

                    //noticdInfo  =   temText.toString().replace("&nbsp;","").replace(" ","")//移除空格编码
                    var n_title: String = ""//公告标题
                    var n_body: String = ""//公告内容
                    var n_time: String = ""//公告时间
                    temText.removeAt(0)//移除第一个导航
                    for (item: Element in temText) {
                        n_title = item.select("tr:nth-of-type(1) td font b").html().toString()
                        n_body = item.select("tr:nth-of-type(2) td").html().toString()
                        n_time = item.select("tr:nth-of-type(3) td font").html().toString()

                        val tem = arrayListOf<String>()

                        tem.add(0, n_body.replace("&nbsp;", "", false))
                        tem.add(n_time.replace("&nbsp;", "", false))
                        _this.noticeTitle.add(n_title)//添加公告
                        _this.notices.add(tem)
                        Log.d("noticeItem", tem.toString())
                        Log.d("noticetitle", _this.noticeTitle.toString())

                    }
                    val msg = Message()
                    msg.what = 0x14
                    Log.d("返回：", temText.toString())
                    hand.sendMessage(msg)
                }

                override fun onFailure(call: Call, e: IOException) {
                    val msg = Message()
                    msg.what = 0
                    hand.sendMessage(msg)
                }
            })

        }
    }

    //搜搜期刊
    fun getQk(hand: Handler,title:String) {
        this.qkBooks.clear()
        val noThings = "暂时没有哦"
        var res = ""
        if (true) {
            //在主线程中开启一个网络线程

            thread {
                //发送参数
                val gsinfo = FormBody.Builder()
                        .add("txttiming", title)
                        .add("mnuzhengtiming", "")
                        .add("txtzuoze", "")
                        .add("mnuzuozhe", "XXX")
                        .add("txtzhuti", "")
                        .add("mnuzhuti", "XXX")
                        .add("txtfenlei", "")
                        .add("mnufenlei", "XXX")
                        .add("txtbianhao", "")
                        .add("mnuchubanwuhao", "XXX")
                        .add("txtguanjianci", "")
                        .add("QKLX", "现刊")
                        .add("txtxiankanyear", "2018")
                        .add("btnsubmit", "检索")

                        .build()
                //构建请求
                val request = Request.Builder().url(this.getQkUrl).post(gsinfo).build()

                val _this = this
                this.client.newCall(request).enqueue(object : Callback {
                    override fun onResponse(call: Call, response: Response) {

                        val resText = response.body()?.string()
                        val temResText: String? = resText
                        val doc3 = Jsoup.parse(temResText)
                        var temDoc = doc3.select(".tableblack table tbody tr:nth-of-type(2) table:nth-of-type(2) tr:nth-of-type(2)")
                        Log.d("@@qk", temDoc.toString())
                        var s_name: String = ""//期刊名
                        var s_number: String = ""//期刊借阅编号
                        var s_author: String = ""//图书作者
                        var s_company: String = ""//图书出版社

                        for (item: Element in temDoc) {
                            s_name = item.select("td:nth-of-type(1) a").html().toString()
                            s_company = item.select("td:nth-of-type(8)").html().toString()
                            s_author = item.select("td:nth-of-type(4)").html().toString()
                            s_number = item.select("td:nth-of-type(2)").html().toString()

                            val tem = LinkedHashMap<String, Any>()

                            tem.put("search_b_name", s_name.replace("&nbsp;", "", false))
                            tem.put("search_b_company", "出版:" + s_company.replace("&nbsp;", "", false))
                            tem.put("search_b_author", "作者：" + s_author.replace("&nbsp;", "", false))
                            tem.put("search_b_number", "索取号：" + s_number.replace("&nbsp;", "", false))
                            _this.qkBooks.add(tem)
                        }
                        Log.d("@@qk:",_this.qkBooks.toString())
                        val msg = Message()
                        msg.what = 1
                        hand.sendMessage(msg)

                    }

                    override fun onFailure(call: Call, e: IOException) {
                        val msg = Message()
                        msg.what = 2
                        hand.sendMessage(msg)
                    }
                })


            }


        } else {
            val msg: Message = Message()
            msg.what = 0
            hand.sendMessage(msg)
        }
    }

    //检查超期
    /**@param books 借阅的图书集合
     * @param hand 主线程Handler
     **/
    fun checkBook(books:ArrayList<Map<String, Any>>,hand:Handler){

       fun parseTime(strTime:String):Int{

           val format = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)

           var time = 0L
             try {
                 time = format.parse(strTime).time
             } catch (e:Exception){
                 println(e)
             }
            return ((time - System.currentTimeMillis())/(1000*60*60*24)).toInt()
        }
        for (i in books){
            //Log.d("@@图书:",i.toString())
            Log.d("@@","借阅时间：${i["b_last"]},限还：${i["b_next"]}")
            val b_last = i["b_last"].toString().replace("借阅","")
            val b_next = i["b_next"].toString().replace("限还","")
            if(parseTime(b_last) > 2){
                val msg = Message()
                msg.what = 0x17
                hand.sendMessage(msg)
                Log.d("@@parse:","剩余：${parseTime(b_next)} 天")
                return
            }


        }


    }


    //修改密码
    fun modifyPass(user:String,pwNew:String,pwOld:String,hand:Handler):Boolean{
        val errorInfo = Regex(".*?\\u9519\\u8bef.*?")//信息不匹配
        var res = ""
            //在主线程中开启一个网络线程
            thread {
                //发送参数
                val gsinfo = FormBody.Builder()
                        .add("user", user)
                        .add("pw", pwOld)
                        .add("pw1", pwNew)
                        .add("pw2", pwNew)
                        .add("submit1", "提 交")
                        .build()
                //构建请求
                val request = Request.Builder().url(this.modifyPassUrl).post(gsinfo).build()
                val _this = this
                var response = this.client.newCall(request).enqueue(object : Callback {
                    override fun onResponse(call: Call, response: Response) {

                        val resText = response.body()?.string()
                        val temResText: String? = resText
                        val doc = Jsoup.parse(temResText)
                        res = doc.html().toString()
                        Log.d("&&修改密码：", res)
                        val msg = Message()
                        if(errorInfo.containsMatchIn(res)){
                            msg.what = 0x12

                        }else{
                            msg.what = 0x18
                        }
                        hand.sendMessage(msg)
                    }

                    override fun onFailure(call: Call, e: IOException) {
                        val msg = Message()
                        msg.what = 0x19
                        hand.sendMessage(msg)
                    }
                })


            }




        return true
    }


}