package kg.optimabank.prototype.data.network.repository

import kg.optimabank.prototype.data.network.api.AuthorizationApi
import kg.optimabank.prototype.features.auth.data.AuthInfo
import kg.optimabank.prototype.features.auth.data.AuthInfoMapper
import javax.inject.Inject

interface AuthAction {

    suspend fun login(
        clientId: String?,
        username: String?,
        password: String?
    ): AuthInfo
}

class AuthActionImpl @Inject constructor(
    private val api: AuthorizationApi
) : AuthAction {

    companion object {
        const val TYPE_PASSWORD = "password"
    }

    override suspend fun login(clientId: String?, username: String?, password: String?): AuthInfo {
        val auth = api.login(clientId, username, password, TYPE_PASSWORD)
        val authInfoMapper = AuthInfoMapper()
        return authInfoMapper.dtoToVo(auth)
    }
}