//package com.philuvarov.flickr.photos
//
//import com.jakewharton.rxbinding2.support.v7.widget.SearchViewQueryTextEvent
//import com.nhaarman.mockito_kotlin.any
//import com.nhaarman.mockito_kotlin.argumentCaptor
//import com.nhaarman.mockito_kotlin.mock
//import com.nhaarman.mockito_kotlin.never
//import com.nhaarman.mockito_kotlin.verify
//import com.nhaarman.mockito_kotlin.whenever
//import com.philuvarov.flickr.Is
//import com.philuvarov.flickr.base.Model
//import com.philuvarov.flickr.util.TestSchedulersProvider
//import io.reactivex.Flowable
//import io.reactivex.subjects.PublishSubject
//import org.hamcrest.CoreMatchers.instanceOf
//import org.junit.Assert.assertThat
//import org.junit.Before
//import org.junit.Rule
//import org.junit.Test
//import org.mockito.Mock
//import org.mockito.junit.MockitoJUnit
//import org.mockito.junit.MockitoRule
//
//@Suppress("IllegalIdentifier")
//class PhotoListPresenterTest {
//    @Rule
//    @JvmField
//    val rule: MockitoRule = MockitoJUnit.rule()
//
//    @Mock private lateinit var useCase: Model<PhotoScreenAction, PhotoScreenState>
//    @Mock private lateinit var view: PhotoListView
//
//    private lateinit var presenter: PhotoListPresenter
//
//    private val initialLoadEvents = PublishSubject.create<Unit>()
//    private val loadMoreEvents = PublishSubject.create<Unit>()
//    private val queryEvents = PublishSubject.create<SearchViewQueryTextEvent>()
//
//
//    @Before
//    fun setUp() {
//        presenter = presenter()
//        whenever(view.loadInitialEvents()).thenReturn(initialLoadEvents)
//        whenever(view.querySubmissions()).thenReturn(queryEvents)
//        whenever(view.loadMoreEvents()).thenReturn(loadMoreEvents)
//        whenever(useCase.handle(any())).thenReturn(Flowable.empty())
//    }
//
//    @Test
//    fun `bind - subscribes to view streams - always`() {
//        presenter.bind(view)
//
//        assertThat(initialLoadEvents.hasObservers(), Is(true))
//        assertThat(loadMoreEvents.hasObservers(), Is(true))
//        assertThat(queryEvents.hasObservers(), Is(true))
//    }
//
//    @Test
//    fun `initial event - maps to Initial action - always`() {
//        presenter.bind(view)
//
//        initialLoadEvents.onNext(Unit)
//
//        verify(useCase).handle(any<PhotoScreenAction.Initial>())
//    }
//
//    @Test
//    fun `load more event - maps to LoadMore action - always`() {
//        presenter.bind(view)
//
//        loadMoreEvents.onNext(Unit)
//
//        verify(useCase).handle(any<PhotoScreenAction.LoadMore>())
//    }
//
//    @Test
//    fun `query event - maps to Query action - query is submitted`() {
//        presenter.bind(view)
//
//        queryEvents.onNext(searchEvent("query", isSubmitted = true))
//
//        val action = captureAction()
//        assertThat(action, instanceOf(PhotoScreenAction.Query::class.java))
//        action as PhotoScreenAction.Query
//        assertThat(action.query, Is("query"))
//    }
//
//    @Test
//    fun `query event - does not map to Query action - query is not submitted`() {
//        presenter.bind(view)
//
//        queryEvents.onNext(searchEvent("query", isSubmitted = false))
//
//        verify(useCase, never()).handle(any())
//    }
//
//    @Test
//    fun `unbind - unsubscribes from view events - always`() {
//        presenter.bind(view)
//
//        presenter.unbind()
//
//        assertThat(initialLoadEvents.hasObservers(), Is(false))
//        assertThat(loadMoreEvents.hasObservers(), Is(false))
//        assertThat(queryEvents.hasObservers(), Is(false))
//    }
//
//    private fun searchEvent(query: String = "", isSubmitted: Boolean = false) = SearchViewQueryTextEvent.create(mock(), query, isSubmitted)
//
//    private fun captureAction(): PhotoScreenAction {
//        val captor = argumentCaptor<PhotoScreenAction>()
//        verify(useCase).handle(captor.capture())
//        return captor.firstValue
//    }
//
//    private fun presenter() = PhotoListPresenter(
//            useCase,
//            TestSchedulersProvider()
//    )
//}