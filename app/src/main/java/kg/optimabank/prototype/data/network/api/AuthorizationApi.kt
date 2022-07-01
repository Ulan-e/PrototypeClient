package kg.optimabank.prototype.data.network.api

import kg.optimabank.prototype.features.auth.data.AuthInfoDto
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.create
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Headers
import retrofit2.http.POST

interface AuthorizationApi {

    @FormUrlEncoded
    @Headers("$CUSTOM_HEADER: $NO_AUTH")
    @POST(AUTH_TOKEN)
    suspend fun login(
        @Field("client_id") clientId: String?,
        @Field("username") username: String?,
        @Field("password") password: String?,
        @Field("grant_type") grantType: String?
    ): AuthInfoDto

    @FormUrlEncoded
    @Headers("$CUSTOM_HEADER: $NO_AUTH")
    @POST(AUTH_TOKEN)
    suspend fun refreshToken(
        @Field("client_id") clientId: String?,
        @Field("grant_type") grantType: String?,
        @Field("refresh_token") refreshToken: String?
    ): Response<AuthInfoDto>

    companion object Factory {
        const val AUTH_TOKEN = "auth/realms/crm-test/protocol/openid-connect/token"

        const val CUSTOM_HEADER = "@"
        const val NO_AUTH = "NoAuth"

        operator fun invoke(retrofit: Retrofit): AuthorizationApi = retrofit.create()
    }

}