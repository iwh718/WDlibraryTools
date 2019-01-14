
-verbose
# 避免混淆泛型
# 这在JSON实体映射时非常重要，比如fastJson
-keepattributes Signature
# 抛出异常时保留代码行号
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.** -keep class okio.** { *; }
-keep interface okio.** { *; }
-dontwarn okio.** -keep class com.hitomi.** { *; }
-keep interface com.hitomi.** { *; }
-dontwarn com.hitomi.**
-printmapping mapping.txt
# 对R文件下的所有类及其方法，都不能被混淆
-keepclassmembers class **.R$* {
    *;
}
# 不需要混淆android-support-v4.jar
-dontwarn android.support.v4.**
-keep class android.support.v4.** { *; }
-keep interface android.support.v4.app.** { *; }
-keep public class * extends android.support.v4.**
-keep public class * extends android.app.Fragment
-keepattributes *Annotation*
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService
-keep class android.support.v7.** { *; }    #过滤android.support.v7  注意这里v4还是v7要看gradle里面compile了那个扩展包
-keep interface android.support.constraint.** { *; }





