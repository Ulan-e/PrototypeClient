package kg.optimabank.prototype.data.preferences

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import kg.optimabank.prototype.dev.UserSettings
import kotlinx.coroutines.flow.Flow

class SessionPreferencesImpl(
    private val dataStore: DataStoreManager
) : SessionPreferences {

    companion object {
        const val PIN = "pin"
        const val IS_TOUCH_LOGIN = "is_login_touch"
    }

    override fun getPin(): Flow<String?> {
        return dataStore.readString(PIN)
    }

    override suspend fun savePin(pin: String) {
        dataStore.saveString(PIN, pin)
    }

    override suspend fun cleanPin() {
        dataStore.saveString(PIN, "")
    }

    override fun isTouchLoginEnabled(): Flow<Boolean> {
        return dataStore.readBoolean(IS_TOUCH_LOGIN)
    }

    override suspend fun setTouchLoginEnabled(isEnabled: Boolean) {
        dataStore.saveBoolean(IS_TOUCH_LOGIN, isEnabled)
    }

    override suspend fun setUserPreferences(userSettings: UserSettings?) {
        userSettings?.let { dataStore.saveUserSettings(it) }
    }

    override fun getUserPreferences(): Flow<UserSettings> {
        return dataStore.getUserSettings()
    }

    override suspend fun clearSession() {
        dataStore.clearAll()
    }

    @SuppressLint("HardwareIds")
    override fun getDeviceId(context: Context) {
        Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }
}