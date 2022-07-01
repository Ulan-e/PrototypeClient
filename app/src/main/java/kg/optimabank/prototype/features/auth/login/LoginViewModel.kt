package kg.optimabank.prototype.features.auth.login

import androidx.lifecycle.viewModelScope
import kg.optimabank.prototype.base.BaseViewModel
import kg.optimabank.prototype.data.network.api.resourse.Resource
import kg.optimabank.prototype.data.network.repository.AuthActionImpl
import kg.optimabank.prototype.data.preferences.SessionPreferences
import kg.optimabank.prototype.dev.UserSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

class LoginViewModel @Inject constructor(
    private val authAction: AuthActionImpl
) : BaseViewModel() {

    @Inject
    lateinit var sessionPreferences: SessionPreferences

    fun doLogin(
        clientId: String,
        username: String,
        password: String
    ) = flow {
        emit(Resource.loading())
        try {
            emit(Resource.success(data = authAction.login(clientId, username, password)))

        } catch (exception: Exception) {
            emit(Resource.error(message = exception.localizedMessage ?: "Error occurred"))
        }
    }

    fun saveUserData(
        username: String,
        password: String,
        clientId: String,
        accessToken: String?,
        refreshToken: String?
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            sessionPreferences.setUserPreferences(
                UserSettings.newBuilder()
                    .setUsername(username)
                    .setPassword(password)
                    .setClientId(clientId)
                    .setAccessToken(accessToken)
                    .setRefreshToken(refreshToken)
                    .build()
            )
        }
    }
}