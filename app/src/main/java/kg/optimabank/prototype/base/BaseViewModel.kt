package kg.optimabank.prototype.base

import androidx.lifecycle.ViewModel
import kg.optimabank.prototype.common.dialogs.DialogError
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class BaseViewModel : ViewModel() {

    companion object {
        const val errorTitle = "Ошибка"
        const val errorMessage = "Попробуйте запрос позднее"
    }

    val dialogMessage = MutableSharedFlow<DialogError>(replay = 1, extraBufferCapacity = 0, BufferOverflow.DROP_OLDEST)
    val authError = MutableSharedFlow<DialogError>(replay = 1, extraBufferCapacity = 0, BufferOverflow.DROP_OLDEST)

    private val _loadingState = MutableStateFlow(false)

    val loadingState: StateFlow<Boolean> get() = _loadingState.asStateFlow()

    fun makeDialogError(error: Throwable?): DialogError {
        return DialogError(
            title = errorTitle,
            message = error?.localizedMessage ?: errorMessage
        )
    }
}