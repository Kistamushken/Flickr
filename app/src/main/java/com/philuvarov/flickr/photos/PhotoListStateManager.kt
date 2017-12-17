package com.philuvarov.flickr.photos

import android.os.Bundle
import com.philuvarov.flickr.base.StateContainer
import com.philuvarov.flickr.base.StateKeeper
import com.philuvarov.flickr.photos.PhotoScreenState.Empty
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.reflect.KProperty

@Singleton
class PhotoListStateManager @Inject constructor() :
        StateContainer<PhotoScreenState>,
        StateKeeper {

    private var state: PhotoScreenState = Empty()

    override fun restore(savedState: Bundle?) {
        state = savedState?.getParcelable(KEY_PHOTOS_STATE) ?: state
    }

    override fun save(outState: Bundle) {
        outState.putParcelable(KEY_PHOTOS_STATE, state)
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): PhotoScreenState {
        return state
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: PhotoScreenState) {
        state = value
    }
}

private const val KEY_PHOTOS_STATE = "key_photos_state"