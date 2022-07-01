package kg.optimabank.prototype.features.auth.touchEnter

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

class TouchDialogViewModel @Inject constructor(
    private var loginPrefs: LoginPreferences,
    private var session: SessionPreferences,
    private var authAction: AuthActionImpl
) : BaseViewModel() {

    private val _successEnter = MutableStateFlow(false)
    val successEnter: StateFlow<Boolean> get() = _successEnter.asStateFlow()

    private val _touchState = MutableStateFlow(TouchType.NORMAL)
    val touchType: StateFlow<TouchType> get() = _touchState.asStateFlow()

    private var userSettings: UserSettings? = null

    fun startWaitTouch() {
        loginPrefs.waitTouch(
            onError = { isError ->
                if (isError)
                    _touchState.value = TouchType.CLOSED
                else
                    _touchState.value = TouchType.EMPTY_ATTEMPT

                stopWaitFingerprint()
            },
            onFail = {
                _touchState.value = TouchType.WRONG_FINGER
            },
            onTouchAccepted = {
                _touchState.value = TouchType.CLOSED
                _successEnter.value = true
                stopWaitFingerprint()
            }
        )
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

    private fun getUserData() {
        viewModelScope.launch(Dispatchers.IO) {
            session.getUserPreferences()
                .collect { userSettings ->
                    this@TouchDialogViewModel.userSettings = userSettings
                }
        }
    }

    fun useTouchId(isTouch: Boolean) {
        viewModelScope.launch {
            loginPrefs.setTouchLogin(isTouch)
            _successEnter.value = true
        }
    }

    fun stopWaitFingerprint() {
        loginPrefs.stopWaitTouch()
    }
}