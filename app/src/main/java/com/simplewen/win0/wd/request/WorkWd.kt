package com.simplewen.win0.wd.request

import android.content.Intent
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.View
import android.widget.BaseExpandableListAdapter
import android.widget.SimpleAdapter
import com.simplewen.win0.Utils.PersistentCookieStore
import com.simplewen.win0.wd.activity.LoginActivity
import com.simplewen.win0.wd.activity.SearchJournal
import com.simplewen.win0.wd.activity.WDMain
import com.simplewen.win0.wd.app.WdTools
import com.simplewen.win0.wd.base.BaseActivity
import com.simplewen.win0.wd.modal.*
import com.simplewen.win0.wd.util.NetError
import com.simplewen.win0.wd.util.Utils
import kotlinx.android.synthetic.main.activity_brow.*
import kotlinx.android.synthetic.main.activity_get_notice.*
import kotlinx.android.synthetic.main.content_wd_main.*
import kotlinx.android.synthetic.main.login_main.*
import kotlinx.coroutines.*
import okhttp3.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.io.IOException
import java.util.LinkedHashMap
import java.util.concurrent.TimeUnit

@ExperimentalCoroutinesApi
/**
 * 网络操作
 */
object WorkWd{

    private val cookieJar: CookieJar = object : CookieJar {
        private val map = PersistentCookieStore(WdTools.getContext())
        override fun saveFromResponse(url: HttpUrl, cookies: MutableList<Cookie>) {
            map[url.host()] = cookies
        }

        override fun loadForRequest(url: HttpUrl): MutableList<Cookie> {
            return map[url.host()] ?: ArrayList()
        }
    }//自定义cookieJar

    private const val logUrl = "http://172.16.1.43/dzjs/login.asp"//登录url
    private const val myBrowUrl = "http://172.16.1.43/dzjs/jhcx.asp"//我的借阅
    private const val myContinueUrl = "http://172.16.1.43/dzxj/dzxj.asp"//我的图书续借
    private const val myHiStoryUrl = "http://172.16.1.43/dzjs/dztj.asp"//我的借阅历史
    private const val bookInfoUrl = "http://172.16.1.43/showmarc/table.asp?nTmpKzh="//图书详情信息
    private const val noticeUrl = "http://172.16.1.43/ggtz/xiaoxi.asp"//通知链接
    private const val modifyPassUrl = "http://172.16.1.43/dzjs/modifyPw.asp"//修改密码
    private const val getQkUrl = "http://172.16.1.43/wxjs/chqkjs.asp"//期刊链接
    private const val bookSearchUrl = "http://172.16.1.43/wxjs/tmjs.asp"//搜索地址

    private val client = OkHttpClient.Builder().cookieJar(cookieJar).connectTimeout(8, TimeUnit.SECONDS).build() //初始化请求
    var books = ArrayList<Map<String, Any>>()//借阅
    var allJournalBooks = ArrayList<Map<String, Any>>()//期刊
    var allHistoryBooks = ArrayList<Map<String, Any>>()//历史
    var allSearchBooks = ArrayList<Map<String, Any>>()//搜索
    var bookInfo = ""//存放图书详情
    var loginFlag: Int = 0
    var userName = ""
    var notices = ArrayList<ArrayList<String>>()//公告内容+时间
    var noticeTitle = ArrayList<String>()//公告标题
    /**
     * 我的借阅图书
     * @param isUpdate 是否刷新
     * @param coroutines 协程上下文
     * @param refresh 刷新zujian
     * @param adapter 适配器
     */
    fun myBrow( coroutines: BaseActivity, refresh: SwipeRefreshLayout? = null, adapter: SimpleAdapter? = null) = coroutines.launch {
        var res: String?

        var tem_res: String?
        val request = Request.Builder().url(this@WorkWd.myBrowUrl).build()
        this@WorkWd.client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                this@WorkWd.books.clear()
                res = response.body()?.string()
                tem_res = res//临时存放string
                val doc2 = Jsoup.parse(tem_res)
                val temText = doc2.select("table[width=\"98%\"] tbody tr:not(table[width=\"98%\"] tbody tr:first-child)")
                for (item: Element in temText) {
                    val browData = BrowBooks(
                            item.select("td:nth-child(2)").html().toString(),
                            item.select("td:nth-child(4)").html().toString(),
                            item.select("td:nth-child(5)").html().toString(),
                            item.select("td:nth-child(8) a").attr("href").toString().replace("../dzxj/dzxj.asp?nbsl=", "")

                    )
                    with(LinkedHashMap<String, Any>()) {
                        put("b_name", browData.browName)
                        put("b_last", "借阅:${browData.browLastTime}")
                        put("b_next", "限还:${browData.browNextTime}")
                        put("b_continue", "续借")
                        put("b_id", browData.browNumber)
                        this@WorkWd.books.add(this)
                    }
                }
                Log.d("@@init_brow",this@WorkWd.books.toString())
                    coroutines.launch (Dispatchers.Main){
                            refresh!!.isRefreshing = false
                            adapter?.notifyDataSetChanged()
                            Utils.Tos("刷新完成！")
                    }
            }

