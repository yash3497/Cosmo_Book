package com.example.cosmobook.Adapter

import ss.com.bannerslider.adapters.SliderAdapter
import ss.com.bannerslider.viewholder.ImageSlideViewHolder

class SliderAdapter(private val bannerList:List<String>):SliderAdapter() {
    override fun getItemCount(): Int {
        return bannerList.size
    }

    override fun onBindImageSlide(position: Int, imageSlideViewHolder: ImageSlideViewHolder?) {
        imageSlideViewHolder!!.bindImageSlide(bannerList[position])
    }
}