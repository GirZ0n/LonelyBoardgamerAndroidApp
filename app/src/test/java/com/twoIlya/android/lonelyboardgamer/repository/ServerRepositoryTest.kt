package com.twoIlya.android.lonelyboardgamer.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.twoIlya.android.lonelyboardgamer.dataClasses.MyProfile
import com.twoIlya.android.lonelyboardgamer.dataClasses.ServerError
import com.twoIlya.android.lonelyboardgamer.dataClasses.ServerMessage
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

    //region login tests

    @Test
    fun `login - Token received when the server returned status 0`() {
        val body = getBodyFromJson("login_0_status_response")

        server.enqueue(
            MockResponse().setBody(body)
        )

        val liveData = repo.login(Token(""))
        val result = liveData.getOrAwaitValue()

        assert(result is Token)
    }

    @Test
    fun `login - ServerError with AUTHORIZATION type received when the server returned status 1`() {
        val body = getBodyFromJson("1_status_response")

        server.enqueue(
            MockResponse().setBody(body)
        )

        val liveData = repo.login(Token(""))
        val result = liveData.getOrAwaitValue()

        assert(result is ServerError && result.code == ServerError.Type.AUTHORIZATION)
    }

    @Test
    fun `login - ServerError with SOME_INFO_MISSING type received when the server returned status 2`() {
        val body = getBodyFromJson("2_status_response")

        server.enqueue(
            MockResponse().setBody(body)
        )

        val liveData = repo.login(Token(""))
        val result = liveData.getOrAwaitValue()

        assert(result is ServerError && result.code == ServerError.Type.SOME_INFO_MISSING)
    }

    @Test
    fun `login - ServerError with ELEMENT_WAS_NOT_FOUND type received when the server returned status 3`() {
        val body = getBodyFromJson("3_status_response")

        server.enqueue(
            MockResponse().setBody(body)
        )

        val liveData = repo.login(Token(""))
        val result = liveData.getOrAwaitValue()

        assert(result is ServerError && result.code == ServerError.Type.ELEMENT_WAS_NOT_FOUND)
    }

    @Test
    fun `login - ServerError with WRONG_DATA_FORMAT type received when the server returned status 4`() {
        val body = getBodyFromJson("4_status_response")

        server.enqueue(
            MockResponse().setBody(body)
        )

        val liveData = repo.login(Token(""))
        val result = liveData.getOrAwaitValue()

        assert(result is ServerError && result.code == ServerError.Type.WRONG_DATA_FORMAT)
    }

    @Test
    fun `login - ServerError with BAD_DATA type received when the server returned status 5`() {
        val body = getBodyFromJson("5_status_response")

        server.enqueue(
            MockResponse().setBody(body)
        )

        val liveData = repo.login(Token(""))
        val result = liveData.getOrAwaitValue()

        assert(result is ServerError && result.code == ServerError.Type.BAD_DATA)
    }

    @Test
    fun `login - ServerError with UNKNOWN type received when the server returned unknown status`() {
        val body = getBodyFromJson("unknown_status_response")

        server.enqueue(
            MockResponse().setBody(body)
        )

        val liveData = repo.login(Token(""))
        val result = liveData.getOrAwaitValue()

        assert(result is ServerError && result.code == ServerError.Type.UNKNOWN)
    }

    @Test
    fun `login - ServerError with NETWORK type received when the server is not available`() {
        // Retrofit ждёт ответ всего 1 секунду
        server.enqueue(MockResponse().setBodyDelay(2, TimeUnit.SECONDS))

        val liveData = repo.login(Token(""))
        val result = liveData.getOrAwaitValue()

        assert(result is ServerError && result.code == ServerError.Type.NETWORK)
    }

    @Test
    fun `login - ServerError with HTTP_401 type received when the server returned 401`() {
        server.enqueue(MockResponse().setResponseCode(401))

        val liveData = repo.login(Token(""))
        val result = liveData.getOrAwaitValue()

        assert(result is ServerError && result.code == ServerError.Type.HTTP_401)
    }

    @Test
    fun `login - ServerError with UNKNOWN type received when the server returns an unsuccessful code`() {
        server.enqueue(MockResponse().setResponseCode(404))

        val liveData = repo.login(Token(""))
        val result = liveData.getOrAwaitValue()

        assert(result is ServerError && result.code == ServerError.Type.UNKNOWN)
    }

    //endregion

    //region register tests

    @Test
    fun `register - Token received when the server returned status 0`() {
        val body = getBodyFromJson("register_0_status_response")

        server.enqueue(
            MockResponse().setBody(body)
        )

        val liveData = repo.register(Token(""), "", emptyList(), emptyList(), "")
        val result = liveData.getOrAwaitValue()

        assert(result is Token)
    }

    @Test
    fun `register - ServerError with AUTHORIZATION type received when the server returned status 1`() {
        val body = getBodyFromJson("1_status_response")

        server.enqueue(
            MockResponse().setBody(body)
        )

        val liveData = repo.register(Token(""), "", emptyList(), emptyList(), "")
        val result = liveData.getOrAwaitValue()

        assert(result is ServerError && result.code == ServerError.Type.AUTHORIZATION)
    }

    @Test
    fun `register - ServerError with SOME_INFO_MISSING type received when the server returned status 2`() {
        val body = getBodyFromJson("2_status_response")

        server.enqueue(
            MockResponse().setBody(body)
        )

        val liveData = repo.register(Token(""), "", emptyList(), emptyList(), "")
        val result = liveData.getOrAwaitValue()

        assert(result is ServerError && result.code == ServerError.Type.SOME_INFO_MISSING)
    }

    @Test
    fun `register - ServerError with ELEMENT_WAS_NOT_FOUND type received when the server returned status 3`() {
        val body = getBodyFromJson("3_status_response")

        server.enqueue(
            MockResponse().setBody(body)
        )

        val liveData = repo.register(Token(""), "", emptyList(), emptyList(), "")
        val result = liveData.getOrAwaitValue()

        assert(result is ServerError && result.code == ServerError.Type.ELEMENT_WAS_NOT_FOUND)
    }

    @Test
    fun `register - ServerError with WRONG_DATA_FORMAT type received when the server returned status 4`() {
        val body = getBodyFromJson("4_status_response")

        server.enqueue(
            MockResponse().setBody(body)
        )

        val liveData = repo.register(Token(""), "", emptyList(), emptyList(), "")
        val result = liveData.getOrAwaitValue()

        assert(result is ServerError && result.code == ServerError.Type.WRONG_DATA_FORMAT)
    }

    @Test
    fun `register - ServerError with BAD_DATA type received when the server returned status 5`() {
        val body = getBodyFromJson("5_status_response")

        server.enqueue(
            MockResponse().setBody(body)
        )

        val liveData = repo.register(Token(""), "", emptyList(), emptyList(), "")
        val result = liveData.getOrAwaitValue()

        assert(result is ServerError && result.code == ServerError.Type.BAD_DATA)
    }

    @Test
    fun `register - ServerError with UNKNOWN type received when the server returned unknown status`() {
        val body = getBodyFromJson("unknown_status_response")

        server.enqueue(
            MockResponse().setBody(body)
        )

        val liveData = repo.register(Token(""), "", emptyList(), emptyList(), "")
        val result = liveData.getOrAwaitValue()

        assert(result is ServerError && result.code == ServerError.Type.UNKNOWN)
    }

    @Test
    fun `register - ServerError with NETWORK type received when the server is not available`() {
        // Retrofit ждёт ответ всего 1 секунду
        server.enqueue(MockResponse().setBodyDelay(2, TimeUnit.SECONDS))

        val liveData = repo.register(Token(""), "", emptyList(), emptyList(), "")
        val result = liveData.getOrAwaitValue()

        assert(result is ServerError && result.code == ServerError.Type.NETWORK)
    }

    @Test
    fun `register - ServerError with HTTP_401 type received when the server returned 401`() {
        server.enqueue(MockResponse().setResponseCode(401))

        val liveData = repo.register(Token(""), "", emptyList(), emptyList(), "")
        val result = liveData.getOrAwaitValue()

        assert(result is ServerError && result.code == ServerError.Type.HTTP_401)
    }

    @Test
    fun `register - ServerError with UNKNOWN type received when the server returns an unsuccessful code`() {
        server.enqueue(MockResponse().setResponseCode(404))

        val liveData = repo.register(Token(""), "", emptyList(), emptyList(), "")
        val result = liveData.getOrAwaitValue()

        assert(result is ServerError && result.code == ServerError.Type.UNKNOWN)
    }

    //endregion

    //region getProfile tests

    @Test
    fun `getProfile - Token received when the server returned status 0`() {
        val body = getBodyFromJson("get_profile_0_status_response")

        server.enqueue(
            MockResponse().setBody(body)
        )

        val liveData = repo.getProfile(Token(""))
        val result = liveData.getOrAwaitValue()

        val isCorrectProfile =
            result is MyProfile &&
                    result.firstName == "Иван" &&
                    result.secondName == "Иванов" &&
                    result.address == "ул. Садовая, 15, Елабуга, Татарстан Респ., Россия, 423602, Елабуга" &&
                    result.categories == listOf("Евро", "Абстрактные") &&
                    result.mechanics == listOf("Удача", "Блеф") &&
                    result.description == "I love D&D"

        assert(isCorrectProfile)
    }

    @Test
    fun `getProfile - ServerError with AUTHORIZATION type received when the server returned status 1`() {
        val body = getBodyFromJson("1_status_response")

        server.enqueue(
            MockResponse().setBody(body)
        )

        val liveData = repo.getProfile(Token(""))
        val result = liveData.getOrAwaitValue()

        assert(result is ServerError && result.code == ServerError.Type.AUTHORIZATION)
    }

    @Test
    fun `getProfile - ServerError with SOME_INFO_MISSING type received when the server returned status 2`() {
        val body = getBodyFromJson("2_status_response")

        server.enqueue(
            MockResponse().setBody(body)
        )

        val liveData = repo.getProfile(Token(""))
        val result = liveData.getOrAwaitValue()

        assert(result is ServerError && result.code == ServerError.Type.SOME_INFO_MISSING)
    }

    @Test
    fun `getProfile - ServerError with ELEMENT_WAS_NOT_FOUND type received when the server returned status 3`() {
        val body = getBodyFromJson("3_status_response")

        server.enqueue(
            MockResponse().setBody(body)
        )

        val liveData = repo.getProfile(Token(""))
        val result = liveData.getOrAwaitValue()

        assert(result is ServerError && result.code == ServerError.Type.ELEMENT_WAS_NOT_FOUND)
    }

    @Test
    fun `getProfile - ServerError with WRONG_DATA_FORMAT type received when the server returned status 4`() {
        val body = getBodyFromJson("4_status_response")

        server.enqueue(
            MockResponse().setBody(body)
        )

        val liveData = repo.getProfile(Token(""))
        val result = liveData.getOrAwaitValue()

        assert(result is ServerError && result.code == ServerError.Type.WRONG_DATA_FORMAT)
    }

    @Test
    fun `getProfile - ServerError with BAD_DATA type received when the server returned status 5`() {
        val body = getBodyFromJson("5_status_response")

        server.enqueue(
            MockResponse().setBody(body)
        )

        val liveData = repo.getProfile(Token(""))
        val result = liveData.getOrAwaitValue()

        assert(result is ServerError && result.code == ServerError.Type.BAD_DATA)
    }

    @Test
    fun `getProfile - ServerError with UNKNOWN type received when the server returned unknown status`() {
        val body = getBodyFromJson("unknown_status_response")

        server.enqueue(
            MockResponse().setBody(body)
        )

        val liveData = repo.getProfile(Token(""))
        val result = liveData.getOrAwaitValue()

        assert(result is ServerError && result.code == ServerError.Type.UNKNOWN)
    }

    @Test
    fun `getProfile - ServerError with NETWORK type received when the server is not available`() {
        // Retrofit ждёт ответ всего 1 секунду
        server.enqueue(MockResponse().setBodyDelay(2, TimeUnit.SECONDS))

        val liveData = repo.getProfile(Token(""))
        val result = liveData.getOrAwaitValue()

        assert(result is ServerError && result.code == ServerError.Type.NETWORK)
    }

    @Test
    fun `getProfile - ServerError with HTTP_401 type received when the server returned 401`() {
        server.enqueue(MockResponse().setResponseCode(401))

        val liveData = repo.getProfile(Token(""))
        val result = liveData.getOrAwaitValue()

        assert(result is ServerError && result.code == ServerError.Type.HTTP_401)
    }

    @Test
    fun `getProfile - ServerError with UNKNOWN type received when the server returns an unsuccessful code`() {
        server.enqueue(MockResponse().setResponseCode(404))

        val liveData = repo.getProfile(Token(""))
        val result = liveData.getOrAwaitValue()

        assert(result is ServerError && result.code == ServerError.Type.UNKNOWN)
    }

    //endregion

    //region logout tests

    @Test
    fun `logout - Token received when the server returned status 0`() {
        val body = getBodyFromJson("logout_0_status_response")

        server.enqueue(
            MockResponse().setBody(body)
        )

        val liveData = repo.logout(Token(""))
        val result = liveData.getOrAwaitValue()

        println(result)

        assert(result is ServerMessage)
    }

    @Test
    fun `logout - ServerError with AUTHORIZATION type received when the server returned status 1`() {
        val body = getBodyFromJson("1_status_response")

        server.enqueue(
            MockResponse().setBody(body)
        )

        val liveData = repo.logout(Token(""))
        val result = liveData.getOrAwaitValue()

        assert(result is ServerError && result.code == ServerError.Type.AUTHORIZATION)
    }

    @Test
    fun `logout - ServerError with SOME_INFO_MISSING type received when the server returned status 2`() {
        val body = getBodyFromJson("2_status_response")

        server.enqueue(
            MockResponse().setBody(body)
        )

        val liveData = repo.logout(Token(""))
        val result = liveData.getOrAwaitValue()

        assert(result is ServerError && result.code == ServerError.Type.SOME_INFO_MISSING)
    }

    @Test
    fun `logout - ServerError with ELEMENT_WAS_NOT_FOUND type received when the server returned status 3`() {
        val body = getBodyFromJson("3_status_response")

        server.enqueue(
            MockResponse().setBody(body)
        )

        val liveData = repo.logout(Token(""))
        val result = liveData.getOrAwaitValue()

        assert(result is ServerError && result.code == ServerError.Type.ELEMENT_WAS_NOT_FOUND)
    }

    @Test
    fun `logout - ServerError with WRONG_DATA_FORMAT type received when the server returned status 4`() {
        val body = getBodyFromJson("4_status_response")

        server.enqueue(
            MockResponse().setBody(body)
        )

        val liveData = repo.logout(Token(""))
        val result = liveData.getOrAwaitValue()

        assert(result is ServerError && result.code == ServerError.Type.WRONG_DATA_FORMAT)
    }

    @Test
    fun `logout - ServerError with BAD_DATA type received when the server returned status 5`() {
        val body = getBodyFromJson("5_status_response")

        server.enqueue(
            MockResponse().setBody(body)
        )

        val liveData = repo.logout(Token(""))
        val result = liveData.getOrAwaitValue()

        assert(result is ServerError && result.code == ServerError.Type.BAD_DATA)
    }

    @Test
    fun `logout - ServerError with UNKNOWN type received when the server returned unknown status`() {
        val body = getBodyFromJson("unknown_status_response")

        server.enqueue(
            MockResponse().setBody(body)
        )

        val liveData = repo.logout(Token(""))
        val result = liveData.getOrAwaitValue()

        assert(result is ServerError && result.code == ServerError.Type.UNKNOWN)
    }

    @Test
    fun `logout - ServerError with NETWORK type received when the server is not available`() {
        // Retrofit ждёт ответ всего 1 секунду
        server.enqueue(MockResponse().setBodyDelay(2, TimeUnit.SECONDS))

        val liveData = repo.logout(Token(""))
        val result = liveData.getOrAwaitValue()

        assert(result is ServerError && result.code == ServerError.Type.NETWORK)
    }

    @Test
    fun `logout - ServerError with HTTP_401 type received when the server returned 401`() {
        server.enqueue(MockResponse().setResponseCode(401))

        val liveData = repo.logout(Token(""))
        val result = liveData.getOrAwaitValue()

        assert(result is ServerError && result.code == ServerError.Type.HTTP_401)
    }

    @Test
    fun `logout - ServerError with UNKNOWN type received when the server returns an unsuccessful code`() {
        server.enqueue(MockResponse().setResponseCode(404))

        val liveData = repo.logout(Token(""))
        val result = liveData.getOrAwaitValue()

        assert(result is ServerError && result.code == ServerError.Type.UNKNOWN)
    }

    //endregion

    //region changeAddress tests

    @Test
    fun `changeAddress - Token received when the server returned status 0`() {
        val body = getBodyFromJson("change_address_0_status_response")

        server.enqueue(
            MockResponse().setBody(body)
        )

        val liveData = repo.changeAddress(Token(""), "")
        val result = liveData.getOrAwaitValue()

        println(result)

        assert(result is ServerMessage)
    }

    @Test
    fun `changeAddress - ServerError with AUTHORIZATION type received when the server returned status 1`() {
        val body = getBodyFromJson("1_status_response")

        server.enqueue(
            MockResponse().setBody(body)
        )

        val liveData = repo.changeAddress(Token(""), "")
        val result = liveData.getOrAwaitValue()

        assert(result is ServerError && result.code == ServerError.Type.AUTHORIZATION)
    }

    @Test
    fun `changeAddress - ServerError with SOME_INFO_MISSING type received when the server returned status 2`() {
        val body = getBodyFromJson("2_status_response")

        server.enqueue(
            MockResponse().setBody(body)
        )

        val liveData = repo.changeAddress(Token(""), "")
        val result = liveData.getOrAwaitValue()

        assert(result is ServerError && result.code == ServerError.Type.SOME_INFO_MISSING)
    }

    @Test
    fun `changeAddress - ServerError with ELEMENT_WAS_NOT_FOUND type received when the server returned status 3`() {
        val body = getBodyFromJson("3_status_response")

        server.enqueue(
            MockResponse().setBody(body)
        )

        val liveData = repo.changeAddress(Token(""), "")
        val result = liveData.getOrAwaitValue()

        assert(result is ServerError && result.code == ServerError.Type.ELEMENT_WAS_NOT_FOUND)
    }

    @Test
    fun `changeAddress - ServerError with WRONG_DATA_FORMAT type received when the server returned status 4`() {
        val body = getBodyFromJson("4_status_response")

        server.enqueue(
            MockResponse().setBody(body)
        )

        val liveData = repo.changeAddress(Token(""), "")
        val result = liveData.getOrAwaitValue()

        assert(result is ServerError && result.code == ServerError.Type.WRONG_DATA_FORMAT)
    }

    @Test
    fun `changeAddress - ServerError with BAD_DATA type received when the server returned status 5`() {
        val body = getBodyFromJson("5_status_response")

        server.enqueue(
            MockResponse().setBody(body)
        )

        val liveData = repo.changeAddress(Token(""), "")
        val result = liveData.getOrAwaitValue()

        assert(result is ServerError && result.code == ServerError.Type.BAD_DATA)
    }

    @Test
    fun `changeAddress - ServerError with UNKNOWN type received when the server returned unknown status`() {
        val body = getBodyFromJson("unknown_status_response")

        server.enqueue(
            MockResponse().setBody(body)
        )

        val liveData = repo.changeAddress(Token(""), "")
        val result = liveData.getOrAwaitValue()

        assert(result is ServerError && result.code == ServerError.Type.UNKNOWN)
    }

    @Test
    fun `changeAddress - ServerError with NETWORK type received when the server is not available`() {
        // Retrofit ждёт ответ всего 1 секунду
        server.enqueue(MockResponse().setBodyDelay(2, TimeUnit.SECONDS))

        val liveData = repo.changeAddress(Token(""), "")
        val result = liveData.getOrAwaitValue()

        assert(result is ServerError && result.code == ServerError.Type.NETWORK)
    }

    @Test
    fun `changeAddress - ServerError with HTTP_401 type received when the server returned 401`() {
        server.enqueue(MockResponse().setResponseCode(401))

        val liveData = repo.changeAddress(Token(""), "")
        val result = liveData.getOrAwaitValue()

        assert(result is ServerError && result.code == ServerError.Type.HTTP_401)
    }

    @Test
    fun `changeAddress - ServerError with UNKNOWN type received when the server returns an unsuccessful code`() {
        server.enqueue(MockResponse().setResponseCode(404))

        val liveData = repo.changeAddress(Token(""), "")
        val result = liveData.getOrAwaitValue()

        assert(result is ServerError && result.code == ServerError.Type.UNKNOWN)
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