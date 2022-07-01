package kg.optimabank.prototype.features.auth.createPin

import androidx.lifecycle.viewModelScope
import kg.optimabank.prototype.base.BaseViewModel
import kg.optimabank.prototype.data.preferences.LoginPreferences
import kg.optimabank.prototype.features.main.StartScreen
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

class CreatePinViewModel @Inject constructor(
    private var loginPrefs: LoginPreferences
) : BaseViewModel() {

    private val _repeatPin = MutableStateFlow(CreatePinType.START)
    val repeatPin: StateFlow<CreatePinType> get() = _repeatPin.asStateFlow()

    val pinAttempts = MutableSharedFlow<Int>(replay = 1, extraBufferCapacity = 0, BufferOverflow.DROP_OLDEST)

    private var pinTryCount = 3
    private var pinCode = ""

    fun isTouchBtnVisible() = flow {
        emit(loginPrefs.isTouchHardwareAvailable())
    }

    fun setPin(pin: String) {
        if (pinCode.length > 4) return

        // вводим первый раз пин
        if (pinCode.isBlank()) {
            pinCode = pin
            _repeatPin.value = CreatePinType.CONFIRM
            return
        }

        // повторный ввод пина
        if (pin == pinCode) {
            viewModelScope.launch {
                _repeatPin.value = CreatePinType.SUCCESS
                loginPrefs.setNewPin(pin)
                loginPrefs.setStartScreen(StartScreen.ENTER_PIN)
            }

        } else {
            pinTryCount--
            pinAttempts.tryEmit(pinTryCount)

            viewModelScope.launch {
                if (pinTryCount == 0) {
                    _repeatPin.value = CreatePinType.WRONG
                    loginPrefs.setStartScreen(StartScreen.LOGIN)
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            loginPrefs.logout()
        }
    }
}