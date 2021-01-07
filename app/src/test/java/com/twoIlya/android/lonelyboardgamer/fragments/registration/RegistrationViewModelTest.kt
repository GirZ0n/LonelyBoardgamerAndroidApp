package com.twoIlya.android.lonelyboardgamer.fragments.registration

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.twoIlya.android.lonelyboardgamer.dataClasses.Event
import com.twoIlya.android.lonelyboardgamer.keystore.RobolectricKeyStore
import com.twoIlya.android.lonelyboardgamer.repository.PreferencesRepository
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
class RegistrationViewModelTest {
    private lateinit var server: MockWebServer
    private lateinit var viewModel: RegistrationViewModel

    //region

    @Before
    fun init() {
        server = MockWebServer()
        server.start()

        val url = server.url("").toUrl().toString()
        val repo = ServerRepository
        repo.setURL(url)

        viewModel = RegistrationViewModel()
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

    @Test
    fun `updateCategories - Categories were updated then categories livaData was updated`() {
        val indices = intArrayOf(1, 2, 3, 10)

        viewModel.updateCategories(indices)

        val result = viewModel.categories.getOrAwaitValue()

        assert(result == PreferencesRepository.convertToCategoriesList(indices))
    }

    @Test
    fun `updateMechanics - Mechanics were updated then mechanics livaData was updated`() {
        val indices = intArrayOf(1, 2, 3, 10)

        viewModel.updateMechanics(indices)

        val result = viewModel.mechanics.getOrAwaitValue()

        assert(result == PreferencesRepository.convertToMechanicsList(indices))
    }

    @Test
    fun `updateAddress - Address was updated then address livaData was updated`() {
        val address = "Улица Пушкина, дом Колотущкина"

        viewModel.updateAddress(address)

        val result = viewModel.address.getOrAwaitValue()

        assert(result == address)
    }

    @Test
    fun `updateDescription - Description was updated then aboutMe livaData was updated`() {
        val description = "лЮбЛю МоНоПоЛиЮ"

        viewModel.updateAboutMe(description)

        val result = viewModel.aboutMe.getOrAwaitValue()

        assert(result == description)
    }

    @Test
    fun `register - Event with NOTIFICATION type received when the address field is empty`() {

        viewModel.register()

        val event = viewModel.events.getOrAwaitValue()

        assert(event.type == Event.Type.NOTIFICATION && event.message == "Укажите местоположение")
    }

    @Test
    fun `register - Event with NOTIFICATION type received when the description is more than 250 characters`() {
        fillProfile()

        val newDescription = "a".repeat(300)
        viewModel.updateAboutMe(newDescription)

        viewModel.register()

        val event = viewModel.events.getOrAwaitValue()

        assert(event.type == Event.Type.NOTIFICATION && event.message == "Описание должно содержать не более 250 символов")
    }

    //region register tests

    @Test
    fun `register - Event with MOVE type received when the server returned status 0`() {
        val body = getBodyFromJson("register_0_status_response")

        fillProfile()

        server.enqueue(
            MockResponse().setBody(body)
        )

        viewModel.register()
        val event = viewModel.events.getOrAwaitValue()

        assert(event.type == Event.Type.MOVE && event.message == "MyProfile")
    }

    @Test
    fun `register - Event with MOVE type received when the server returned status 1`() {
        val body = getBodyFromJson("1_status_response")

        fillProfile()

        server.enqueue(
            MockResponse().setBody(body)
        )

        viewModel.register()
        val event = viewModel.events.getOrAwaitValue()

        assert(event.type == Event.Type.MOVE && event.message == "Login")
    }

    @Test
    fun `register - Event with NOTIFICATION type received when the server returned status 2`() {
        val body = getBodyFromJson("2_status_response")

        fillProfile()

        server.enqueue(
            MockResponse().setBody(body)
        )

        viewModel.register()
        val event = viewModel.events.getOrAwaitValue()

        assert(event.type == Event.Type.NOTIFICATION)
    }

    @Test
    fun `register - Event with NOTIFICATION type received when the server returned status 3`() {
        val body = getBodyFromJson("3_status_response")

        fillProfile()

        server.enqueue(
            MockResponse().setBody(body)
        )

        viewModel.register()
        val event = viewModel.events.getOrAwaitValue()

        assert(event.type == Event.Type.NOTIFICATION)
    }

    @Test
    fun `register - Event with ERROR type received when the server returned status 4`() {
        val body = getBodyFromJson("4_status_response")

        fillProfile()

        server.enqueue(
            MockResponse().setBody(body)
        )

        viewModel.register()
        val event = viewModel.events.getOrAwaitValue()

        assert(event.type == Event.Type.ERROR)
    }

    @Test
    fun `register - Event with ERROR type received when the server returned status 5`() {
        val body = getBodyFromJson("5_status_response")

        fillProfile()

        server.enqueue(
            MockResponse().setBody(body)
        )

        viewModel.register()
        val event = viewModel.events.getOrAwaitValue()

        assert(event.type == Event.Type.ERROR)
    }

    @Test
    fun `register - Event with ERROR type received when the server returned unknown status`() {
        val body = getBodyFromJson("unknown_status_response")

        fillProfile()

        server.enqueue(
            MockResponse().setBody(body)
        )

        viewModel.register()
        val event = viewModel.events.getOrAwaitValue()

        assert(event.type == Event.Type.ERROR)
    }

    @Test
    fun `register - Event with NOTIFICATION type received when the server is not available`() {
        // Retrofit ждёт ответ всего 1 секунду
        server.enqueue(MockResponse().setBodyDelay(2, TimeUnit.SECONDS))

        fillProfile()

        viewModel.register()
        val event = viewModel.events.getOrAwaitValue()

        assert(event.type == Event.Type.NOTIFICATION)
    }

    @Test
    fun `register - Event with ERROR type received when the server returned 401`() {
        server.enqueue(MockResponse().setResponseCode(401))

        fillProfile()

        viewModel.register()
        val event = viewModel.events.getOrAwaitValue()

        assert(event.type == Event.Type.ERROR)
    }

    @Test
    fun `register - Event with ERROR type received when the server returns an unsuccessful code`() {
        server.enqueue(MockResponse().setResponseCode(404))

        fillProfile()

        viewModel.register()
        val event = viewModel.events.getOrAwaitValue()

        assert(event.type == Event.Type.ERROR)
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

    private fun fillProfile() {
        viewModel.updateAddress("Улица Пушкина, дом Колотушкина")
        viewModel.updateCategories(intArrayOf(1, 2, 3))
        viewModel.updateMechanics(intArrayOf(1, 2, 3))
        viewModel.updateAboutMe("Привет")
    }

    //endregion
}
