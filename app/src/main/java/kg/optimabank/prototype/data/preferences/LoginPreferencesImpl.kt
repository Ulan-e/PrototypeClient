package kg.optimabank.prototype.data.preferences

import android.content.Context
import kg.optimabank.prototype.common.extensions.FingerprintHardwareUtils
import kg.optimabank.prototype.dev.UserSettings
import kg.optimabank.prototype.features.main.StartScreen
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map

class LoginPreferencesImpl(
    context: Context,
    private val session: SessionPreferences,
    private val dataStore: DataStoreManager
) : LoginPreferences {

    companion object {
        const val MAX_TOUCH_TRIES = 3
    }

    private var isPinAuthComplete = false
    private var touchTryCount = MAX_TOUCH_TRIES

    private val touchUtils = FingerprintHardwareUtils(context)

    override suspend fun setStartScreen(screen: StartScreen) {
        dataStore.saveStartScreen(screen)
    }

    override fun getStartScreen(): Flow<StartScreen> {
        return dataStore.readStartScreen()
    }

    override suspend fun isPinNotSet(): Boolean {
        session.getPin().map { pin ->
            return@map pin == ""
        }
        return false
    }

    override suspend fun isUserDataNotSaved(): Flow<UserSettings?> {
        return session.getUserPreferences()
    }

    override suspend fun cleanPin() {
        session.cleanPin()
    }

    override suspend fun checkPin(
        pinForCheck: String,
        onSuccess: () -> Unit,
        onError: () -> Unit
    ) {
        session.getPin().collect { pin ->
            if (pin == pinForCheck) {
                touchTryCount = MAX_TOUCH_TRIES
                isPinAuthComplete = true
                onSuccess.invoke()
                return@collect
            } else {
                onError.invoke()
                isPinAuthComplete = false
                return@collect
            }
        }
    }

    override suspend fun setNewPin(pin: String) {
        session.savePin(pin)
        isPinAuthComplete = true
    }

    override fun waitTouch(
        onTouchAccepted: () -> Unit,
        onError: (disableTouch: Boolean) -> Unit,
        onFail: () -> Unit
    ) {
        if (!touchUtils.isTouchAvailable()) return
        touchUtils.stopListenTouch()
        touchUtils.startListenTouch(
            onTouchSuccess = {
                isPinAuthComplete = true
                onTouchAccepted()
            },
            onTouchFailed = {
                touchTryCount -= 1

                if (touchTryCount < 1) {
                    // session.setTouchBlocked(true)
                    onError(true)
                } else {
                    isPinAuthComplete = false
                    onFail()
                }
            },
            onTouchError = {
                onError(false)
            }
        )
    }

    override fun stopWaitTouch() {
        touchUtils.stopListenTouch()
    }

    override suspend fun setTouchLogin(isTouchLogin: Boolean) {
        session.setTouchLoginEnabled(isTouchLogin)
    }

    override fun isTouchLogin(): Flow<Boolean> {
        return session.isTouchLoginEnabled()
    }

    override fun isTouchHardwareAvailable(): Boolean {
        return touchUtils.isTouchAvailable()
    }

    override fun resetPinAuth() {
        isPinAuthComplete = false
    }

    override fun needPinAuth(): Boolean {
        return !isPinAuthComplete
    }

    override suspend fun logout() {
        session.clearSession()
    }
}