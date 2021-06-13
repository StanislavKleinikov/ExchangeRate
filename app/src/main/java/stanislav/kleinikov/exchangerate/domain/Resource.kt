package stanislav.kleinikov.exchangerate.domain

import java.util.concurrent.atomic.AtomicBoolean

sealed class Resource<T> {

    class Loading<T : Any> : Resource<T>()

    class Success<T : Any>(val data: T) : Resource<T>()

    class Error<T : Any>(private val cause: Throwable) : Resource<T>(), Event<Throwable> {
        private val isHandled = AtomicBoolean(false)
        override fun handleEvent(action: (Throwable) -> Unit) {
            if (isHandled.compareAndSet(false, true)) {
                action.invoke(cause)
            }
        }
    }
}

interface Event<T> {
    fun handleEvent(action: (data: T) -> Unit)
}
