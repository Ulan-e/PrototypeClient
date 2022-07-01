package kg.optimabank.prototype.features.auth.data

import com.google.gson.annotations.SerializedName

data class AuthInfoDto(
    @SerializedName("access_token")
    val accessToken: String,
    @SerializedName("expires_in")
    val expiresIn: Long,
    @SerializedName("refresh_token")
    val refreshToken: String,
    @SerializedName("refresh_expires_in")
    val refreshExpiresIn: Long,
    @SerializedName("token_type")
    val tokenType: String,
    @SerializedName("not-before-policy")
    val notBeforePolicy: Int,
    @SerializedName("session_state")
    val sessionState: String,
    @SerializedName("scope")
    val scope: String
)