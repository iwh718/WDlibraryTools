package com.simplewen.win0.wd.modal


data class BrowBooks(val browName:String,val browLastTime:String,val browNextTime:String,val browNumber:String)

/**
 * 搜索图书数据类
 * @param booksAuthor 作者
 * @param booksId 编号
 * @param booksName 书名
 * @param booksNumber 借阅号
 * @param booksTime 出版日期
 */
data class SearchBooks(val booksId:String,val booksName:String,val booksTime:String,val booksNumber:String,val booksAuthor:String )

/**
 * 期刊搜索数据
 * @param journalAuthor 作者
 * @param journalName 期刊名称
 * @param journalNumber 索取号
 * @param journalPress 出版社
 */
data class JournalBooks(val journalName:String,val journalPress:String,val journalNumber:String,val journalAuthor:String )

/**
 * 公告数据
 * @param noticeContent 公告内容
 * @param noticeTime 日期
 * @param noticeTitle 标题
 */
data class NoticeData(val noticeTitle:String,val noticeContent:String,val noticeTime:String)

/**
 * 历史借阅数据
 * @param historyName 书名
 * @param historyNumber 编号
 */
data class HistoreBooks(val historyName:String,val historyNumber:String)
