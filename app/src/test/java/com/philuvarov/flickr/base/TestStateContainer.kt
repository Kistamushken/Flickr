package com.philuvarov.flickr.base

import kotlin.reflect.KProperty

class TestStateContainer<VS : ViewState>(var state: VS) : StateContainer<VS> {

    override fun getValue(thisRef: Any?, property: KProperty<*>) = state

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: VS) {
        this.state = value
    }
}