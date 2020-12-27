package com.twoIlya.android.lonelyboardgamer.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.twoIlya.android.lonelyboardgamer.dataClasses.ServerError
import com.twoIlya.android.lonelyboardgamer.dataClasses.Token
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

class ServerRepositoryTest {

    private lateinit var server: MockWebServer
    private var repo = ServerRepository

    //region

    @Before
    fun init() {
        server = MockWebServer()
        server.start()

        val url = server.url("").toUrl().toString()
        val repo = ServerRepository
        repo.setURL(url)
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    //endregion

    //region Login tests

    @Test
    fun `Login - Token received when the server returned status 0`() {
        val body = getBodyFromJson("login_0_status_response")

        server.enqueue(
            MockResponse().setBody(body)
        )

        val a = repo.login(Token(""))
        val result = a.getOrAwaitValue()

        assert(result is Token)
    }

    @Test
    fun `Login - ServerError with SERIALIZATION type received when the server returns an incorrect message`() {
        val body = getBodyFromJson("login_0_status_response_with_incorrect_message")

        server.enqueue(MockResponse().setBody(body))

        val a = repo.login(Token(""))
        val result = a.getOrAwaitValue()

        assert(result is ServerError && result.code == ServerError.Type.SERIALIZATION)
    }

    @Test
    fun `Login - ServerError with AUTHORIZATION type received when the server returned status 1`() {
        val body = getBodyFromJson("1_status_response")

        server.enqueue(
            MockResponse().setBody(body)
        )

        val a = repo.login(Token(""))
        val result = a.getOrAwaitValue()

        assert(result is ServerError && result.code == ServerError.Type.AUTHORIZATION)
    }

    @Test
    fun `Login - ServerError with SOME_INFO_MISSING type received when the server returned status 2`() {
        val body = getBodyFromJson("2_status_response")

        server.enqueue(
            MockResponse().setBody(body)
        )

        val a = repo.login(Token(""))
        val result = a.getOrAwaitValue()

        assert(result is ServerError && result.code == ServerError.Type.SOME_INFO_MISSING)
    }

    @Test
    fun `Login - ServerError with ELEMENT_WAS_NOT_FOUND type received when the server returned status 3`() {
        val body = getBodyFromJson("3_status_response")

        server.enqueue(
            MockResponse().setBody(body)
        )

        val a = repo.login(Token(""))
        val result = a.getOrAwaitValue()

        assert(result is ServerError && result.code == ServerError.Type.ELEMENT_WAS_NOT_FOUND)
    }

    @Test
    fun `Login - ServerError with WRONG_DATA_FORMAT type received when the server returned status 4`() {
        val body = getBodyFromJson("4_status_response")

        server.enqueue(
            MockResponse().setBody(body)
        )

        val a = repo.login(Token(""))
        val result = a.getOrAwaitValue()

        assert(result is ServerError && result.code == ServerError.Type.WRONG_DATA_FORMAT)
    }

    @Test
    fun `Login - ServerError with BAD_DATA type received when the server returned status 5`() {
        val body = getBodyFromJson("5_status_response")

        server.enqueue(
            MockResponse().setBody(body)
        )

        val a = repo.login(Token(""))
        val result = a.getOrAwaitValue()

        assert(result is ServerError && result.code == ServerError.Type.BAD_DATA)
    }

    @Test
    fun `Login - ServerError with UNKNOWN type received when the server returned unknown status`() {
        val body = getBodyFromJson("unknown_status_response")

        server.enqueue(
            MockResponse().setBody(body)
        )

        val a = repo.login(Token(""))
        val result = a.getOrAwaitValue()

        assert(result is ServerError && result.code == ServerError.Type.UNKNOWN)
    }

    @Test
    fun `Login - ServerError with NETWORK type received when the server is not available`() {
        // Retrofit ждёт ответ всего 1 секунду
        server.enqueue(MockResponse().setBodyDelay(2, TimeUnit.SECONDS))

        val a = repo.login(Token(""))
        val result = a.getOrAwaitValue()

        assert(result is ServerError && result.code == ServerError.Type.NETWORK)
    }

    @Test
    fun `Login - ServerError with HTTP_401 type received when the server returned 401`() {
        server.enqueue(MockResponse().setResponseCode(401))

        val a = repo.login(Token(""))
        val result = a.getOrAwaitValue()

        assert(result is ServerError && result.code == ServerError.Type.HTTP_401)
    }

    @Test
    fun `Login - ServerError with UNKNOWN type received when the server returns an unsuccessful code`() {
        server.enqueue(MockResponse().setResponseCode(404))

        val a = repo.login(Token(""))
        val result = a.getOrAwaitValue()

        assert(result is ServerError && result.code == ServerError.Type.UNKNOWN)
    }

    //endregion

    //region Registration test

    @Test
    fun `Registration - Token received when the server returned status 0`() {
        // val body = getBodyFromJson("login_0_status_response")

        /*server.enqueue(
            MockResponse().setBody(body)
        )*/

        val a = repo.login(Token(""))
        val result = a.getOrAwaitValue()

        // assert(result is Token)

        assert(false)
    }

    //endregion

    //region Utils

    private fun getBodyFromJson(fileName: String): String {
        return javaClass.classLoader?.getResource("repository/ServerRepositoryTest/$fileName.json")
            ?.readText() ?: ""
    }

    private fun <T> LiveData<T>.getOrAwaitValue(
        time: Long = 30,
        timeUnit: TimeUnit = TimeUnit.SECONDS
    ): T {
        var data: T? = null
        val latch = CountDownLatch(1)
        val observer = object : Observer<T> {
            override fun onChanged(o: T?) {
                data = o
                latch.countDown()
                this@getOrAwaitValue.removeObserver(this)
            }
        }

        this.observeForever(observer)

        // Don't wait indefinitely if the LiveData is not set.
        if (!latch.await(time, timeUnit)) {
            throw TimeoutException("LiveData value was never set.")
        }

        @Suppress("UNCHECKED_CAST")
        return data as T
    }

    //endregion
}