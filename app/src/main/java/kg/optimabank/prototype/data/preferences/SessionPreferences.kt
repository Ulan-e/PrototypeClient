package kg.optimabank.prototype.data.preferences

import android.content.Context
import kg.optimabank.prototype.dev.UserSettings
import kotlinx.coroutines.flow.Flow

interface SessionPreferences {

    fun getPin(): Flow<String?>
    suspend fun savePin(pin: String)
    suspend fun cleanPin()

    fun isTouchLoginEnabled(): Flow<Boolean>
    suspend fun setTouchLoginEnabled(isEnabled: Boolean)

    suspend fun setUserPreferences(userSettings: UserSettings?)
    fun getUserPreferences(): Flow<UserSettings?>

    fun getDeviceId(context: Context)

    suspend fun clearSession()
}