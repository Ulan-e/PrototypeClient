package kg.optimabank.prototype.features.auth.data

class AuthInfoMapper {

    fun dtoToVo(dto: AuthInfoDto): AuthInfo {
        return AuthInfo(
            accessToken = dto.accessToken,
            refreshToken = dto.refreshToken,
            expiresIn = dto.expiresIn,
            refreshExpiresIn = dto.refreshExpiresIn,
            tokenType = dto.tokenType,
            notBeforePolicy = dto.notBeforePolicy,
            sessionState = dto.sessionState,
            scope = dto.scope
        )
    }
}