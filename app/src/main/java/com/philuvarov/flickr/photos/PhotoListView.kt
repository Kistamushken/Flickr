package com.philuvarov.flickr.photos

import android.support.design.widget.Snackbar
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.util.Log
import android.widget.Toast.LENGTH_SHORT
import com.jakewharton.rxbinding2.support.v7.widget.SearchViewQueryTextEvent
import com.jakewharton.rxbinding2.support.v7.widget.queryTextChangeEvents
import com.philuvarov.flickr.R
import com.philuvarov.flickr.R.id.root
import com.philuvarov.flickr.base.View
import com.philuvarov.flickr.base.View2
import com.philuvarov.flickr.photos.PhotoScreenAction.LoadMore
import com.philuvarov.flickr.photos.PhotoScreenAction.Query
import com.philuvarov.flickr.photos.PhotoScreenState.*
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.subjects.PublishSubject
import android.view.View as PlatformView

interface PhotoListView: View<PhotoScreenState> {
    fun loadMoreEvents(): Observable<Unit>
    fun loadInitialEvents(): Observable<Unit>
    fun querySubmissions(): Observable<SearchViewQueryTextEvent>
}

class PhotoListViewImpl(root: PlatformView) : PhotoListView {

    private val recycler = root.findViewById<RecyclerView>(R.id.recycler)
    private val searchView = root.findViewById<SearchView>(R.id.search)
    private val refreshLayout = root.findViewById<SwipeRefreshLayout>(R.id.swipe_refresh)
    private val photosAdapter = PhotosAdapter()

    init {
        refreshLayout.isEnabled = false
        refreshLayout.setProgressViewOffset(true,
                root.resources.getDimensionPixelSize(R.dimen.pull_refresh_offset_start),
                root.resources.getDimensionPixelSize(R.dimen.pull_refresh_offset_end))

        val lm = GridLayoutManager(recycler.context, 3)
        recycler.layoutManager = lm
    }

    override fun loadMoreEvents(): Observable<Unit> {
        return Observable.create<Unit> { e ->
            with(ScrollListener(e)) {
                recycler.addOnScrollListener(this)
                e.setCancellable { recycler.removeOnScrollListener(this) }
            }
        }
    }

    override fun render(state: PhotoScreenState) {
        Log.e("State rendered", "$state")
        when (state) {
            is Empty -> refreshLayout.isRefreshing = true
            is Loading -> refreshLayout.isRefreshing = true
            is Loaded ->  {
                refreshLayout.isRefreshing = false
                photosAdapter.items = state.photos
                if (recycler.adapter == null) {
                    recycler.adapter = photosAdapter
                }
                photosAdapter.notifyDataSetChanged()
            }
            is PhotoScreenState.Error -> {
                refreshLayout.isRefreshing = false
                Snackbar.make(recycler, R.string.error, LENGTH_SHORT).show()
            }
        }
    }

    override fun loadInitialEvents(): Observable<Unit> = Observable.just(Unit)

    override fun querySubmissions(): Observable<SearchViewQueryTextEvent> {
        return searchView.queryTextChangeEvents()
    }

    private inner class ScrollListener(
            private val emitter: ObservableEmitter<Unit>
    ) : RecyclerView.OnScrollListener() {

        override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(rv, dx, dy)
            if (dx == 0 && dy == 0) return

            val lm = rv.layoutManager as GridLayoutManager
            val totalItemCount = lm.itemCount - 1
            val lastVisibleItem = lm.findLastCompletelyVisibleItemPosition()
            if (!refreshLayout.isRefreshing && totalItemCount <= lastVisibleItem) {
                emitter.onNext(Unit)
            }
        }
    }

}

class PhotoListView2(root: PlatformView) : View2<PhotoScreenState, PhotoScreenAction> {

    private val recycler = root.findViewById<RecyclerView>(R.id.recycler)
    private val searchView = root.findViewById<SearchView>(R.id.search)
    private val refreshLayout = root.findViewById<SwipeRefreshLayout>(R.id.swipe_refresh)
    private val photosAdapter = PhotosAdapter()

    init {
        refreshLayout.isEnabled = false
        refreshLayout.setProgressViewOffset(true,
                root.resources.getDimensionPixelSize(R.dimen.pull_refresh_offset_start),
                root.resources.getDimensionPixelSize(R.dimen.pull_refresh_offset_end))

        val lm = GridLayoutManager(recycler.context, 3)
        recycler.layoutManager = lm
        val s: PublishSubject<Int> = PublishSubject.create()
        Observable.just(1).subscribeWith(s)
    }

    override fun intents(): Observable<PhotoScreenAction> {
        return Observable.merge(
                loadMoreEvents(),
                loadInitialEvents(),
                querySubmissions()
        )
    }

    private fun loadMoreEvents(): Observable<LoadMore> {
        return Observable.create<LoadMore> { e ->
            with(ScrollListener(e)) {
                recycler.addOnScrollListener(this)
                e.setCancellable { recycler.removeOnScrollListener(this) }
            }
        }
    }

    private fun loadInitialEvents(): Observable<PhotoScreenAction.Initial> = Observable.just(PhotoScreenAction.Initial())

    private fun querySubmissions(): Observable<Query> {
        return searchView
                .queryTextChangeEvents()
                .filter{it.isSubmitted}
                .map { Query(it.queryText().toString()) }
    }

    override fun render(state: PhotoScreenState) {
        Log.e("State rendered", "$state")
        when (state) {
            is Empty -> refreshLayout.isRefreshing = true
            is Loading -> refreshLayout.isRefreshing = true
            is Loaded ->  {
                refreshLayout.isRefreshing = false
                photosAdapter.items = state.photos
                if (recycler.adapter == null) {
                    recycler.adapter = photosAdapter
                }
                photosAdapter.notifyDataSetChanged()
            }
            is PhotoScreenState.Error -> {
                refreshLayout.isRefreshing = false
                Snackbar.make(recycler, R.string.error, LENGTH_SHORT).show()
            }
        }
    }

    private inner class ScrollListener(
            private val emitter: ObservableEmitter<LoadMore>
    ) : RecyclerView.OnScrollListener() {

        override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(rv, dx, dy)
            if (dx == 0 && dy == 0) return

            val lm = rv.layoutManager as GridLayoutManager
            val totalItemCount = lm.itemCount - 1
            val lastVisibleItem = lm.findLastCompletelyVisibleItemPosition()
            if (!refreshLayout.isRefreshing && totalItemCount <= lastVisibleItem) {
                emitter.onNext(LoadMore())
            }
        }
    }

}