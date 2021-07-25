package com.example.cosmobook.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cosmobook.Adapter.ComicAdapter.MyViewHolder
import com.example.cosmobook.Model.Comic
import com.example.cosmobook.R
import com.example.cosmobook.ViewComicActivity
import com.example.cosmobook.databinding.ComicItemBinding

class ComicAdapter(internal var context: Context, internal var comiclist:List<Comic>):RecyclerView.Adapter<MyViewHolder>() {
    class MyViewHolder(itemView:View): RecyclerView.ViewHolder(itemView) {
        var binding: ComicItemBinding = ComicItemBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.comic_item,parent,false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
       var item = comiclist.get(position)
        holder.binding.comicName.text = item.comicname
        Glide.with(context).load(item.image).into(holder.binding.comicImage)
        holder.binding.comicItem.setOnClickListener {
            var intent = Intent (context,ViewComicActivity::class.java)
            intent.putExtra("title",holder.binding.comicName.text.toString())
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return comiclist.size
    }
}

