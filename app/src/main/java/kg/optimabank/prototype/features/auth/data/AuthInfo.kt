package kg.optimabank.prototype.features.auth.data

data class AuthInfo(
    val accessToken: String?,
    val expiresIn: Long,
    val refreshToken: String?,
    val refreshExpiresIn: Long,
    val tokenType: String?,
    val notBeforePolicy: Int,
    val sessionState: String?,
    val scope: String?
)