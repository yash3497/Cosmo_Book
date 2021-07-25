package com.example.cosmobook.Service

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import ss.com.bannerslider.ImageLoadingService

class GlideImageLoadingService(private val context: Context): ImageLoadingService {

    override fun loadImage(url: String?, imageView: ImageView?) {
        if (imageView != null) {
            Glide.with(context).load(url).into(imageView)
        }
    }

    override fun loadImage(resource: Int, imageView: ImageView?) {
        if (imageView != null) {
            Glide.with(context).load(resource).into(imageView)
        }
    }

    override fun loadImage(
        url: String?,
        placeHolder: Int,
        errorDrawable: Int,
        imageView: ImageView?
    ) {
        if (imageView != null) {
            Glide.with(context).load(url).placeholder(placeHolder).error(errorDrawable).into(imageView)
        }
    }
}