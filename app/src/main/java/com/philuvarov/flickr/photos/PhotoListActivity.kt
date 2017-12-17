package com.philuvarov.flickr.photos

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.philuvarov.flickr.R
import com.philuvarov.flickr.base.Dispatcher
import com.philuvarov.flickr.base.StateKeeper
import dagger.android.AndroidInjection
import javax.inject.Inject

class PhotoListActivity : AppCompatActivity() {

    @Inject lateinit var dispatcher: Dispatcher<PhotoScreenState, PhotoScreenMessage>

    @Inject lateinit var stateManager: StateKeeper

    private lateinit var view: PhotoListView

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_list)
        stateManager.restore(savedInstanceState)
        view = PhotoListView(findViewById(android.R.id.content))
        dispatcher.bind(view, this)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        stateManager.save(outState)
        super.onSaveInstanceState(outState)
    }

}