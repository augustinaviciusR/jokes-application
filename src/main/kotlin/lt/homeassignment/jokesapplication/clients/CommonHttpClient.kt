package lt.homeassignment.jokesapplication.clients

interface CommonHttpClient {
    fun <T> executeRequest(url: String, responseType: Class<T>): T
}
