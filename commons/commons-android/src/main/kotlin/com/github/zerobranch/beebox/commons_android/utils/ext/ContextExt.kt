package com.github.zerobranch.beebox.commons_android.utils.ext

import android.accessibilityservice.AccessibilityServiceInfo
import android.app.Activity
import android.app.ActivityManager
import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.ComponentName
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.media.AudioManager
import android.net.Uri
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Process
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.accessibility.AccessibilityManager
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.annotation.AnimRes
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.annotation.FloatRange
import androidx.annotation.FractionRes
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.app.ActivityOptionsCompat
import androidx.core.app.ShareCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.github.zerobranch.beebox.commons_android.R
import java.util.Locale
import kotlin.reflect.KClass

val Context.audioManager
    get() = getSystemService(Context.AUDIO_SERVICE) as AudioManager

fun Context.colorInt(@ColorRes colorId: Int) = ContextCompat.getColor(this, colorId)

fun Context.getDrawableCompat(@DrawableRes drawableId: Int): Drawable =
    AppCompatResources.getDrawable(this, drawableId)!!

fun Context.drawable(@DrawableRes drawableResId: Int): Drawable? =
    ContextCompat.getDrawable(this, drawableResId)

fun Context.dpToPx(dp: Int): Int {
    return dp * (resources.displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT)
}

fun Context.getFraction(@FractionRes fractionResId: Int): Float = resources.getFraction(fractionResId, 1, 1)

fun Context.toPx(@DimenRes dimenReId: Int) = resources.getDimensionPixelSize(dimenReId)

fun getColorIntHex(@ColorInt colorId: Int): String =
    String.format(
        "#%02x%02x%02x",
        Color.red(colorId),
        Color.green(colorId),
        Color.blue(colorId)
    )

fun Context.getColorHex(@ColorRes colorId: Int): String =
    getColorIntHex(colorInt(colorId))

fun Context.isOtherProcess(): Boolean {
    val processName = processName()
    return processName != packageName && processName.isNotBlank()
}

fun Context.processName(): String {
    if (VERSION.SDK_INT >= VERSION_CODES.P) {
        return Application.getProcessName()
    } else {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager ?: return ""

        if (manager.runningAppProcesses == null) {
            return ""
        }

        for (processInfo in manager.runningAppProcesses) {
            if (processInfo.pid == Process.myPid()) {
                return processInfo.processName
            }
        }
        return ""
    }
}

fun Context.getActivity(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) {
            return context
        }
        context = context.baseContext
    }
    return null
}

val Context.screenWidth
    get() = resources.displayMetrics.widthPixels

val Context.screenHeight
    get() = resources.displayMetrics.heightPixels

fun Context.getLocaledResources(locale: Locale = Locale.getDefault()): Resources =
    createConfigurationContext(
        Configuration(resources.configuration).apply { setLocale(locale) }
    ).resources

fun Context.isClickToScreenLeftSide(
    touchX: Float,
    @FloatRange(from = 0.0, to = 1.0) sidePart: Float
): Boolean = touchX.toInt() in 0..(screenWidth * sidePart).toInt()

fun Context.isClickToScreenRightSide(
    touchX: Float,
    @FloatRange(from = 0.0, to = 1.0) sidePart: Float
): Boolean {
    val screenWidth = screenWidth
    val partInPx = (screenWidth * sidePart).toInt()
    return touchX.toInt() in (screenWidth - partInPx)..screenWidth
}

fun Context.openInDefaultBrowser(url: String) =
    openInDefaultBrowser(Uri.parse(url))

fun Context.openInDefaultBrowser(url: Uri) {
    runCatching {
        ContextCompat.startActivity(this, Intent(Intent.ACTION_VIEW, url), null)
    }
}

fun Context.openInBrowser(
    url: String,
    @AnimRes enterResId: Int,
    @AnimRes exitResId: Int,
) = openInBrowser(Uri.parse(url), enterResId, exitResId)

fun Context.openInCustomTabs(
    url: String,
    @ColorInt toolbarColor: Int,
    @AnimRes enterResId: Int,
    @AnimRes exitResId: Int,
    @AnimRes enterPopResId: Int,
    @AnimRes exitPopResId: Int
) = openInCustomTabs(Uri.parse(url), toolbarColor, enterResId, exitResId, enterPopResId, exitPopResId)

fun Context.openInCustomTabs(
    url: Uri,
    @ColorInt toolbarColor: Int,
    @AnimRes enterResId: Int,
    @AnimRes exitResId: Int,
    @AnimRes enterPopResId: Int,
    @AnimRes exitPopResId: Int
) {
    runCatching {
        CustomTabsIntent
            .Builder()
            .setDefaultColorSchemeParams(
                CustomTabColorSchemeParams.Builder()
                    .setNavigationBarColor(Color.WHITE)
                    .setNavigationBarDividerColor(Color.WHITE)
                    .setToolbarColor(toolbarColor)
                    .build()
            )
            .setStartAnimations(this, enterResId, exitResId)
            .setExitAnimations(
                this,
                enterPopResId,
                exitPopResId
            )
            .setColorScheme(CustomTabsIntent.COLOR_SCHEME_LIGHT)
            .setShareState(CustomTabsIntent.SHARE_STATE_OFF)
            .setShowTitle(true)
            .setUrlBarHidingEnabled(true)
            .build()
            .launchUrl(this, url)
    }.onFailure { openInBrowser(url, enterResId, exitResId) }
}

fun Context.openInBrowser(url: Uri, @AnimRes enterResId: Int, @AnimRes exitResId: Int) {
    runCatching {
        ContextCompat.startActivity(
            this,
            Intent(Intent.ACTION_VIEW, url),
            ActivityOptionsCompat.makeCustomAnimation(
                this,
                enterResId,
                exitResId
            ).toBundle()
        )
    }
}

