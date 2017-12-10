package com.philuvarov.flickr.photos

import android.util.Log
import com.philuvarov.flickr.base.Cmd
import com.philuvarov.flickr.base.UseCase
import com.philuvarov.flickr.photos.PhotoListCommand.LoadInitial
import com.philuvarov.flickr.photos.PhotoListCommand.LoadNextPage
import com.philuvarov.flickr.photos.PhotoListCommand.LoadQuery
import com.philuvarov.flickr.photos.PhotoListCommand.None
import com.philuvarov.flickr.photos.PhotoScreenAction.Initial
import com.philuvarov.flickr.photos.PhotoScreenAction.LoadMore
import com.philuvarov.flickr.photos.PhotoScreenAction.LoadingError
import com.philuvarov.flickr.photos.PhotoScreenAction.PageLoaded
import com.philuvarov.flickr.photos.PhotoScreenAction.Query
import com.philuvarov.flickr.photos.PhotoScreenAction.QueryLoaded
import com.philuvarov.flickr.photos.PhotoScreenState.Empty
import com.philuvarov.flickr.photos.PhotoScreenState.Error
import com.philuvarov.flickr.photos.PhotoScreenState.Loading
import io.reactivex.Observable
import javax.inject.Inject

@PhotosScope
class PhotoListUseCase @Inject constructor() : UseCase<PhotoScreenAction, PhotoScreenState, PhotoListCommand>() {

    override fun process(intents: Observable<PhotoScreenAction>): Observable<Pair<PhotoScreenState, PhotoListCommand>> {
        return intents
                .scan(Empty() as PhotoScreenState to None as PhotoListCommand) { (state, _), action -> reduce(state, action) }
                .replay(1)
                .publish()
                .autoConnect(0)
    }

    private fun reduce(oldState: PhotoScreenState, action: PhotoScreenAction): Pair<PhotoScreenState, PhotoListCommand> {
        Log.e("Reduce", "Lol")
        with(oldState) {
            return when (action) {
                is Initial -> {
                    when (this) {
                        is Empty -> toInitial()
                        is Error, is Loading -> if (photos.isEmpty()) toInitial() else toLoaded() to None
                        else -> toLoaded() to None
                    }
                }
                is Query -> toLoading() to LoadQuery(action.query)
                is LoadMore -> toLoading() to LoadNextPage(query, page + 1)
                is PageLoaded -> toLoaded(photos + action.photos, action.query, action.page) to None
                is QueryLoaded -> toLoaded(action.photos, action.query, 1) to None
                is LoadingError -> Error(photos, action.query, action.page) to None
            }
        }
    }


    private fun PhotoScreenState.toInitial() = toLoading() to LoadInitial

}

sealed class PhotoListCommand : Cmd {
    object None : PhotoListCommand()
    object LoadInitial : PhotoListCommand()
    class LoadQuery(val query: String) : PhotoListCommand()
    class LoadNextPage(val query: String?, val page: Int) : PhotoListCommand()
}