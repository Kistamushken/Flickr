package com.philuvarov.flickr.photos

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import com.philuvarov.flickr.base.TestStateContainer
import com.philuvarov.flickr.executeImmediately
import com.philuvarov.flickr.executeOnTestSubscriber
import com.philuvarov.flickr.photos.PhotoScreenAction.Initial
import com.philuvarov.flickr.photos.PhotoScreenAction.LoadMore
import com.philuvarov.flickr.photos.PhotoScreenAction.Query
import com.philuvarov.flickr.photos.PhotoScreenState.Empty
import com.philuvarov.flickr.photos.PhotoScreenState.Error
import com.philuvarov.flickr.photos.PhotoScreenState.Loaded
import com.philuvarov.flickr.photos.PhotoScreenState.Loading
import com.philuvarov.flickr.remote.Api
import com.philuvarov.flickr.remote.model.Photo
import com.philuvarov.flickr.remote.model.PhotosResponse
import com.philuvarov.flickr.util.TestSchedulersProvider
import io.reactivex.Single
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule

@Suppress("IllegalIdentifier")
class PhotoListUseCaseTest {

    @Rule
    @JvmField
    val rule: MockitoRule = MockitoJUnit.rule()

    @Mock private lateinit var api: Api

    private lateinit var useCase: PhotoListUseCase

    private val stateContainer = TestStateContainer<PhotoScreenState>(Empty())

    @Before
    fun setUp() {
        mockApi()
        useCase = photoListUseCase()
    }

    @Test
    fun `handle initial - loads recent from api - current state is empty`() {
        useCase.handle(Initial()).executeImmediately()

        verify(api).getRecent(1)
    }

    @Test
    fun `handle initial - loads recent from api - current state is Error and photos are empty`() {
        stateContainer.state = error()

        useCase.handle(Initial()).executeOnTestSubscriber()

        verify(api).getRecent(1)
    }

    @Test
    fun `handle initial - loads recent from api - current state is Loading and photos are empty`() {
        stateContainer.state = loading(photos = emptyList())

        useCase.handle(Initial()).executeOnTestSubscriber()

        verify(api).getRecent(1)
    }

    @Test
    fun `handle initial - return previous state - current state is loaded`() {
        stateContainer.state = loaded()

        val subscriber = useCase.handle(Initial()).executeOnTestSubscriber()

        verify(api, never()).getRecent(any())
        subscriber.assertValue { it is Loaded }
    }

    @Test
    fun `handle initial - return previous state - current state is Error and photos are not empty`() {
        stateContainer.state = error(photos = listOf(photoItem()))

        val subscriber = useCase.handle(Initial()).executeOnTestSubscriber()

        verify(api, never()).getRecent(any())
        subscriber.assertValue { it is Loaded }
    }

    @Test
    fun `handle initial - return previous state - current state is Loading and photos are not empty`() {
        stateContainer.state = loaded(photos = listOf(photoItem()))

        val subscriber = useCase.handle(Initial()).executeOnTestSubscriber()

        verify(api, never()).getRecent(any())
        subscriber.assertValue { it is Loaded }
    }

    @Test
    fun `handle query - loads first page of search - always`() {
        useCase.handle(Query("query")).executeImmediately()

        verify(api).searchPhotos(1, "query")
    }

    @Test
    fun `handle query - loads first page of recent - query is empty`() {
        useCase.handle(Query("")).executeImmediately()

        verify(api).getRecent(1)
    }

    @Test
    fun `handle query - loads first page of recent - query is blank`() {
        useCase.handle(Query("  ")).executeImmediately()

        verify(api).getRecent(1)
    }

    @Test
    fun `handle load more - loads next page of recent - state has no query`() {
        stateContainer.state = loaded(page = 1)

        useCase.handle(LoadMore()).executeImmediately()

        verify(api).getRecent(2)
    }

    @Test
    fun `handle load more - loads next page of search - state has query`() {
        stateContainer.state = loaded(page = 1, query = "query")

        useCase.handle(LoadMore()).executeImmediately()

        verify(api).searchPhotos(2, "query")
    }

    @Test
    fun `load - starts with Loading - always`() {
        givenRecentResponse(Single.just(photoResponse()))
        stateContainer.state = loaded()

        val subscriber = useCase.handle(LoadMore()).executeOnTestSubscriber()

        subscriber.assertValueAt(0) { newState -> newState is Loading }
    }

    @Test
    fun `load - Loading state contains previous state with incremented page - always`() {
        val initialList = listOf(photoItem())
        givenRecentResponse(Single.just(photoResponse()))
        stateContainer.state = loaded(photos = initialList, page = 1, query = "query")

        val subscriber = useCase.handle(LoadMore()).executeOnTestSubscriber()

        subscriber.assertValueAt(0) { newState ->
            newState.page == 2 &&
                    newState.query == "query" &&
                    newState.photos == initialList
        }
    }