            override fun onFailure(call: Call, e: IOException) {
                NetError().showError(coroutines)
                coroutines.refresh.isRefreshing = false
            }
        })

    }

    /**
     * 我的借阅历史
     * @param adapter 适配器
     * @param coroutines 协程上下文
     */
    fun myHisory(adapter: SimpleAdapter, coroutines: BaseActivity) = coroutines.launch {
        var res: String?
        var tem_res: String?
        this@WorkWd.allHistoryBooks.clear()
        val request = Request.Builder().url(this@WorkWd.myHiStoryUrl).build()
        this@WorkWd.client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                this@WorkWd.books.clear()
                res = response.body()?.string()
                tem_res = res
                val doc = Jsoup.parse(tem_res)
                val r1 = doc.select(".pmain table:nth-of-type(4) tbody tr")
                for (item: Element in r1) {
                    val historyData = HistoreBooks(item.select("td:nth-of-type(3)").html().toString(), item.select("td:nth-of-type(2)").html().toString())
                    val tem = LinkedHashMap<String, Any>()
                    tem["h_name"] = historyData.historyName
                    tem["h_number"] = "索取号:${historyData.historyNumber}"
                    this@WorkWd.allHistoryBooks.add(tem)
                }
                this@WorkWd.allHistoryBooks.removeAt(0)
                this@WorkWd.allHistoryBooks.removeAt(allHistoryBooks.size - 1)
                //切换到UI线程
               coroutines.launch(Dispatchers.Main) {
                   adapter.notifyDataSetChanged()
               }

            }

            override fun onFailure(call: Call, e: IOException) {
                coroutines.launch(Dispatchers.Main) {
                    Utils.Tos("网络连接失败！")
                }
            }
        })
    }

    /**
     * 登陆方法
     * @param user 用户名
     * @param pw 密码
     */
    fun myLogin(user: String, pw: String, coroutines: BaseActivity) = coroutines.launch{

        val isFailLogin = Regex(".*window.history.back.*")//判断是否失败
        val isSuccessLogin = Regex(".*dzjs.login_form.*")//判断是否成功
        var res: String


        //发送参数
        val myinfo = FormBody.Builder()
                .add("user", user)
                .add("pw", pw)
                .add("imageField.Y", "0")
                .add("imageField.X", "0")
                .build()
        val request = Request.Builder().url(this@WorkWd.logUrl).post(myinfo).build()
        this@WorkWd.client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val resText = response.body()?.string()
                val temResText: String? = resText
                val doc = Jsoup.parse(temResText)
                res = doc.getElementsByTag("script").html().toString()
                Log.d("@@loginres:", res)
                when {
                    isSuccessLogin.containsMatchIn(res) -> {
                        res = res.replace("，欢迎您登录！\\n离开时,不要忘记安全退出！\");", "")
                                .replace("window.alert(\"", "")
                                .replace("window.location=\"../dzjs/login_form.asp\";", "")
                                .replace("\$nbsp;", "")
                                .replace(" ", "")
                        //保留用户名
                        this@WorkWd.userName = res
                        this@WorkWd.loginFlag = 1
                        coroutines.launch {
                            withContext(Dispatchers.Main) {
                                //Utils.Tos("登陆成功！")
                                //存储登录信息到本地私有目录
                                iwhDataOperator
                                        .setSHP("user", user, "wd")
                                        .setSHP("pw", pw, "wd")
                                        .setSHP("flag", "true", "wd")
                                //设置单例网络管理
                                coroutines.launch (Dispatchers.Main){
                                    coroutines.startActivity(Intent(coroutines, WDMain::class.java))
                                    coroutines.finish()
                                }

                            }
                        }
                    }
                    isFailLogin.containsMatchIn(res) -> {

                        //切换到UI协程
                        coroutines.launch {
                            coroutines.login_sign.visibility = View.VISIBLE
                            coroutines.login_progressbar.visibility = View.GONE
                            withContext(Dispatchers.Main) {
                                Utils.Tos("登陆失败，账号或密码错误！")
                                coroutines.login_sign.visibility = View.VISIBLE
                                coroutines.login_progressbar.visibility = View.GONE
                                coroutines.user.visibility = View.VISIBLE
                                coroutines.pw.visibility = View.VISIBLE
                            }

                        }

                    }
                }

            }

            override fun onFailure(call: Call, e: IOException) {
                Log.d("@@timeout", "----------")
                coroutines.launch (Dispatchers.Main){
                    Utils.Tos("网络连接失败！")
                    coroutines.login_sign.visibility = View.VISIBLE
                    coroutines.login_progressbar.visibility = View.GONE
                    coroutines.user.visibility = View.VISIBLE
                    coroutines.pw.visibility = View.VISIBLE

                }
            }
        })
    }

    /**@param mySearch 搜索**/
    /**@param bname 书名**/
    /**@param mode 搜索模式**/
    /**@param sort 搜索方式**/

    fun mySearch(bname: String, mode: String = "1", sort: String = "正题名", coroutines: BaseActivity, adapter: SimpleAdapter) = coroutines.launch {

        //发送参数
        val search_info = FormBody.Builder().add("txtWxlx", "CN").add("hidWxlx", "spanCNLx").add("txtPY:", "HZ")
                .add("txtTm", bname).add("txtLx", "%").add("txtSearchType", mode).add("nMaxCount", "5000").add("nSetPageSize", "50").add("cSortFld", sort).add("B1", "检索")
                .build()
        //构建请求
        this@WorkWd.client.newCall(Request.Builder().url(this@WorkWd.bookSearchUrl).post(search_info).build()).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                coroutines.launch(Dispatchers.Main) {
                    Utils.Tos("网络连接出错！")
                }
            }

            override fun onResponse(call: Call, response: Response) {
                this@WorkWd.allSearchBooks.clear()
                val resText = response.body()?.string()
                val temResText: String? = resText?.replace("&nbsp;", "", false)
                val doc3 = Jsoup.parse(temResText)
                val temDoc = doc3.select("table[width=\"98%\"] tbody tr:not(table[width=\"98%\"] tbody tr:first-child)")
                for (item: Element in temDoc) {
                    val searchBooks = SearchBooks(
                            item.select("td:nth-of-type(7) a").attr("href").toString().replace("../dzyy/default.asp?nTmpKzh=", ""),
                            item.select("td:nth-of-type(3) a").html().toString(),
                            item.select("td:nth-of-type(6)").html().toString(),
                            item.select("td:nth-of-type(2)").html().toString(),
                            item.select("td:nth-of-type(4)").html().toString())
                    with(LinkedHashMap<String, Any>()) {
                        put("search_b_id", searchBooks.booksId)
                        put("search_b_name", searchBooks.booksName)
                        put("search_b_time", "出版:" + searchBooks.booksTime)
                        put("search_b_author", "作者：" + searchBooks.booksAuthor)
                        put("search_b_number", "索取号：" + searchBooks.booksNumber)
                        this@WorkWd.allSearchBooks.add(this)
                    }

                }
                //切换到UI线程
                coroutines.launch(Dispatchers.Main) {
                    if (this@WorkWd.allSearchBooks.size < 1) {
                        Utils.Tos("没有找到，换个搜索词？")
                    }
                    adapter.notifyDataSetChanged()
                }

            }
        })

    }

    /**
     * 图书续借
     * @param b_id 续借图书id
     * @param coroutines 协程上下文
     */
    fun myContinue(b_id: String, coroutines: BaseActivity) {
        val isNot = Regex(".*?\\u6700\\u9ad8\\u7eed\\u501f.*?")//判断是否校园网
        Log.d("@@continue_init", "----------$b_id")

        this@WorkWd.client.newCall(Request.Builder().url("${this@WorkWd.myContinueUrl}?nbsl=${b_id}").build()).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                coroutines.launch {
                    withContext(Dispatchers.Main) {
                        Utils.Tos("网络连接失败！")
                    }
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val resText = response.body()?.string()
                val temResText: String? = resText
                //  Log.d("@@continue:", temResText)
                val doc3 = Jsoup.parse(temResText)
                val res = doc3.getElementsByTag("script").html().toString()
                coroutines.launch(Dispatchers.Main) {
                    if (isNot.containsMatchIn(res)) {
                        Utils.Tos("续借失败，你已经续借过！")
                    } else {
                        Utils.Tos("续借完成，请下拉刷新一下！")
                    }
                }
            }
        })

    }

    /**
     * 图书简介
     * @param id 图书id
     * @param coroutines 协程上下文
     */
    fun bookInfo(id: String, coroutines: BaseActivity) = coroutines.launch {
        val _this = this@WorkWd
        val request = Request.Builder().url("$bookInfoUrl$id").build()
        this@WorkWd.client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val res = response.body()?.string()
                Log.d("res", res)

                val temRes = res//临时存放string
                val doc2 = Jsoup.parse(temRes)
                Log.d("doc", doc2.toString())
                val temText = doc2.select(".panelContentContainer div:nth-of-type(3) table tr:nth-of-type(7)")
                _this.bookInfo = temText.toString().replace("&nbsp;", "").replace("<td>", "")
                        .replace("</td>", "").replace("<tr>", "")
                        .replace("</tr>", "")
                        .replace(" ", "")//移除空格编码
                // Log.d("books.Info", _this.bookInfo)

                // Log.d("length", _this.bookInfo.length.toString())

                coroutines.launch(Dispatchers.Main) {
                    if (_this.bookInfo.length < 5) {
                        Utils.Tos("这本书没有简介哦！")

                    } else {
                        AlertDialog.Builder(coroutines).setTitle("本书简介")
                                .setMessage(_this.bookInfo).create().show()
                    }
                }

            }

            override fun onFailure(call: Call, e: IOException) {
                NetError().showError(coroutines)
            }
        })


    }

    //获取通知
    fun getNotice(coroutines: BaseActivity, adapter: BaseExpandableListAdapter? = null) = coroutines.launch outer@{


        val request = Request.Builder().url(this@WorkWd.noticeUrl).build()//获取公告
        this@WorkWd.client.newCall(request).enqueue(object : Callback {

            override fun onResponse(call: Call, response: Response) {
                this@WorkWd.noticeTitle.clear()
                this@WorkWd.notices.clear()
                val res = response.body()?.string()
                val tem_res = res?.replace("&nbsp;", "", false)//临时存放string
                val doc2 = Jsoup.parse(tem_res)
                val temText = doc2.select(".pmain table")

                temText.removeAt(0)//移除第一个导航
                for (item: Element in temText) {
                    val noticeData = NoticeData(
                            item.select("tr:nth-of-type(1) td font b").html().toString(),
                            item.select("tr:nth-of-type(2) td").html().toString(),
                            item.select("tr:nth-of-type(3) td font").html().toString())
                    val tem = arrayListOf<String>()
                    tem.add(0, noticeData.noticeContent)
                    tem.add(noticeData.noticeTime)
                    this@WorkWd.noticeTitle.add(noticeData.noticeTitle)//添加公告
                    this@WorkWd.notices.add(tem)
                }
                coroutines.launch(Dispatchers.Main) {
                    adapter?.let {
                        adapter.notifyDataSetChanged()
                        coroutines.notice_refresh.isRefreshing = false
                        return@launch
                    }
                    coroutines.noticeTextSwitcher.text = this@WorkWd.notices[0][0]

                }
            }

            override fun onFailure(call: Call, e: IOException) {
                NetError().showError(coroutines)
                coroutines.notice_refresh.isRefreshing = false
            }
        })
    }

    //搜搜期刊
    fun getJournal(coroutines: BaseActivity, title: String, adapter: SimpleAdapter) = coroutines.launch {
        this@WorkWd.allJournalBooks.clear()
        //发送参数
        val gsinfo = FormBody.Builder().add("txttiming", title)
                .add("mnuzhengtiming", "").add("txtzuoze", "").add("mnuzuozhe", "XXX")
                .add("txtzhuti", "").add("mnuzhuti", "XXX").add("txtfenlei", "")
                .add("mnufenlei", "XXX").add("txtbianhao", "")
                .add("mnuchubanwuhao", "XXX").add("txtguanjianci", "")
                .add("QKLX", "现刊").add("txtxiankanyear", "2018")
                .add("btnsubmit", "检索").build()
        //构建请求
        val request = Request.Builder().url(this@WorkWd.getQkUrl).post(gsinfo).build()

        val mThis = this@WorkWd
        this@WorkWd.client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {

                val resText = response.body()?.string()
                val temResText: String? = resText?.replace("&nbsp;", "", false)
                val doc3 = Jsoup.parse(temResText)
                val temDoc = doc3.select(".tableblack table tbody tr:nth-of-type(2) table:nth-of-type(2) tr:nth-of-type(2)")
                for (item: Element in temDoc) {
                    val journals = JournalBooks(
                            item.select("td:nth-of-type(1) a").html().toString(),
                            item.select("td:nth-of-type(8)").html().toString(),
                            item.select("td:nth-of-type(2)").html().toString(),
                            item.select("td:nth-of-type(4)").html().toString())

                    val tem = LinkedHashMap<String, Any>()
                    tem["search_b_name"] = journals.journalName
                    tem["search_b_company"] = "出版:" + journals.journalPress
                    tem["search_b_author"] = "作者：" + journals.journalAuthor
                    tem["search_b_number"] = "索取号：" + journals.journalNumber
                    mThis.allJournalBooks.add(tem)
                }

                coroutines.launch(Dispatchers.Main) {
                    if (this@WorkWd.allJournalBooks.size < 1) {
                        Utils.Tos("没有找到哦！")
                    }
                    (coroutines as SearchJournal).apply {
                        this.temLoadView.dismiss()
                    }
                    adapter.notifyDataSetChanged()
                }

            }

            override fun onFailure(call: Call, e: IOException) {
                coroutines.launch(Dispatchers.Main) {
                    NetError().showError(coroutines)
                }
            }
        })


    }


    /**
     * 修改密码
     * @param user 用户名
     *
     * @param pwNew 新密码
     * @param coroutines 协程上下文
     */
    fun modifyPass(user: String, pwNew: String, pwOld: String, coroutines: BaseActivity) = coroutines.launch {
        val errorInfo = Regex(".*?\\u9519\\u8bef.*?")//信息不匹配
        var res: String
        //发送参数
        val gsInfo = FormBody.Builder()
                .add("user", user)
                .add("pw", pwOld)
                .add("pw1", pwNew)
                .add("pw2", pwNew)
                .add("submit1", "提 交")
                .build()
        //构建请求
        val request = Request.Builder().url(this@WorkWd.modifyPassUrl).post(gsInfo).build()
        this@WorkWd.client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val resText = response.body()?.string()
                val temResText: String? = resText
                val doc = Jsoup.parse(temResText)
                res = doc.html().toString()

                coroutines.launch(Dispatchers.Main) {
                    if (errorInfo.containsMatchIn(res)) {
                        Utils.Tos("密码与账号不匹配！")
                    } else {
                        Utils.Tos("修改完成，请重新登录！")
                        coroutines.startActivity(Intent(coroutines, LoginActivity::class.java))
                        coroutines.finish()
                    }

                }
            }

            override fun onFailure(call: Call, e: IOException) {
                NetError().showError(coroutines)
            }
        })


    }

}