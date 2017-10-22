package com.philuvarov.flickr.util

import dagger.Reusable
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

interface SchedulersProvider  {

    fun trampoline(): Scheduler

    fun computation(): Scheduler

    fun io(): Scheduler

    fun mainThread(): Scheduler

}

@Reusable
class SchedulersProviderImpl @Inject constructor(): SchedulersProvider {

    override fun trampoline() = Schedulers.trampoline()

    override fun computation() = Schedulers.computation()

    override fun io() = Schedulers.io()

    override fun mainThread() = AndroidSchedulers.mainThread()
}