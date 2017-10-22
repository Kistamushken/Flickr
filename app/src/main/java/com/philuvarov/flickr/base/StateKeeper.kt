package com.philuvarov.flickr.base

import android.os.Bundle

interface StateKeeper {

    fun restore(savedState: Bundle?)

    fun save(outState: Bundle)

}