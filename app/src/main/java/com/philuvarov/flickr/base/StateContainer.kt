package com.philuvarov.flickr.base

import kotlin.reflect.KProperty

interface StateContainer<VS: ViewState> {

    operator fun getValue(thisRef: Any?, property: KProperty<*>): VS

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: VS)

}