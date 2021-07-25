package com.example.cosmobook.Interface

import com.example.cosmobook.Model.Comic

interface ComicLoadListener {
    fun OnComicLoadListener(comic:List<Comic>)
}