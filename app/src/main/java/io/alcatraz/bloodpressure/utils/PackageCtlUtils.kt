package io.alcatraz.bloodpressure.utils

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable

object PackageCtlUtils {
    fun getIcon(ctx: Context, pkg: String): Drawable? {
        val packageManager = ctx.packageManager
        val applicationInfo = packageManager.getApplicationInfo(pkg, PackageManager.GET_META_DATA)
        return packageManager.getApplicationIcon(applicationInfo)
    }

    fun getLabel(ctx: Context, pkg: String): String {
        val pm = ctx.packageManager
        return try {
            val ai = pm.getApplicationInfo(pkg, PackageManager.GET_META_DATA)
            pm.getApplicationLabel(ai).toString()
        } catch (e: PackageManager.NameNotFoundException) {
            pkg
        }
    }

    @Synchronized
    fun getVersionName(context: Context): String? {
        try {
            val packageManager = context.packageManager
            val packageInfo = packageManager.getPackageInfo(
                context.packageName, 0
            )
            return packageInfo.versionName
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    @Deprecated("Deprecated in Java")
    @Synchronized
    fun getVersionCode(context: Context): Int {
        try {
            val packageManager = context.packageManager
            val packageInfo = packageManager.getPackageInfo(
                context.packageName, 0
            )
            return packageInfo.versionCode
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return 0
    }
}
