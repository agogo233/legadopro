package io.legado.app.ui.book.read.page

import android.net.Uri
import android.os.Build
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.Typeface
import io.legado.app.ui.platform.sharedAppContext
import io.legado.app.utils.isContentScheme

/**
 * Android actual: 按路径加载 [android.graphics.Typeface] (见 [loadAndroidTypeface]), 包进
 * Compose [Typeface]。普通路径对照原版 `TextStyleProvider.getTypeface` 走
 * `Typeface.createFromFile`; SAF content URI 的授权通道 (`openFileDescriptor` +
 * `Typeface.Builder`) 为本次新增 (原版该路径下 `createFromFile` 直接失败)。
 */
actual fun loadReaderFontFamily(path: String): FontFamily? =
    loadAndroidTypeface(path)?.let { FontFamily(Typeface(it)) }

/**
 * 按路径加载 [android.graphics.Typeface], 渲染侧 [loadReaderFontFamily] 与度量侧
 * (app 端 `MainActivity.readerMeasureTypeface`) 共用同一函数, 保证两侧读同一字体。
 *
 * - 普通文件路径: `Typeface.createFromFile`
 * - content URI (SAF 授权字体): `contentResolver.openFileDescriptor` + `Typeface.Builder` (API 26+);
 *   低于 O 无 Builder, 返回 null 回落默认字体 (与度量侧同源, 两侧一致)
 * - 失败返回 null, 由 [resolveReaderFontFamily] 统一回退
 */
fun loadAndroidTypeface(path: String): android.graphics.Typeface? = runCatching {
    when {
        path.isContentScheme() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
            val ctx = sharedAppContext ?: return@runCatching null
            ctx.contentResolver.openFileDescriptor(Uri.parse(path), "r")
                ?.use { android.graphics.Typeface.Builder(it.fileDescriptor).build() }
        }
        path.isContentScheme() -> null
        path.isNotEmpty() -> android.graphics.Typeface.createFromFile(path)
        else -> null
    }
}.getOrNull()
