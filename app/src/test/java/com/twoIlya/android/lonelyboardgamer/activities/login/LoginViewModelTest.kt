package com.twoIlya.android.lonelyboardgamer.activities.login

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.twoIlya.android.lonelyboardgamer.dataClasses.Event
import com.twoIlya.android.lonelyboardgamer.keystore.RobolectricKeyStore
import com.twoIlya.android.lonelyboardgamer.repository.ServerRepository
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class LoginViewModelTest {
    private lateinit var server: MockWebServer
    private lateinit var viewModel: LoginViewModel

    //region

    @Before
    fun init() {
        server = MockWebServer()
        server.start()

        val url = server.url("").toUrl().toString()
        val repo = ServerRepository
        repo.setURL(url)

        viewModel = LoginViewModel()
    }


    @After
    fun down() {
        server.shutdown()
    }

    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    companion object {
        @JvmStatic
        @BeforeClass
        fun beforeClass() {
            RobolectricKeyStore.setup
        }
    }

    //endregion

    //region login tests

    @Test
    fun `Event with MOVE type received when the server returned status 0`() {
        val body = getBodyFromJson("login_0_status_response")

        server.enqueue(
                MockResponse().setBody(body)
        )

        viewModel.login("")
        val result = viewModel.events.getOrAwaitValue()

        assert(result.type == Event.Type.MOVE && result.message == "MyProfile")
    }

    @Test
    fun `Event with NOTIFICATION type received when the server returned status 1`() {
        val body = getBodyFromJson("1_status_response")

        server.enqueue(
                MockResponse().setBody(body)
        )

        viewModel.login("")
        val result = viewModel.events.getOrAwaitValue()

        assert(result.type == Event.Type.NOTIFICATION)
    }

    @Test
    fun `Event with NOTIFICATION type received when the server returned status 2`() {
        val body = getBodyFromJson("2_status_response")

        server.enqueue(
                MockResponse().setBody(body)
        )

        viewModel.login("")
        val result = viewModel.events.getOrAwaitValue()

        assert(result.type == Event.Type.NOTIFICATION)
    }

    @Test
    fun `Event with MOVE type received when the server returned status 3`() {
        val body = getBodyFromJson("3_status_response")

        server.enqueue(
                MockResponse().setBody(body)
        )

        viewModel.login("")
        val result = viewModel.events.getOrAwaitValue()

        assert(result.type == Event.Type.MOVE && result.message == "Registration")
    }

    @Test
    fun `Event with ERROR type received when the server returned status 4`() {
        val body = getBodyFromJson("4_status_response")

        server.enqueue(
                MockResponse().setBody(body)
        )

        viewModel.login("")
        val result = viewModel.events.getOrAwaitValue()

        assert(result.type == Event.Type.ERROR)
    }

    @Test
    fun `Event with ERROR type received when the server returned status 5`() {
        val body = getBodyFromJson("5_status_response")

        server.enqueue(
                MockResponse().setBody(body)
        )

        viewModel.login("")
        val result = viewModel.events.getOrAwaitValue()

        assert(result.type == Event.Type.ERROR)
    }

    @Test
    fun `Event with ERROR type received when the server returned unknown status`() {
        val body = getBodyFromJson("unknown_status_response")

        server.enqueue(
                MockResponse().setBody(body)
        )

        viewModel.login("")
        val result = viewModel.events.getOrAwaitValue()

        assert(result.type == Event.Type.ERROR)
    }

    @Test
    fun `Event with NOTIFICATION type received when the server is not available`() {
        // Retrofit ждёт ответ всего 1 секунду
        server.enqueue(MockResponse().setBodyDelay(2, TimeUnit.SECONDS))

        viewModel.login("")
        val result = viewModel.events.getOrAwaitValue()

        assert(result.type == Event.Type.NOTIFICATION)
    }

    @Test
    fun `Event with ERROR type received when the server returned 401`() {
        server.enqueue(MockResponse().setResponseCode(401))

        viewModel.login("")
        val result = viewModel.events.getOrAwaitValue()

        assert(result.type == Event.Type.ERROR)
    }

    @Test
    fun `Event with ERROR type received when the server returns an unsuccessful code`() {
        server.enqueue(MockResponse().setResponseCode(404))

        viewModel.login("")
        val result = viewModel.events.getOrAwaitValue()

        assert(result.type == Event.Type.ERROR)
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
