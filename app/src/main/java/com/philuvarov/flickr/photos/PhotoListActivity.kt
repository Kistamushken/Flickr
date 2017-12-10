package com.philuvarov.flickr.photos

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.philuvarov.flickr.R
import com.philuvarov.flickr.base.Dispatcher
import dagger.android.AndroidInjection
import javax.inject.Inject

class PhotoListActivity : AppCompatActivity() {

    @Inject lateinit var dispatcher: Dispatcher<PhotoScreenState, PhotoScreenAction, PhotoListCommand>

    private lateinit var view: PhotoListView

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_list)
        view = PhotoListView(findViewById(android.R.id.content))
    }

    override fun onStart() {
        super.onStart()
        dispatcher.bind(view, this)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }

}