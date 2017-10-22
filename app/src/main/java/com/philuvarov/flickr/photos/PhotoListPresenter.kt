package com.philuvarov.flickr.photos

import com.philuvarov.flickr.base.Presenter
import com.philuvarov.flickr.base.UseCase
import com.philuvarov.flickr.photos.PhotoScreenAction.Initial
import com.philuvarov.flickr.photos.PhotoScreenAction.LoadMore
import com.philuvarov.flickr.photos.PhotoScreenAction.Query
import com.philuvarov.flickr.util.SchedulersProvider
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import javax.inject.Inject

@PhotosScope
class PhotoListPresenter @Inject constructor(
        private val useCase: UseCase<PhotoScreenAction, PhotoScreenState>,
        private val schedulers: SchedulersProvider
) : Presenter<PhotoScreenState, PhotoListView> {

    private val disposables = CompositeDisposable()

    override fun bind(view: PhotoListView) {
        disposables += Observable
                .merge(
                        view.loadInitialEvents()
                                .map { Initial() },
                        view.loadMoreEvents()
                                .map { LoadMore() },
                        view.querySubmissions()
                                .filter { it.isSubmitted }
                                .map { Query(it.queryText().toString()) }
                )
                .switchMap { useCase.handle(it).toObservable() }
                .observeOn(schedulers.mainThread())
                .subscribe({ view.render(it) })

    }

    override fun unbind() = disposables.clear()


}