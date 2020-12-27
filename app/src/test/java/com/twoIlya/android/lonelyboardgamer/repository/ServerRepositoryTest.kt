package com.twoIlya.android.lonelyboardgamer.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.twoIlya.android.lonelyboardgamer.dataClasses.*
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

    //region changeDescription tests

    @Test
    fun `changeDescription - Token received when the server returned status 0`() {
        val body = getBodyFromJson("change_description_0_status_response")

        server.enqueue(
            MockResponse().setBody(body)
        )

        val liveData = repo.changeDescription(Token(""), "")
        val result = liveData.getOrAwaitValue()

        assert(result is ServerMessage)
    }

    @Test
    fun `changeDescription - ServerError with AUTHORIZATION type received when the server returned status 1`() {
        val body = getBodyFromJson("1_status_response")

        server.enqueue(
            MockResponse().setBody(body)
        )

        val liveData = repo.changeDescription(Token(""), "")
        val result = liveData.getOrAwaitValue()

        assert(result is ServerError && result.code == ServerError.Type.AUTHORIZATION)
    }

    @Test
    fun `changeDescription - ServerError with SOME_INFO_MISSING type received when the server returned status 2`() {
        val body = getBodyFromJson("2_status_response")

        server.enqueue(
            MockResponse().setBody(body)
        )

        val liveData = repo.changeDescription(Token(""), "")
        val result = liveData.getOrAwaitValue()

        assert(result is ServerError && result.code == ServerError.Type.SOME_INFO_MISSING)
    }

    @Test
    fun `changeDescription - ServerError with ELEMENT_WAS_NOT_FOUND type received when the server returned status 3`() {
        val body = getBodyFromJson("3_status_response")

        server.enqueue(
            MockResponse().setBody(body)
        )

        val liveData = repo.changeDescription(Token(""), "")
        val result = liveData.getOrAwaitValue()

        assert(result is ServerError && result.code == ServerError.Type.ELEMENT_WAS_NOT_FOUND)
    }

    @Test
    fun `changeDescription - ServerError with WRONG_DATA_FORMAT type received when the server returned status 4`() {
        val body = getBodyFromJson("4_status_response")

        server.enqueue(
            MockResponse().setBody(body)
        )

        val liveData = repo.changeDescription(Token(""), "")
        val result = liveData.getOrAwaitValue()

        assert(result is ServerError && result.code == ServerError.Type.WRONG_DATA_FORMAT)
    }

    @Test
    fun `changeDescription - ServerError with BAD_DATA type received when the server returned status 5`() {
        val body = getBodyFromJson("5_status_response")

        server.enqueue(
            MockResponse().setBody(body)
        )

        val liveData = repo.changeDescription(Token(""), "")
        val result = liveData.getOrAwaitValue()

        assert(result is ServerError && result.code == ServerError.Type.BAD_DATA)
    }

    @Test
    fun `changeDescription - ServerError with UNKNOWN type received when the server returned unknown status`() {
        val body = getBodyFromJson("unknown_status_response")

        server.enqueue(
            MockResponse().setBody(body)
        )

        val liveData = repo.changeDescription(Token(""), "")
        val result = liveData.getOrAwaitValue()

        assert(result is ServerError && result.code == ServerError.Type.UNKNOWN)
    }

    @Test
    fun `changeDescription - ServerError with NETWORK type received when the server is not available`() {
        // Retrofit ждёт ответ всего 1 секунду
        server.enqueue(MockResponse().setBodyDelay(2, TimeUnit.SECONDS))

        val liveData = repo.changeDescription(Token(""), "")
        val result = liveData.getOrAwaitValue()

        assert(result is ServerError && result.code == ServerError.Type.NETWORK)
    }

    @Test
    fun `changeDescription - ServerError with HTTP_401 type received when the server returned 401`() {
        server.enqueue(MockResponse().setResponseCode(401))

        val liveData = repo.changeDescription(Token(""), "")
        val result = liveData.getOrAwaitValue()

        assert(result is ServerError && result.code == ServerError.Type.HTTP_401)
    }

    @Test
    fun `changeDescription - ServerError with UNKNOWN type received when the server returns an unsuccessful code`() {
        server.enqueue(MockResponse().setResponseCode(404))

        val liveData = repo.changeDescription(Token(""), "")
        val result = liveData.getOrAwaitValue()

        assert(result is ServerError && result.code == ServerError.Type.UNKNOWN)
    }

    //endregion

    //region changeCategories tests

    @Test
    fun `changeCategories - Token received when the server returned status 0`() {
        val body = getBodyFromJson("change_categories_0_status_response")

        server.enqueue(
            MockResponse().setBody(body)
        )

        val liveData = repo.changeCategories(Token(""), emptyList())
        val result = liveData.getOrAwaitValue()

        assert(result is ServerMessage)
    }

    @Test
    fun `changeCategories - ServerError with AUTHORIZATION type received when the server returned status 1`() {
        val body = getBodyFromJson("1_status_response")

        server.enqueue(
            MockResponse().setBody(body)
        )

        val liveData = repo.changeCategories(Token(""), emptyList())
        val result = liveData.getOrAwaitValue()

        assert(result is ServerError && result.code == ServerError.Type.AUTHORIZATION)
    }

    @Test
    fun `changeCategories - ServerError with SOME_INFO_MISSING type received when the server returned status 2`() {
        val body = getBodyFromJson("2_status_response")

        server.enqueue(
            MockResponse().setBody(body)
        )

        val liveData = repo.changeCategories(Token(""), emptyList())
        val result = liveData.getOrAwaitValue()

        assert(result is ServerError && result.code == ServerError.Type.SOME_INFO_MISSING)
    }

    @Test
    fun `changeCategories - ServerError with ELEMENT_WAS_NOT_FOUND type received when the server returned status 3`() {
        val body = getBodyFromJson("3_status_response")

        server.enqueue(
            MockResponse().setBody(body)
        )

        val liveData = repo.changeCategories(Token(""), emptyList())
        val result = liveData.getOrAwaitValue()

        assert(result is ServerError && result.code == ServerError.Type.ELEMENT_WAS_NOT_FOUND)
    }

    @Test
    fun `changeCategories - ServerError with WRONG_DATA_FORMAT type received when the server returned status 4`() {
        val body = getBodyFromJson("4_status_response")

        server.enqueue(
            MockResponse().setBody(body)
        )

        val liveData = repo.changeCategories(Token(""), emptyList())
        val result = liveData.getOrAwaitValue()

        assert(result is ServerError && result.code == ServerError.Type.WRONG_DATA_FORMAT)
    }

    @Test
    fun `changeCategories - ServerError with BAD_DATA type received when the server returned status 5`() {
        val body = getBodyFromJson("5_status_response")

        server.enqueue(
            MockResponse().setBody(body)
        )

        val liveData = repo.changeCategories(Token(""), emptyList())
        val result = liveData.getOrAwaitValue()

        assert(result is ServerError && result.code == ServerError.Type.BAD_DATA)
    }

    @Test
    fun `changeCategories - ServerError with UNKNOWN type received when the server returned unknown status`() {
        val body = getBodyFromJson("unknown_status_response")

        server.enqueue(
            MockResponse().setBody(body)
        )

        val liveData = repo.changeCategories(Token(""), emptyList())
        val result = liveData.getOrAwaitValue()

        assert(result is ServerError && result.code == ServerError.Type.UNKNOWN)
    }

    @Test
    fun `changeCategories - ServerError with NETWORK type received when the server is not available`() {
        // Retrofit ждёт ответ всего 1 секунду
        server.enqueue(MockResponse().setBodyDelay(2, TimeUnit.SECONDS))

        val liveData = repo.changeCategories(Token(""), emptyList())
        val result = liveData.getOrAwaitValue()

        assert(result is ServerError && result.code == ServerError.Type.NETWORK)
    }

    @Test
    fun `changeCategories - ServerError with HTTP_401 type received when the server returned 401`() {
        server.enqueue(MockResponse().setResponseCode(401))

        val liveData = repo.changeCategories(Token(""), emptyList())
        val result = liveData.getOrAwaitValue()

        assert(result is ServerError && result.code == ServerError.Type.HTTP_401)
    }

    @Test
    fun `changeCategories - ServerError with UNKNOWN type received when the server returns an unsuccessful code`() {
        server.enqueue(MockResponse().setResponseCode(404))

        val liveData = repo.changeCategories(Token(""), emptyList())
        val result = liveData.getOrAwaitValue()

        assert(result is ServerError && result.code == ServerError.Type.UNKNOWN)
    }

    //endregion

    //region changeMechanics tests

    @Test
    fun `changeMechanics - Token received when the server returned status 0`() {
        val body = getBodyFromJson("change_mechanics_0_status_response")

        server.enqueue(
            MockResponse().setBody(body)
        )

        val liveData = repo.changeMechanics(Token(""), emptyList())
        val result = liveData.getOrAwaitValue()

        assert(result is ServerMessage)
    }

    @Test
    fun `changeMechanics - ServerError with AUTHORIZATION type received when the server returned status 1`() {
        val body = getBodyFromJson("1_status_response")

        server.enqueue(
            MockResponse().setBody(body)
        )

        val liveData = repo.changeMechanics(Token(""), emptyList())
        val result = liveData.getOrAwaitValue()

        assert(result is ServerError && result.code == ServerError.Type.AUTHORIZATION)
    }

    @Test
    fun `changeMechanics - ServerError with SOME_INFO_MISSING type received when the server returned status 2`() {
        val body = getBodyFromJson("2_status_response")

        server.enqueue(
            MockResponse().setBody(body)
        )

        val liveData = repo.changeMechanics(Token(""), emptyList())
        val result = liveData.getOrAwaitValue()

        assert(result is ServerError && result.code == ServerError.Type.SOME_INFO_MISSING)
    }

    @Test
    fun `changeMechanics - ServerError with ELEMENT_WAS_NOT_FOUND type received when the server returned status 3`() {
        val body = getBodyFromJson("3_status_response")

        server.enqueue(
            MockResponse().setBody(body)
        )

        val liveData = repo.changeMechanics(Token(""), emptyList())
        val result = liveData.getOrAwaitValue()

        assert(result is ServerError && result.code == ServerError.Type.ELEMENT_WAS_NOT_FOUND)
    }

    @Test
    fun `changeMechanics - ServerError with WRONG_DATA_FORMAT type received when the server returned status 4`() {
        val body = getBodyFromJson("4_status_response")

        server.enqueue(
            MockResponse().setBody(body)
        )

        val liveData = repo.changeMechanics(Token(""), emptyList())
        val result = liveData.getOrAwaitValue()

        assert(result is ServerError && result.code == ServerError.Type.WRONG_DATA_FORMAT)
    }

    @Test
    fun `changeMechanics - ServerError with BAD_DATA type received when the server returned status 5`() {
        val body = getBodyFromJson("5_status_response")

        server.enqueue(
            MockResponse().setBody(body)
        )

        val liveData = repo.changeMechanics(Token(""), emptyList())
        val result = liveData.getOrAwaitValue()

        assert(result is ServerError && result.code == ServerError.Type.BAD_DATA)
    }

    @Test
    fun `changeMechanics - ServerError with UNKNOWN type received when the server returned unknown status`() {
        val body = getBodyFromJson("unknown_status_response")

        server.enqueue(
            MockResponse().setBody(body)
        )

        val liveData = repo.changeMechanics(Token(""), emptyList())
        val result = liveData.getOrAwaitValue()

        assert(result is ServerError && result.code == ServerError.Type.UNKNOWN)
    }

    @Test
    fun `changeMechanics - ServerError with NETWORK type received when the server is not available`() {
        // Retrofit ждёт ответ всего 1 секунду
        server.enqueue(MockResponse().setBodyDelay(2, TimeUnit.SECONDS))

        val liveData = repo.changeMechanics(Token(""), emptyList())
        val result = liveData.getOrAwaitValue()

        assert(result is ServerError && result.code == ServerError.Type.NETWORK)
    }

    @Test
    fun `changeMechanics - ServerError with HTTP_401 type received when the server returned 401`() {
        server.enqueue(MockResponse().setResponseCode(401))

        val liveData = repo.changeMechanics(Token(""), emptyList())
        val result = liveData.getOrAwaitValue()

        assert(result is ServerError && result.code == ServerError.Type.HTTP_401)
    }

    @Test
    fun `changeMechanics - ServerError with UNKNOWN type received when the server returns an unsuccessful code`() {
        server.enqueue(MockResponse().setResponseCode(404))

        val liveData = repo.changeMechanics(Token(""), emptyList())
        val result = liveData.getOrAwaitValue()

        assert(result is ServerError && result.code == ServerError.Type.UNKNOWN)
    }

    //endregion

    //region searchByID tests

    @Test
    fun `searchByID - Token received when the server returned status 0`() {
        val body = getBodyFromJson("search_by_id_0_status_response")

        server.enqueue(
            MockResponse().setBody(body)
        )

        val liveData = repo.searchByID(Token(""), 0)
        val result = liveData.getOrAwaitValue()

        val isCorrectProfile =
            result is UserProfile &&
                    result.friendStatus == 0 &&
                    result.id == 1 &&
                    result.idVK == "23456" &&
                    result.firstName == "Иван" &&
                    result.secondName == "Иванов" &&
                    result.categories == listOf("Рандом", "Простые") &&
                    result.mechanics == listOf("Блеф") &&
                    result.description == "Lets play some munchkin!"

        assert(isCorrectProfile)
    }

    @Test
    fun `searchByID - ServerError with AUTHORIZATION type received when the server returned status 1`() {
        val body = getBodyFromJson("1_status_response")

        server.enqueue(
            MockResponse().setBody(body)
        )

        val liveData = repo.searchByID(Token(""), 0)
        val result = liveData.getOrAwaitValue()

        assert(result is ServerError && result.code == ServerError.Type.AUTHORIZATION)
    }

    @Test
    fun `searchByID - ServerError with SOME_INFO_MISSING type received when the server returned status 2`() {
        val body = getBodyFromJson("2_status_response")

        server.enqueue(
            MockResponse().setBody(body)
        )

        val liveData = repo.searchByID(Token(""), 0)
        val result = liveData.getOrAwaitValue()

        assert(result is ServerError && result.code == ServerError.Type.SOME_INFO_MISSING)
    }

    @Test
    fun `searchByID - ServerError with ELEMENT_WAS_NOT_FOUND type received when the server returned status 3`() {
        val body = getBodyFromJson("3_status_response")

        server.enqueue(
            MockResponse().setBody(body)
        )

        val liveData = repo.searchByID(Token(""), 0)
        val result = liveData.getOrAwaitValue()

        assert(result is ServerError && result.code == ServerError.Type.ELEMENT_WAS_NOT_FOUND)
    }

    @Test
    fun `searchByID - ServerError with WRONG_DATA_FORMAT type received when the server returned status 4`() {
        val body = getBodyFromJson("4_status_response")

        server.enqueue(
            MockResponse().setBody(body)
        )

        val liveData = repo.searchByID(Token(""), 0)
        val result = liveData.getOrAwaitValue()

        assert(result is ServerError && result.code == ServerError.Type.WRONG_DATA_FORMAT)
    }

    @Test
    fun `searchByID - ServerError with BAD_DATA type received when the server returned status 5`() {
        val body = getBodyFromJson("5_status_response")

        server.enqueue(
            MockResponse().setBody(body)
        )

        val liveData = repo.searchByID(Token(""), 0)
        val result = liveData.getOrAwaitValue()

        assert(result is ServerError && result.code == ServerError.Type.BAD_DATA)
    }

    @Test
    fun `searchByID - ServerError with UNKNOWN type received when the server returned unknown status`() {
        val body = getBodyFromJson("unknown_status_response")

        server.enqueue(
            MockResponse().setBody(body)
        )

        val liveData = repo.searchByID(Token(""), 0)
        val result = liveData.getOrAwaitValue()

        assert(result is ServerError && result.code == ServerError.Type.UNKNOWN)
    }

    @Test
    fun `searchByID - ServerError with NETWORK type received when the server is not available`() {
        // Retrofit ждёт ответ всего 1 секунду
        server.enqueue(MockResponse().setBodyDelay(2, TimeUnit.SECONDS))

        val liveData = repo.searchByID(Token(""), 0)
        val result = liveData.getOrAwaitValue()

        assert(result is ServerError && result.code == ServerError.Type.NETWORK)
    }

    @Test
    fun `searchByID - ServerError with HTTP_401 type received when the server returned 401`() {
        server.enqueue(MockResponse().setResponseCode(401))

        val liveData = repo.searchByID(Token(""), 0)
        val result = liveData.getOrAwaitValue()

        assert(result is ServerError && result.code == ServerError.Type.HTTP_401)
    }

    @Test
    fun `searchByID - ServerError with UNKNOWN type received when the server returns an unsuccessful code`() {
        server.enqueue(MockResponse().setResponseCode(404))

        val liveData = repo.searchByID(Token(""), 0)
        val result = liveData.getOrAwaitValue()

        assert(result is ServerError && result.code == ServerError.Type.UNKNOWN)
    }

    //endregion

    //region sendFriendRequest tests

    @Test
    fun `sendFriendRequest - Token received when the server returned status 0`() {
        val body = getBodyFromJson("send_friend_request_0_status_response")

        server.enqueue(
            MockResponse().setBody(body)
        )

        val liveData = repo.sendFriendRequest(Token(""), 0)
        val result = liveData.getOrAwaitValue()

        assert(result is ServerMessage)
    }

    @Test
    fun `sendFriendRequest - ServerError with AUTHORIZATION type received when the server returned status 1`() {
        val body = getBodyFromJson("1_status_response")

        server.enqueue(
            MockResponse().setBody(body)
        )

        val liveData = repo.sendFriendRequest(Token(""), 0)
        val result = liveData.getOrAwaitValue()

        assert(result is ServerError && result.code == ServerError.Type.AUTHORIZATION)
    }

    @Test
    fun `sendFriendRequest - ServerError with SOME_INFO_MISSING type received when the server returned status 2`() {
        val body = getBodyFromJson("2_status_response")

        server.enqueue(
            MockResponse().setBody(body)
        )

        val liveData = repo.sendFriendRequest(Token(""), 0)
        val result = liveData.getOrAwaitValue()

        assert(result is ServerError && result.code == ServerError.Type.SOME_INFO_MISSING)
    }

    @Test
    fun `sendFriendRequest - ServerError with ELEMENT_WAS_NOT_FOUND type received when the server returned status 3`() {
        val body = getBodyFromJson("3_status_response")

        server.enqueue(
            MockResponse().setBody(body)
        )

        val liveData = repo.sendFriendRequest(Token(""), 0)
        val result = liveData.getOrAwaitValue()

        assert(result is ServerError && result.code == ServerError.Type.ELEMENT_WAS_NOT_FOUND)
    }

    @Test
    fun `sendFriendRequest - ServerError with WRONG_DATA_FORMAT type received when the server returned status 4`() {
        val body = getBodyFromJson("4_status_response")

        server.enqueue(
            MockResponse().setBody(body)
        )

        val liveData = repo.sendFriendRequest(Token(""), 0)
        val result = liveData.getOrAwaitValue()

        assert(result is ServerError && result.code == ServerError.Type.WRONG_DATA_FORMAT)
    }

    @Test
    fun `sendFriendRequest - ServerError with BAD_DATA type received when the server returned status 5`() {
        val body = getBodyFromJson("5_status_response")

        server.enqueue(
            MockResponse().setBody(body)
        )

        val liveData = repo.sendFriendRequest(Token(""), 0)
        val result = liveData.getOrAwaitValue()

        assert(result is ServerError && result.code == ServerError.Type.BAD_DATA)
    }

    @Test
    fun `sendFriendRequest - ServerError with UNKNOWN type received when the server returned unknown status`() {
        val body = getBodyFromJson("unknown_status_response")

        server.enqueue(
            MockResponse().setBody(body)
        )

        val liveData = repo.sendFriendRequest(Token(""), 0)
        val result = liveData.getOrAwaitValue()

        assert(result is ServerError && result.code == ServerError.Type.UNKNOWN)
    }

    @Test
    fun `sendFriendRequest - ServerError with NETWORK type received when the server is not available`() {
        // Retrofit ждёт ответ всего 1 секунду
        server.enqueue(MockResponse().setBodyDelay(2, TimeUnit.SECONDS))

        val liveData = repo.sendFriendRequest(Token(""), 0)
        val result = liveData.getOrAwaitValue()

        assert(result is ServerError && result.code == ServerError.Type.NETWORK)
    }

    @Test
    fun `sendFriendRequest - ServerError with HTTP_401 type received when the server returned 401`() {
        server.enqueue(MockResponse().setResponseCode(401))

        val liveData = repo.sendFriendRequest(Token(""), 0)
        val result = liveData.getOrAwaitValue()

        assert(result is ServerError && result.code == ServerError.Type.HTTP_401)
    }

    @Test
    fun `sendFriendRequest - ServerError with UNKNOWN type received when the server returns an unsuccessful code`() {
        server.enqueue(MockResponse().setResponseCode(404))

        val liveData = repo.sendFriendRequest(Token(""), 0)
        val result = liveData.getOrAwaitValue()

        assert(result is ServerError && result.code == ServerError.Type.UNKNOWN)
    }

    //endregion

    //region revokeRequest tests

    @Test
    fun `revokeRequest - Token received when the server returned status 0`() {
        val body = getBodyFromJson("revoke_request_0_status_response")

        server.enqueue(
            MockResponse().setBody(body)
        )

        val liveData = repo.revokeRequest(Token(""), 0)
        val result = liveData.getOrAwaitValue()

        assert(result is ServerMessage)
    }

    @Test
    fun `revokeRequest - ServerError with AUTHORIZATION type received when the server returned status 1`() {
        val body = getBodyFromJson("1_status_response")

        server.enqueue(
            MockResponse().setBody(body)
        )

        val liveData = repo.revokeRequest(Token(""), 0)
        val result = liveData.getOrAwaitValue()

        assert(result is ServerError && result.code == ServerError.Type.AUTHORIZATION)
    }

    @Test
    fun `revokeRequest - ServerError with SOME_INFO_MISSING type received when the server returned status 2`() {
        val body = getBodyFromJson("2_status_response")

        server.enqueue(
            MockResponse().setBody(body)
        )

        val liveData = repo.revokeRequest(Token(""), 0)
        val result = liveData.getOrAwaitValue()

        assert(result is ServerError && result.code == ServerError.Type.SOME_INFO_MISSING)
    }

    @Test
    fun `revokeRequest - ServerError with ELEMENT_WAS_NOT_FOUND type received when the server returned status 3`() {
        val body = getBodyFromJson("3_status_response")

        server.enqueue(
            MockResponse().setBody(body)
        )

        val liveData = repo.revokeRequest(Token(""), 0)
        val result = liveData.getOrAwaitValue()

        assert(result is ServerError && result.code == ServerError.Type.ELEMENT_WAS_NOT_FOUND)
    }

    @Test
    fun `revokeRequest - ServerError with WRONG_DATA_FORMAT type received when the server returned status 4`() {
        val body = getBodyFromJson("4_status_response")

        server.enqueue(
            MockResponse().setBody(body)
        )

        val liveData = repo.revokeRequest(Token(""), 0)
        val result = liveData.getOrAwaitValue()

        assert(result is ServerError && result.code == ServerError.Type.WRONG_DATA_FORMAT)
    }

    @Test
    fun `revokeRequest - ServerError with BAD_DATA type received when the server returned status 5`() {
        val body = getBodyFromJson("5_status_response")

        server.enqueue(
            MockResponse().setBody(body)
        )

        val liveData = repo.revokeRequest(Token(""), 0)
        val result = liveData.getOrAwaitValue()

        assert(result is ServerError && result.code == ServerError.Type.BAD_DATA)
    }

    @Test
    fun `revokeRequest - ServerError with UNKNOWN type received when the server returned unknown status`() {
        val body = getBodyFromJson("unknown_status_response")

        server.enqueue(
            MockResponse().setBody(body)
        )

        val liveData = repo.revokeRequest(Token(""), 0)
        val result = liveData.getOrAwaitValue()

        assert(result is ServerError && result.code == ServerError.Type.UNKNOWN)
    }

    @Test
    fun `revokeRequest - ServerError with NETWORK type received when the server is not available`() {
        // Retrofit ждёт ответ всего 1 секунду
        server.enqueue(MockResponse().setBodyDelay(2, TimeUnit.SECONDS))

        val liveData = repo.revokeRequest(Token(""), 0)
        val result = liveData.getOrAwaitValue()

        assert(result is ServerError && result.code == ServerError.Type.NETWORK)
    }

    @Test
    fun `revokeRequest - ServerError with HTTP_401 type received when the server returned 401`() {
        server.enqueue(MockResponse().setResponseCode(401))

        val liveData = repo.revokeRequest(Token(""), 0)
        val result = liveData.getOrAwaitValue()

        assert(result is ServerError && result.code == ServerError.Type.HTTP_401)
    }

    @Test
    fun `revokeRequest - ServerError with UNKNOWN type received when the server returns an unsuccessful code`() {
        server.enqueue(MockResponse().setResponseCode(404))

        val liveData = repo.revokeRequest(Token(""), 0)
        val result = liveData.getOrAwaitValue()

        assert(result is ServerError && result.code == ServerError.Type.UNKNOWN)
    }

    //endregion

    //region answerOnRequest tests

    @Test
    fun `answerOnRequest - Token received when the server returned status 0`() {
        val body = getBodyFromJson("answer_on_request_0_status_response")

        server.enqueue(
            MockResponse().setBody(body)
        )

        val liveData = repo.answerOnRequest(Token(""), 0, true)
        val result = liveData.getOrAwaitValue()

        assert(result is ServerMessage)
    }

    @Test
    fun `answerOnRequest - ServerError with AUTHORIZATION type received when the server returned status 1`() {
        val body = getBodyFromJson("1_status_response")

        server.enqueue(
            MockResponse().setBody(body)
        )

        val liveData = repo.answerOnRequest(Token(""), 0, true)
        val result = liveData.getOrAwaitValue()

        assert(result is ServerError && result.code == ServerError.Type.AUTHORIZATION)
    }

    @Test
    fun `answerOnRequest - ServerError with SOME_INFO_MISSING type received when the server returned status 2`() {
        val body = getBodyFromJson("2_status_response")

        server.enqueue(
            MockResponse().setBody(body)
        )

        val liveData = repo.answerOnRequest(Token(""), 0, true)
        val result = liveData.getOrAwaitValue()

        assert(result is ServerError && result.code == ServerError.Type.SOME_INFO_MISSING)
    }

    @Test
    fun `answerOnRequest - ServerError with ELEMENT_WAS_NOT_FOUND type received when the server returned status 3`() {
        val body = getBodyFromJson("3_status_response")

        server.enqueue(
            MockResponse().setBody(body)
        )

        val liveData = repo.answerOnRequest(Token(""), 0, true)
        val result = liveData.getOrAwaitValue()

        assert(result is ServerError && result.code == ServerError.Type.ELEMENT_WAS_NOT_FOUND)
    }

    @Test
    fun `answerOnRequest - ServerError with WRONG_DATA_FORMAT type received when the server returned status 4`() {
        val body = getBodyFromJson("4_status_response")

        server.enqueue(
            MockResponse().setBody(body)
        )

        val liveData = repo.answerOnRequest(Token(""), 0, true)
        val result = liveData.getOrAwaitValue()

        assert(result is ServerError && result.code == ServerError.Type.WRONG_DATA_FORMAT)
    }

    @Test
    fun `answerOnRequest - ServerError with BAD_DATA type received when the server returned status 5`() {
        val body = getBodyFromJson("5_status_response")

        server.enqueue(
            MockResponse().setBody(body)
        )

        val liveData = repo.answerOnRequest(Token(""), 0, true)
        val result = liveData.getOrAwaitValue()

        assert(result is ServerError && result.code == ServerError.Type.BAD_DATA)
    }

    @Test
    fun `answerOnRequest - ServerError with UNKNOWN type received when the server returned unknown status`() {
        val body = getBodyFromJson("unknown_status_response")

        server.enqueue(
            MockResponse().setBody(body)
        )

        val liveData = repo.answerOnRequest(Token(""), 0, true)
        val result = liveData.getOrAwaitValue()

        assert(result is ServerError && result.code == ServerError.Type.UNKNOWN)
    }

    @Test
    fun `answerOnRequest - ServerError with NETWORK type received when the server is not available`() {
        // Retrofit ждёт ответ всего 1 секунду
        server.enqueue(MockResponse().setBodyDelay(2, TimeUnit.SECONDS))

        val liveData = repo.answerOnRequest(Token(""), 0, true)
        val result = liveData.getOrAwaitValue()

        assert(result is ServerError && result.code == ServerError.Type.NETWORK)
    }

    @Test
    fun `answerOnRequest - ServerError with HTTP_401 type received when the server returned 401`() {
        server.enqueue(MockResponse().setResponseCode(401))

        val liveData = repo.answerOnRequest(Token(""), 0, true)
        val result = liveData.getOrAwaitValue()

        assert(result is ServerError && result.code == ServerError.Type.HTTP_401)
    }

    @Test
    fun `answerOnRequest - ServerError with UNKNOWN type received when the server returns an unsuccessful code`() {
        server.enqueue(MockResponse().setResponseCode(404))

        val liveData = repo.answerOnRequest(Token(""), 0, true)
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
