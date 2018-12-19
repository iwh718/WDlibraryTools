
package com.simplewen.win0.ser
import okhttp3.Cookie
import java.io.Serializable

/**
 * 序列化的Cookie对象
 * @author cxm
 */
class SerializableCookie(cookie: Cookie) : Serializable {
    private val name: String?
    private val value: String?
    private val expiresAt: Long?
    private val domain: String?
    private val path: String?
    private val secure: Boolean?
    private val httpOnly: Boolean?
    private val hostOnly: Boolean?

    init {
        name = cookie.name()
        value = cookie.value()
        expiresAt = cookie.expiresAt()
        domain = cookie.domain()
        path = cookie.path()
        secure = cookie.secure()
        httpOnly = cookie.httpOnly()
        hostOnly = cookie.hostOnly()
    }

    /**
     * 从当前对象中参数生成一个Cookie
     * @author cxm
     */
    fun cookie(): Cookie {
        return Cookie.Builder()
                .name(name)
                .value(value)
                .expiresAt(expiresAt ?: 0L)
                .path(path)
                .let {
                    if (secure ?: false) it.secure()
                    if (httpOnly ?: false) it.httpOnly()
                    if (hostOnly ?: false)
                        it.hostOnlyDomain(domain)
                    else
                        it.domain(domain)
                    it
                }
                .build()
    }
}