fun Context.openInMail(
    whom: String,
    title: String,
    content: String,
    attachFiles: ArrayList<Uri>? = null,
    createChooserTitle: String? = null
) {
    val chooserTitle = createChooserTitle ?: getString(R.string.utils_android_choose_email_client)
    val mailto = "mailto:"
    val emailIntent = Intent()
        .apply {
            putExtra(Intent.EXTRA_EMAIL, arrayOf(whom))
            putExtra(Intent.EXTRA_SUBJECT, title)
            putExtra(Intent.EXTRA_TEXT, content)

            if (attachFiles.isNullOrEmpty()) {
                action = Intent.ACTION_SENDTO
                data = Uri.parse(mailto)
            } else {
                selector = Intent(Intent.ACTION_SENDTO).apply { data = Uri.parse(mailto) }
                action = Intent.ACTION_SEND_MULTIPLE
                putParcelableArrayListExtra(Intent.EXTRA_STREAM, attachFiles)
            }
        }

    runCatching {
        startActivity(Intent.createChooser(emailIntent, chooserTitle))
    }.onFailure {
        Toast.makeText(this, R.string.utils_android_email_client_not_found, Toast.LENGTH_SHORT).show()
    }
}

fun Context.openDeepLink(link: String) =
    runCatching { startActivity(Intent(Intent.ACTION_VIEW, link.toUri())) }

fun Context.openInMarket(marketLink: String) =
    runCatching { startActivity(Intent(Intent.ACTION_VIEW, marketLink.toUri())) }

fun Context.isAppInstalled(packageName: String): Boolean =
    runCatching {
        packageManager.getPackageInfo(packageName, 0)
        true
    }.getOrNull() ?: false

val Context.webViewVersion: String
    get() = runCatching {
        packageManager.getPackageInfo(
            "com.google.android.webview",
            0
        ).versionName
    }.getOrNull() ?: "unknown"

fun Context.openApp(packageName: String) {
    startActivity(packageManager.getLaunchIntentForPackage(packageName) ?: return)
}

fun Context.setComponentEnabled(component: KClass<*>, isEnable: Boolean) {
    val packageManager = packageManager
    val enabledState =
        if (isEnable) PackageManager.COMPONENT_ENABLED_STATE_ENABLED
        else PackageManager.COMPONENT_ENABLED_STATE_DISABLED

    packageManager.setComponentEnabledSetting(
        ComponentName(
            this,
            component.java
        ), enabledState, PackageManager.DONT_KILL_APP
    )
}

fun Context.rebirth() {
    val intent = packageManager.getLaunchIntentForPackage(packageName) ?: return
    val mainIntent = Intent.makeRestartActivityTask(intent.component)
    startActivity(mainIntent)
    Runtime.getRuntime().exit(0)
}

fun Context.copyToClipboard(label: String, text: String) {
    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager ?: return
    clipboard.setPrimaryClip(ClipData.newPlainText(label, text))
}

fun importFiles(chooserTitle: String? = null): Intent =
    Intent.createChooser(
        Intent(Intent.ACTION_GET_CONTENT).apply { type = "*/*" },
        chooserTitle
    )

fun Context.share(
    text: String,
    targetPackage: String? = null,
    chooserTitle: String? = null,
    attachFiles: List<Uri> = emptyList()
) {
    runCatching {
        Intent.createChooser(Intent(), null)
        ShareCompat.IntentBuilder(this).apply {
            if (attachFiles.isEmpty()) {
                setType("text/plain")
            } else {
                val ext = MimeTypeMap.getFileExtensionFromUrl(attachFiles.first().toString())
                setType("application/$ext")
            }

            targetPackage?.run { intent.setPackage(targetPackage) }

            setText(text)
            setChooserTitle(chooserTitle ?: getString(R.string.utils_android_title_share))
            attachFiles.forEach { file -> addStream(file) }
            startChooser()
        }
    }.onFailure {
        Toast.makeText(this, R.string.utils_android_base_error, Toast.LENGTH_SHORT).show()
    }
}

fun Context.longToast(@StringRes title: Int) {
    Toast.makeText(this, title, Toast.LENGTH_LONG).show()
}

fun Context.shortToast(@StringRes title: Int) {
    Toast.makeText(this, title, Toast.LENGTH_SHORT).show()
}

fun Context.isScreenReaderEnabled(): Boolean {
    val am = getSystemService(Context.ACCESSIBILITY_SERVICE) as? AccessibilityManager
    if (am != null && am.isEnabled) {
        val serviceInfoList = am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_SPOKEN)
        return serviceInfoList.isNotEmpty()
    }
    return false
}

@Suppress("DEPRECATION")
val Context.installerPackageName: String?
    get() = runCatching {
        if (VERSION.SDK_INT >= VERSION_CODES.R) {
            packageManager.getInstallSourceInfo(packageName).installingPackageName
        } else {
            packageManager.getInstallerPackageName(packageName)
        }
    }.getOrNull()

fun Context.getStyleByAttr(@AttrRes attr: Int): Int {
    val typedArray = obtainStyledAttributes(TypedValue().data, intArrayOf(attr))
    val resId = typedArray.getResourceId(0, 0)
    typedArray.recycle()
    return resId
}

fun Context.getPlurals(
    @PluralsRes pluralsResId: Int,
    value: Int,
    formattedValue: Any? = null,
    @StringRes zeroValueStringResId: Int? = null
): String =
    if (value == 0 && zeroValueStringResId != null) {
        getString(zeroValueStringResId)
    } else {
        getLocaledResources()
            .getQuantityString(pluralsResId, value, formattedValue ?: value)
    }