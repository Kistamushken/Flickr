package com.philuvarov.flickr.photos

import com.philuvarov.flickr.assertLastValue
import com.philuvarov.flickr.base.TestStateContainer
import com.philuvarov.flickr.executeOnTestSubscriber
import com.philuvarov.flickr.photos.PhotoScreenMessage.Initial
import com.philuvarov.flickr.photos.PhotoScreenMessage.LoadMore
import com.philuvarov.flickr.photos.PhotoScreenMessage.LoadingError
import com.philuvarov.flickr.photos.PhotoScreenMessage.PageLoaded
import com.philuvarov.flickr.photos.PhotoScreenMessage.Query
import com.philuvarov.flickr.photos.PhotoScreenMessage.QueryLoaded
import com.philuvarov.flickr.photos.PhotoScreenState.Empty
import com.philuvarov.flickr.photos.PhotoScreenState.Error
import com.philuvarov.flickr.photos.PhotoScreenState.Loaded
import com.philuvarov.flickr.photos.PhotoScreenState.Loading
import io.reactivex.subjects.PublishSubject
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule

@Suppress("IllegalIdentifier")
class PhotoListModelTest {

    @Rule
    @JvmField
    val rule: MockitoRule = MockitoJUnit.rule()

    private lateinit var model: PhotoListModel

    private val stateContainer = TestStateContainer<PhotoScreenState>(Empty())

    private val actionsStream = PublishSubject.create<PhotoScreenMessage>()

    @Before
    fun setUp() {
        model = photoListModel()
        model.observe(actionsStream)
    }

    @Test
    fun `observe - starts with Empty state - always`() {
        val result = model.states().executeOnTestSubscriber()

        result.assertLastValue { this is Empty }
    }

    @Test
    fun `reduce Initial action - returns Loading - previous state is Empty`() {
        val result = model.states().executeOnTestSubscriber()

        actionsStream.onNext(Initial)

        result.assertLastValue { this is Loading }
    }

    @Test
    fun `reduce Initial action - returns Loaded - previous state is Error`() {
        val result = model.states().executeOnTestSubscriber()
        actionsStream.onNext(loadingError())

        actionsStream.onNext(Initial)

        result.assertLastValue { this is Loaded }
    }

    @Test
    fun `reduce Initial action - returns Loading - last state is Loading`() {
        val result = model.states().executeOnTestSubscriber()
        actionsStream.onNext(Initial)

        actionsStream.onNext(Initial)

        result.assertLastValue { this is Loading }
    }

    @Test
    fun `reduce Query action - returns Loading - always`() {
        val result = model.states().executeOnTestSubscriber()

        actionsStream.onNext(query("query"))

        result.assertLastValue { this is Loading && query == "query" }
    }

    @Test
    fun `reduce LoadMore action - returns Loading - always`() {
        val result = model.states().executeOnTestSubscriber()

        actionsStream.onNext(LoadMore)

        result.assertLastValue { this is Loading }
    }

    @Test
    fun `reduce PageLoaded action - returns Loading - action & state pages are the same`() {
        val result = model.states().executeOnTestSubscriber()
        actionsStream.onNext(LoadMore)

        actionsStream.onNext(pageLoaded(page = 0))

        result.assertLastValue { this is Loading }
    }

    @Test
    fun `reduce PageLoaded action - returns Loaded - action & state pages are different`() {
        val result = model.states().executeOnTestSubscriber()
        actionsStream.onNext(pageLoaded(page = 1))

        actionsStream.onNext(pageLoaded(page = 2))

        result.assertLastValue { this is Loaded && page == 2 }
    }

    @Test
    fun `reduce PageLoaded action - unions existing list of photos with new one - always`() {
        val result = model.states().executeOnTestSubscriber()
        actionsStream.onNext(pageLoaded(listOf(photoItem(id = 1)), page = 1))

        actionsStream.onNext(pageLoaded(listOf(photoItem(id = 1), photoItem(id = 2)), page = 2))

        result.assertLastValue { photos.size == 2 && photos[0].id == 1L && photos[1].id == 2L }
    }

    @Test
    fun `reduce QueryLoaded action - returns Loaded - always`() {
        val result = model.states().executeOnTestSubscriber()

        actionsStream.onNext(queryLoaded())

        result.assertLastValue { this is Loaded }
    }

    @Test
    fun `reduce QueryLoaded action - replaces data with a new one - always`() {
        val oldPhotos = listOf(photoItem(1))
        val newPhotos = listOf(photoItem(2))
        val result = model.states().executeOnTestSubscriber()
        actionsStream.onNext(pageLoaded(oldPhotos, "oldQuery", page = 10))

        actionsStream.onNext(queryLoaded(newPhotos, "newQuery"))

        result.assertLastValue { photos === newPhotos && query == "newQuery" && page == 1 }
    }

    @Test
    fun `reduce LoadingError action - returns Error - always`() {
        val result = model.states().executeOnTestSubscriber()

        actionsStream.onNext(loadingError())

        result.assertLastValue { this is Error }
    }

    @Test
    fun `reduce LoadingError action - Error contains data of the last state - always`() {
        val photos = listOf(photoItem(1))
        val result = model.states().executeOnTestSubscriber()
        actionsStream.onNext(queryLoaded(photos))

        actionsStream.onNext(loadingError("query", page = 2))

        result.assertLastValue { photos === photos && query == "query" && page == 2 }
    }

    private fun queryLoaded(photos: List<PhotoItem> = emptyList(),
                            query: String? = "") = QueryLoaded(photos, query)

    private fun pageLoaded(photos: List<PhotoItem> = emptyList(),
                           query: String? = "",
                           page: Int = 1) = PageLoaded(
            photos,
            query,
            page
    )

    private fun loadingError(query: String? = "",
                             page: Int = 1) = LoadingError(query, page)

    private fun query(query: String = "") = Query(query)

    private fun photoItem(id: Long = 0L, url: String = "") = PhotoItem(id, url)

    private fun photoListModel() = PhotoListModel(stateContainer)
}