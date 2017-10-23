package com.philuvarov.flickr.photos

import android.app.Activity
import android.os.Bundle
import com.philuvarov.flickr.R
import com.philuvarov.flickr.base.Presenter
import com.philuvarov.flickr.base.StateKeeper
import dagger.android.AndroidInjection
import javax.inject.Inject

class PhotoListActivity : Activity() {

    @Inject lateinit var presenter: Presenter<PhotoScreenState, PhotoListView>

    @Inject lateinit var stateKeeper: StateKeeper

    private lateinit var view: PhotoListView

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        stateKeeper.restore(savedInstanceState)
        setContentView(R.layout.activity_photo_list)
        view = PhotoListViewImpl(findViewById(android.R.id.content))
    }

    override fun onStart() {
        super.onStart()
        presenter.bind(view)
    }

    override fun onStop() {
        presenter.unbind()
        super.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        stateKeeper.save(outState)
        super.onSaveInstanceState(outState)
    }

}