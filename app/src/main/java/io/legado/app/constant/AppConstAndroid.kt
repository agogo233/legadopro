package io.legado.app.constant

import android.content.pm.PackageManager
import androidx.annotation.Keep
import io.legado.app.App
import io.legado.app.BuildConfig

/**
 * AppConst 安卓半区(appCtx/BuildConfig 绑定, 进不了 commonMain)。
 * 以同包扩展属性挂在 [AppConst] 上, 调用处 AppConst.xxx 写法不变。
 */

@Keep
data class AppInfo(
    var versionCode: Long = 0L,
    var versionName: String = "",
)

private val appInfoInternal: AppInfo by lazy {
    val appInfo = AppInfo()
    @Suppress("DEPRECATION")
    App.instance.packageManager.getPackageInfo(
        App.instance.packageName,
        PackageManager.GET_ACTIVITIES
    )
        ?.let {
            appInfo.versionName = it.versionName!!

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                appInfo.versionCode = it.longVersionCode
            } else {
                @Suppress("DEPRECATION")
                appInfo.versionCode = it.versionCode.toLong()
            }
        }
    appInfo
}

val AppConst.appInfo: AppInfo get() = appInfoInternal

/**
 * The authority of a FileProvider defined in a <provider> element in your app's manifest.
 */
val AppConst.authority: String get() = BuildConfig.APPLICATION_ID + ".fileProvider"
