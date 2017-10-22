package com.philuvarov.flickr.base

interface Converter<in T, out R> {

    fun convert(value: T): R

}