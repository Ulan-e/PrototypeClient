package kg.optimabank.prototype.common.extensions

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import com.scottyab.rootbeer.RootBeer
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.lang.reflect.Method

class AppSafety(private val context: Context) {

    fun isPlayStoreVersion() = try {
        context.packageManager
            .getInstallerPackageName(context.packageName)
            ?.isNotEmpty()
    } catch (exception: Exception) {
        false
    }

    fun isDeviceRooted(): Boolean {
        val rootBeer = RootBeer(context)
        if (rootBeer.isRooted) {
            return true
        }
        return false
    }

    private var isRunningOnEmulator: Boolean? = null

    fun isRunningOnEmulator(): Boolean {
        var result = isRunningOnEmulator
        if (result != null) {
            return result
        }

        result = (Build.FINGERPRINT.startsWith("google/sdk_gphone_")
                && Build.FINGERPRINT.endsWith(":user/release-keys")
                && Build.MANUFACTURER == "Google" && Build.PRODUCT.startsWith("sdk_gphone_")
                && Build.BRAND == "google" && Build.MODEL.startsWith("sdk_gphone_"))
                || Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                // bluestacks
                || "QC_Reference_Phone" == Build.BOARD && !"Xiaomi".equals(Build.MANUFACTURER, ignoreCase = true)
                || Build.MANUFACTURER.contains("Genymotion")
                || Build.HOST == "Build2" //MSI App Player
                || Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic")
                || Build.PRODUCT == "google_sdk"
                // another Android SDK emulator check
                || SystemProperties.getProp("ro.kernel.qemu") == "1"
        isRunningOnEmulator = result
        return result
    }

    object SystemProperties {
        private var failedUsingReflection = false
        private var getPropMethod: Method? = null

        @SuppressLint("PrivateApi")
        fun getProp(propName: String, defaultResult: String = ""): String {
            if (!failedUsingReflection) try {
                if (getPropMethod == null) {
                    val clazz = Class.forName("android.os.SystemProperties")
                    getPropMethod = clazz.getMethod("get", String::class.java, String::class.java)
                }
                return getPropMethod!!.invoke(null, propName, defaultResult) as String? ?: defaultResult
            } catch (exception: Exception) {
                getPropMethod = null
                failedUsingReflection = true
            }
            var process: Process? = null
            try {
                process = Runtime.getRuntime().exec("getprop \"$propName\" \"$defaultResult\"")
                val reader = BufferedReader(InputStreamReader(process.inputStream))
                return reader.readLine()
            } catch (exception: IOException) {

            } finally {
                process?.destroy()
            }
            return defaultResult
        }
    }
}