package kg.optimabank.prototype.data.network.api.resourse

data class Resource<out T>(val status: Status, val data: T?, val message: String?) {
    companion object {
        fun <T> loading(): Resource<T> =
            Resource(status = Status.LOADING, data = null, message = null)

        fun <T> success(data: T): Resource<T> =
            Resource(status = Status.SUCCESS, data = data, message = null)

        fun <T> error(message: String): Resource<T> =
            Resource(status = Status.ERROR, data = null, message = message)
    }
}

enum class Status {
    SUCCESS,
    ERROR,
    LOADING
}