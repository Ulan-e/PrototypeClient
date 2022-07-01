package kg.optimabank.prototype.data.preferences

import kg.optimabank.prototype.dev.UserSettings
import kg.optimabank.prototype.features.main.StartScreen
import kotlinx.coroutines.flow.Flow

interface LoginPreferences {

    /**
     * назначаем стартовый экран
     */
    suspend fun setStartScreen(screen: StartScreen)

    /**
     * получаем какой экран будет стартовым
     */
    fun getStartScreen(): Flow<StartScreen>

    /**
     * проверяем существует ли пин
     */
    suspend fun isPinNotSet(): Boolean

    /**
     * проверяем не пусты ли данные пользователя
     */
    suspend fun isUserDataNotSaved(): Flow<UserSettings?>

    /**
     * стираем старый пин
     */
    suspend fun cleanPin()

    /**
     * проверяем pinForCheck овпадают ли пин с пином из хранилища
     */
    suspend fun checkPin(
        pinForCheck: String,
        onSuccess: () -> Unit,
        onError: () -> Unit
    )

    /**
     * ставим новы пин
     */
    suspend fun setNewPin(pin: String)

    /**
     * офидаем прикосновения отпечатки пальцев
     * onTouchAccepted если отпечатки пальцев совподают
     * onError если что-то пошло не так либо превысили количество попыток
     * onFail если отпечатки пальцев не совпадают так как нет его в хранилище
     */
    fun waitTouch(
        onTouchAccepted: () -> Unit,
        onError: (disableTouch: Boolean) -> Unit,
        onFail: () -> Unit
    )

    /**
     * прекращаем офидание отпечатки пальцев
     */
    fun stopWaitTouch()

    /**
     * вход по отпечатку пальцев в приложение
     */
    suspend fun setTouchLogin(isTouchLogin: Boolean)

    /**
     * разрешен ли вход по отпечатку пальцев в приложение
     */
    fun isTouchLogin(): Flow<Boolean>

    /**
     * доступен ли отпечатки пальца на устройстве
     */
    fun isTouchHardwareAvailable(): Boolean

    /**
     * сбрасываем вход по пину
     */
    fun resetPinAuth()

    /**
     * если вход по пин коду
     */
    fun needPinAuth(): Boolean

    suspend fun logout()
}