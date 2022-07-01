package kg.optimabank.prototype.common.extensions

import androidx.lifecycle.LifecycleCoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect

inline fun <T> Flow<T>.collectWhenStarted(
    lifecycleScope: LifecycleCoroutineScope,
    crossinline action: suspend (value: T) -> Unit
) {
    lifecycleScope.launchWhenStarted {
        this@collectWhenStarted.collect {
            action.invoke(it)
        }
    }
}