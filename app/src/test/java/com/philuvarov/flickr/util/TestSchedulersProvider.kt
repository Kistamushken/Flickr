package com.philuvarov.flickr.util

import io.reactivex.schedulers.Schedulers

class TestSchedulersProvider : SchedulersProvider {

    override fun trampoline() = Schedulers.trampoline()

    override fun computation() = Schedulers.trampoline()

    override fun io() = Schedulers.trampoline()

    override fun mainThread() = Schedulers.trampoline()
}