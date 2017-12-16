package com.philuvarov.flickr.photos

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import com.philuvarov.flickr.assertLastValue
import com.philuvarov.flickr.executeImmediately
import com.philuvarov.flickr.executeOnTestSubscriber
import com.philuvarov.flickr.photos.PhotoScreenAction.Initial
import com.philuvarov.flickr.photos.PhotoScreenAction.LoadMore
import com.philuvarov.flickr.photos.PhotoScreenAction.LoadingError
import com.philuvarov.flickr.photos.PhotoScreenAction.PageLoaded
import com.philuvarov.flickr.photos.PhotoScreenAction.Query
import com.philuvarov.flickr.photos.PhotoScreenAction.QueryLoaded
import com.philuvarov.flickr.photos.PhotoScreenState.Empty
import com.philuvarov.flickr.photos.PhotoScreenState.Loading
import com.philuvarov.flickr.remote.Api
import com.philuvarov.flickr.remote.model.Photo
import com.philuvarov.flickr.remote.model.PhotosResponse
import com.philuvarov.flickr.util.TestSchedulersProvider
import io.reactivex.Single
import io.reactivex.rxkotlin.toSingle
import io.reactivex.subjects.PublishSubject
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule

private const val DEFAULT_QUERY = "kittens"

@Suppress("IllegalIdentifier")
class PhotoListDriverTest {

    @Mock private lateinit var api: Api

    @Rule
    @JvmField
    val rule: MockitoRule = MockitoJUnit.rule()

    private val events = PublishSubject.create<Pair<PhotoScreenAction, PhotoScreenState>>()

    private lateinit var driver: PhotoListDriver

    @Before
    fun setUp() {
        driver = photoListDriver()
        mockApi()
    }

    @Test
    fun `combine - loads first page with default query - action is Intial and state is Empty`() {
        driver.results(events).executeImmediately()

        events.onNext(Initial to empty())

        verify(api).searchPhotos(1, DEFAULT_QUERY)
    }

    @Test
    fun `combine - does not do anything - action is Initial and state is not Empty`() {
        val results = driver.results(events).executeOnTestSubscriber()

        events.onNext(Initial to loading())

        results.assertNoValues()
        verify(api, never()).searchPhotos(any(), any())
    }

    @Test
    fun `combine - loads new search - action is Query`() {
        driver.results(events).executeImmediately()

        events.onNext(Query("query") to loading())

        verify(api).searchPhotos(1, "query")
    }

    @Test
    fun `combine - loads next page - action is LoadMore`() {
        driver.results(events).executeImmediately()

        events.onNext(LoadMore to loading(query = "query", page = 1))

        verify(api).searchPhotos(2, "query")
    }

    @Test
    fun `query - wraps response into QueryLoaded action - always`() {
        givenSearchResponse(photoResponse().toSingle())
        val results = driver.results(events).executeOnTestSubscriber()

        events.onNext(Query("query") to loading())

        results.assertLastValue { this is QueryLoaded && query == "query" && photos.size == 1 }
    }

    @Test
    fun `load next page - wraps response into PageLoaded action - always`() {
        givenSearchResponse(photoResponse().toSingle())
        val results = driver.results(events).executeOnTestSubscriber()

        events.onNext(LoadMore to loading(query = "query"))

        results.assertLastValue { this is PageLoaded && query == "query" && photos.size == 1 }
    }

    @Test
    fun `load next page - increments page - on successful load`() {
        givenSearchResponse(photoResponse().toSingle())
        val results = driver.results(events).executeOnTestSubscriber()

        events.onNext(LoadMore to loading(page = 2))

        results.assertLastValue { this is PageLoaded && page == 3 }
    }

    @Test
    fun `load next page - leaves page as is - on error during load`() {
        givenSearchResponse(Single.error(Throwable()))
        val results = driver.results(events).executeOnTestSubscriber()

        events.onNext(LoadMore to loading(page = 2))

        results.assertLastValue { this is LoadingError && page == 2 }
    }

    @Test
    fun `load initial page  - wraps response into PageLoaded action - always`() {
        givenSearchResponse(photoResponse().toSingle())
        val results = driver.results(events).executeOnTestSubscriber()

        events.onNext(Initial to empty())

        results.assertLastValue { this is PageLoaded && query == DEFAULT_QUERY && photos.size == 1 && page == 1 }
    }

    @Test
    fun `load - wraps exception into LoadingError action - always`() {
        givenSearchResponse(Single.error(Throwable()))
        val results = driver.results(events).executeOnTestSubscriber()

        events.onNext(Initial to empty())

        results.assertNoErrors()
        results.assertLastValue { this is LoadingError && page == 1 }
    }

    private fun empty(photos: List<PhotoItem> = emptyList(),
                       query: String? = null,
                       page: Int = 1) = Empty(photos, query, page)

    private fun loading(photos: List<PhotoItem> = emptyList(),
                        query: String? = null,
                        page: Int = 1) = Loading(photos, query, page)


    private fun photoListDriver() = PhotoListDriver(TestSchedulersProvider(), PhotoItemConverter(), api)

    private fun mockApi() {
        givenSearchResponse(Single.never())
    }

    private fun givenSearchResponse(response: Single<PhotosResponse>) {
        whenever(api.searchPhotos(any(), any())).thenReturn(response)
    }

    private fun photoResponse(photos: List<Photo> = listOf(photo())) = PhotosResponse(photos, totalPages = 0)

    private fun photo(id: Long = 0L, url: String = "") = Photo(
            id,
            url,
            farm = "",
            secret = "",
            server = ""
    )

}