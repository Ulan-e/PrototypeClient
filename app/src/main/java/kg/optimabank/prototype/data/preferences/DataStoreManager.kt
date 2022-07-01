package kg.optimabank.prototype.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kg.optimabank.prototype.data.preferences.proto.SettingsSerializer.settingsDataStore
import kg.optimabank.prototype.dev.UserSettings
import kg.optimabank.prototype.features.main.StartScreen
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import timber.log.Timber

class DataStoreManager(private val context: Context) {

    companion object {
        const val PREFERENCES = "settings_1"
        val START_SCREEN = intPreferencesKey("screen_start")
    }

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = PREFERENCES)

    suspend fun saveString(key: String, value: String) {
        val stringKey = stringPreferencesKey(key)
        context.dataStore.edit { settings ->
            settings[stringKey] = value
        }
    }

    fun readString(key: String): Flow<String> {
        val stringKey = stringPreferencesKey(key)
        return context.dataStore.data
            .catch { Timber.e(it.localizedMessage) }
            .map { preferences ->
                val name = preferences[stringKey] ?: ""
                name
            }
    }

    suspend fun saveBoolean(key: String, value: Boolean) {
        val booleanKey = booleanPreferencesKey(key)
        context.dataStore.edit { settings ->
            settings[booleanKey] = value
        }
    }

    fun readBoolean(key: String): Flow<Boolean> {
        val booleanKey = booleanPreferencesKey(key)
        return context.dataStore.data
            .catch { Timber.e(it.localizedMessage) }
            .map { preferences ->
                val result = preferences[booleanKey] ?: false
                result
            }
    }

    suspend fun saveStartScreen(startScreen: StartScreen) {
        context.dataStore.edit { settings ->
            settings[START_SCREEN] = when (startScreen) {
                StartScreen.LOGIN -> 1
                StartScreen.ENTER_PIN -> 2
            }
        }
    }

    fun readStartScreen(): Flow<StartScreen> = context.dataStore.data
        .catch { Timber.e(it.localizedMessage) }
        .map { preferences ->
            val screen = preferences[START_SCREEN] ?: 1

            when (screen) {
                1 -> StartScreen.LOGIN
                2 -> StartScreen.ENTER_PIN
                else -> StartScreen.LOGIN
            }
        }

    suspend fun saveUserSettings(userSettings: UserSettings) {
        context.settingsDataStore.updateData {
            userSettings
        }
    }

    fun getUserSettings(): Flow<UserSettings> {
        return context.settingsDataStore.data
    }

    suspend fun clearAll() {
        context.apply {
            dataStore.edit { data ->
                data.clear()
            }
            settingsDataStore.updateData { userSettings ->
                userSettings
                    .toBuilder()
                    .clear()
                    .build()
            }
        }
    }
}