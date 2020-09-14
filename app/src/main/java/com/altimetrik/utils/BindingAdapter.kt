package com.altimetrik.utils

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.altimetrik.itunes.R
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions

@BindingAdapter("imageUrl")
fun imageUrl(view: ImageView, imageUrl: String?) {
    Glide.with(view.context)
        .load(imageUrl).apply(RequestOptions())
        .placeholder(R.drawable.ic_launcher_background)
        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
        .skipMemoryCache(true)
        .into(view)
}