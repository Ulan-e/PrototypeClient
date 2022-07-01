package kg.optimabank.prototype.features.auth.enterPin

import androidx.lifecycle.viewModelScope
import kg.optimabank.prototype.base.BaseViewModel
import kg.optimabank.prototype.data.network.api.resourse.Resource
import kg.optimabank.prototype.data.network.repository.AuthActionImpl
import kg.optimabank.prototype.data.preferences.LoginPreferences
import kg.optimabank.prototype.data.preferences.SessionPreferences
import kg.optimabank.prototype.dev.UserSettings
import kg.optimabank.prototype.features.auth.data.AuthInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class EnterPinViewModel @Inject constructor(
    private var loginPrefs: LoginPreferences,
    private var session: SessionPreferences,
    private var authAction: AuthActionImpl
) : BaseViewModel() {

    private var pinTryCount = 3

    private val _pinAttempts = MutableStateFlow(3)
    val pinAttempts: StateFlow<Int> get() = _pinAttempts.asStateFlow()

    private val _entryType = MutableStateFlow(EntryType.PIN)
    val entryType: StateFlow<EntryType> get() = _entryType.asStateFlow()

    private val _successLogin = MutableStateFlow(false)
    val successLogin: StateFlow<Boolean> get() = _successLogin.asStateFlow()

    init {
        checkTouchLogin()
    }

    private fun checkTouchLogin() {
        viewModelScope.launch {
            loginPrefs.isTouchLogin().collect { isEnabled ->
                if (isEnabled) _entryType.value = EntryType.TOUCH
                else _entryType.value = EntryType.PIN
            }
        }
    }

    suspend fun checkPin(pinForCheck: String) {
        loginPrefs.checkPin(
            pinForCheck = pinForCheck,
            onSuccess = {
                _successLogin.value = true
            },
            onError = {
                pinTryCount--
                _pinAttempts.value = pinTryCount
            })
    }

    fun isTouchBtnVisible() = flow {
        emit(loginPrefs.isTouchHardwareAvailable())
    }

    fun doLogin() = flow {
        val userSettings = runBlocking {
            session.getUserPreferences().first()
        }
        emit(Resource.loading())
        try {
            emit(
                Resource.success(
                    data = authAction.login(
                        clientId = userSettings?.clientId,
                        username = userSettings?.username,
                        password = userSettings?.password
                    )
                )
            )
        } catch (exception: Exception) {
            emit(Resource.error(message = exception.localizedMessage ?: "Error occurred"))
        }
    }.flowOn(Dispatchers.IO)

    fun saveUserData(response: AuthInfo?) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = runBlocking {
                session.getUserPreferences().first()
            }
            session.setUserPreferences(
                UserSettings.newBuilder()
                    .setClientId(user?.clientId)
                    .setUsername(user?.username)
                    .setPassword(user?.password)
                    .setAccessToken(response?.accessToken)
                    .setRefreshToken(response?.refreshToken)
                    .build()
            )
        }
    }

    fun logout() {
        viewModelScope.launch {
            loginPrefs.logout()
        }
    }
}