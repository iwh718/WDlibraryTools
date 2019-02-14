package com.simplewen.win0.wd.modal

class PreData{
    companion object {
        //登录
        val loginIndexUrl = ""
        //搜索
        val searchUrl = ""
        //续借
        val continueUrl =""
        //挂失
        val dismissUrl = ""
        //在借
        val orderingUrl = ""
        //历史
        val orderedUrl = ""
    }
}

/**
 * 搜索图书数据
 * @param bookName 图书名
 * @param bookId 图书编号
 * @param bookDesc 简介
 * @param bookTime 出版时间
 * @param bookAuthor 作者
 * @param bookFrom 出版社
 */
data class SearchBooksData(val bookName:String,val bookId:String,val bookDesc:String,val bookTime:String,val bookAuthor:String,val bookFrom:String)

/**
 * 我的在借图书
 * @param bookName 图书名
 * @param bookId 图书编号
 * @param orderEnd 限还日期
 * @param orderStart 借阅日期
 */
data class MyBookData(val bookName:String,val bookId:String,val orderStart:String,val orderEnd:String)

data class HistoryBookData(val bookName:String)