package com.philuvarov.flickr.remote

import com.philuvarov.flickr.remote.model.PhotosResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface Api {

    @GET("?method=flickr.photos.getRecent")
    fun getRecent(@Query("page") page: Int): Single<PhotosResponse>

    @GET("?method=flickr.photos.search")
    fun searchPhotos(@Query("page") page: Int,
                              @Query("text") query: String): Single<PhotosResponse>

}