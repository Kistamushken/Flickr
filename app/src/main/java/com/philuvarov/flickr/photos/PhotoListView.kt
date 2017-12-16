package com.philuvarov.flickr.photos

import android.support.design.widget.Snackbar
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.util.Log
import android.widget.Toast
import com.jakewharton.rxbinding2.support.v7.widget.queryTextChangeEvents
import com.philuvarov.flickr.R
import com.philuvarov.flickr.base.View
import com.philuvarov.flickr.photos.PhotoScreenAction.*
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit
import android.view.View as PlatformView

class PhotoListView(root: PlatformView) : View<PhotoScreenState, PhotoScreenAction> {

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

    override fun intents(): Observable<PhotoScreenAction> {
        return Observable.merge(
                loadMoreEvents(),
                loadInitialEvents(),
                querySubmissions()
//                test()
        )
    }

    private fun test() = Observable.interval(2, 1, TimeUnit.SECONDS).doOnNext { Log.e("Tick", "$it")}.map { Test() }

    private fun loadMoreEvents(): Observable<LoadMore> {
        return Observable.create<LoadMore> { e ->
            with(ScrollListener(e)) {
                recycler.addOnScrollListener(this)
                e.setCancellable { recycler.removeOnScrollListener(this) }
            }
        }
    }

    private fun loadInitialEvents(): Observable<PhotoScreenAction.Initial> = Observable.just(Initial)

    private fun querySubmissions(): Observable<Query> {
        return searchView
                .queryTextChangeEvents()
                .filter{it.isSubmitted}
                .map { Query(it.queryText().toString()) }
    }

    override fun render(state: Observable<out PhotoScreenState>) {
        state
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete{
                    val x = 0
                    val z = 2
                }
                .doOnDispose {
                    val x = 0
                    val z = 2
                }
                .doOnTerminate{
                    val x = 0
                    val z = 2
                }
                .subscribe({
                    Log.e("State rendered", "$it")
                    when (it) {
                        is PhotoScreenState.Empty -> refreshLayout.isRefreshing = true
                        is PhotoScreenState.Loading -> refreshLayout.isRefreshing = true
                        is PhotoScreenState.Loaded ->  {
                            refreshLayout.isRefreshing = false
                            photosAdapter.items = it.photos
                            if (recycler.adapter == null) {
                                recycler.adapter = photosAdapter
                            }
                            photosAdapter.notifyDataSetChanged()
                        }
                        is PhotoScreenState.Error -> {
                            refreshLayout.isRefreshing = false
                            Snackbar.make(recycler, R.string.error, Toast.LENGTH_SHORT).show()
                        }
                    }
                },{
                    val x = 0
                    val z = 2
                })
    }

    override fun render(it: PhotoScreenState) {
        Log.e("State rendered", "$it")
        when (it) {
            is PhotoScreenState.Empty -> refreshLayout.isRefreshing = true
            is PhotoScreenState.Loading -> refreshLayout.isRefreshing = true
            is PhotoScreenState.Loaded -> {
                refreshLayout.isRefreshing = false
                photosAdapter.items = it.photos
                if (recycler.adapter == null) {
                    recycler.adapter = photosAdapter
                }
                photosAdapter.notifyDataSetChanged()
            }
            is PhotoScreenState.Error -> {
                refreshLayout.isRefreshing = false
                Snackbar.make(recycler, R.string.error, Toast.LENGTH_SHORT).show()
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
                emitter.onNext(LoadMore)
            }
        }
    }

}