    @Test
    fun `load - return Loaded with incremented page - on successful result`() {
        givenRecentResponse(Single.just(photoResponse()))
        stateContainer.state = loaded(page = 1)

        val subscriber = useCase.handle(LoadMore()).executeOnTestSubscriber()

        subscriber.assertValueAt(1) { newState ->
            newState is Loaded &&
                    newState.page == 2
        }
    }
    @Test
    fun `load - wraps state into Loaded state - on successful result`() {
        givenSearchResponse(Single.just(photoResponse(listOf(photo(1L, "url")))))
        stateContainer.state = loaded(query = "query")

        val subscriber = useCase.handle(LoadMore()).executeOnTestSubscriber()

        subscriber.assertValueAt(1) { newState ->
            newState is Loaded &&
                    newState.query == "query" &&
                    newState.photos.size == 1 &&
                    newState.photos[0].id == 1L &&
                    newState.photos[0].url == "url"
        }
    }

    @Test
    fun `load - adds new photos to existing list - on LoadMore action`() {
        val initialList = listOf(photoItem())
        givenSearchResponse(Single.just(photoResponse(listOf(photo(1L, "url")))))
        stateContainer.state = loaded(photos = initialList, query = "query")

        val subscriber = useCase.handle(LoadMore()).executeOnTestSubscriber()

        subscriber.assertValueAt(1) { newState -> newState.photos.size == 2 }
    }

    @Test
    fun `load - removes old photos from the list - on Query action`() {
        val oldItem = photoItem()
        val initialList = listOf(oldItem)
        givenSearchResponse(Single.just(photoResponse(listOf(photo(1L, "url")))))
        stateContainer.state = loaded(photos = initialList)

        val subscriber = useCase.handle(Query("query")).executeOnTestSubscriber()

        subscriber.assertValueAt(1) { newState ->
            newState.photos.size == 1 &&
                    !newState.photos.contains(oldItem)
        }
    }

    @Test
    fun `load - wraps exception into Error - always`() {
        givenRecentResponse(Single.error(Throwable()))
        stateContainer.state = loaded()

        val subscriber = useCase.handle(LoadMore()).executeOnTestSubscriber()

        subscriber.assertValueAt(1) { newState -> newState is Error }
    }

    @Test
    fun `load - wrapped Error has page of previous state - always`() {
        val pageN = 100
        val initialList = listOf(photoItem())
        givenRecentResponse(Single.error(Throwable()))
        stateContainer.state = loaded(photos = initialList, page = pageN)

        val subscriber = useCase.handle(LoadMore()).executeOnTestSubscriber()

        subscriber.assertValueAt(1) { newState ->
            newState is Error &&
                    newState.page == pageN &&
                    newState.query == null &&
                    newState.photos == initialList
        }
    }

    @Test
    fun `load - does not add new photo - photo with this id already exists`() {
        val initialList = listOf(photoItem(id = 100L))
        givenRecentResponse(Single.just(photoResponse(listOf(photo(id = 100L)))))
        stateContainer.state = loaded(photos = initialList)

        val subscriber = useCase.handle(LoadMore()).executeOnTestSubscriber()

        subscriber.assertValueAt(1) { newState ->
            newState is Loaded &&
                    newState.photos == initialList
        }
    }

    @Test
    fun `load - filters out photos with existing ids - always`() {
        val initialList = listOf(photoItem(id = 100L))
        givenRecentResponse(
                Single.just(
                        photoResponse(
                                listOf(
                                        photo(id = 100L),
                                        photo(id = 200L)
                                )
                        )
                )
        )
        stateContainer.state = loaded(photos = initialList)

        val subscriber = useCase.handle(LoadMore()).executeOnTestSubscriber()

        subscriber.assertValueAt(1) { newState ->
            newState is Loaded &&
                    newState.photos.size == 2 &&
                    newState.photos[0].id == 100L &&
                    newState.photos[1].id == 200L
        }
    }

    private fun mockApi() {
        givenRecentResponse(Single.never())
        givenSearchResponse(Single.never())
    }

    private fun givenSearchResponse(response: Single<PhotosResponse>) {
        whenever(api.searchPhotos(any(), any())).thenReturn(response)
    }

    private fun givenRecentResponse(response: Single<PhotosResponse>) {
        whenever(api.getRecent(any())).thenReturn(response)
    }

    private fun photoResponse(photos: List<Photo> = listOf(photo())) = PhotosResponse(photos, totalPages = 0)

    private fun photo(id: Long = 0L, url: String = "") = Photo(
            id,
            url,
            farm = "",
            secret = "",
            server = ""
    )

    private fun error(photos:List<PhotoItem> = emptyList(),
                      query: String? = null,
                      page: Int = 1) = Error(photos, query, page)

    private fun loaded(photos:List<PhotoItem> = emptyList(),
                       query: String? = null,
                       page: Int = 1) = Loaded(photos, query, page)

    private fun loading(photos:List<PhotoItem> = emptyList(),
                        query: String? = null,
                        page: Int = 1) = Loading(photos, query, page)

    private fun photoItem(id: Long = 0L, url: String = "") = PhotoItem(id, url)

    private fun photoListUseCase() = PhotoListUseCase(
            api,
            TestSchedulersProvider(),
            PhotoItemConverter(),
            stateContainer
    )
}