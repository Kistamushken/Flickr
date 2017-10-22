package com.philuvarov.flickr.photos

import android.support.annotation.LayoutRes
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.philuvarov.flickr.R
import com.squareup.picasso.Picasso

class PhotosAdapter : RecyclerView.Adapter<PhotosAdapter.PhotoItemViewHolder>() {

    init {
        setHasStableIds(true)
    }

    var items = emptyList<PhotoItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoItemViewHolder {
        return PhotoItemViewHolder(inflateView(parent, R.layout.photo_item))
    }

    private fun inflateView(parent: ViewGroup, @LayoutRes resId: Int): View {
        return LayoutInflater.from(parent.context).inflate(resId, parent, false)
    }

    override fun onBindViewHolder(holder: PhotoItemViewHolder, position: Int) {
        bindPhotoView(holder, position)
    }

    override fun getItemId(position: Int): Long {
        return if (items.isEmpty()) 0 else items[position].id
    }

    private fun bindPhotoView(holder: PhotoItemViewHolder, position: Int) {
        holder.load(items[position].url)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class PhotoItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val image: ImageView = view.findViewById(R.id.image)

        fun load(url: String) {
            cancelPreviousLoad()
            Picasso.with(itemView.context)
                    .load(url)
                    .into(image)

        }

        private fun cancelPreviousLoad() {
            Picasso.with(itemView.context).cancelRequest(image)
        }
    }

}