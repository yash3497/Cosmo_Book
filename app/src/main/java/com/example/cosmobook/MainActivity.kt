package com.example.cosmobook

import android.os.Bundle
import android.os.Handler
import android.provider.Settings

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.cosmobook.Adapter.ComicAdapter
import com.example.cosmobook.Adapter.SliderAdapter
import com.example.cosmobook.Common.common
import com.example.cosmobook.Interface.BannerLoadDoneListener
import com.example.cosmobook.Interface.ComicLoadListener
import com.example.cosmobook.Model.Comic
import com.example.cosmobook.Service.GlideImageLoadingService
import com.example.cosmobook.databinding.ActivityMainBinding
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.FirebaseFirestore
import dmax.dialog.SpotsDialog
import ss.com.bannerslider.Slider
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity(), BannerLoadDoneListener, ComicLoadListener {
    private lateinit var binding: ActivityMainBinding
    private var mInterstitialAd: InterstitialAd? = null

    //listener
    lateinit var Bannerlistener:BannerLoadDoneListener
    lateinit var comiclistener:ComicLoadListener

    //Alert Dialog
    lateinit var alertDialog: android.app.AlertDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //init listener
        Bannerlistener = this
        comiclistener = this

        //alert dialog
        alertDialog = SpotsDialog.Builder().setContext(this)
            .setCancelable(false)
            .setMessage("Please Wait...")
            .build()

        binding.SwipeToRefresh.setColorSchemeResources(R.color.design_default_color_primary,R.color.design_default_color_primary_dark)
        binding.SwipeToRefresh.setOnRefreshListener {
            loadBanner()
            loadComic()
        }
        binding.SwipeToRefresh.post {
            loadBanner()
            loadComic()
        }
        Slider.init(GlideImageLoadingService(this))

        val spanCount:Int = 2
        val spacing:Int = 20
        val includeEdge:Boolean = false

        binding.recyclerviewComic.setHasFixedSize(true)
        binding.recyclerviewComic.layoutManager = GridLayoutManager(this,2)
        binding.recyclerviewComic.addItemDecoration(GridSpacingItemDecoration(spanCount,spacing,includeEdge))


        MobileAds.initialize(this) {}

            var adRequest = AdRequest.Builder().build()


            InterstitialAd.load(this@MainActivity,"ca-app-pub-7136996864447431/5601012869", adRequest, object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d("error", adError?.message)
                    mInterstitialAd = null
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    Log.d("adds", "Ad was loaded.")
                    mInterstitialAd = interstitialAd
                }
            })
            Handler().postDelayed(Runnable {
                if (mInterstitialAd != null) {
                    mInterstitialAd?.show(this@MainActivity)
                } else {
                    Log.d("TAG", "The interstitial ad wasn't ready yet.")
                }
            },15000)

    }

    private fun loadComic() {
        alertDialog.show()
        var comic_load: MutableList<Comic> = ArrayList<Comic>()
        FirebaseFirestore.getInstance()
            .collection("Comic")
            .get()
            .addOnCompleteListener(OnCompleteListener { task ->
                if (task.isSuccessful){
                    var snapshot = task.result?.documents
                    if (snapshot != null) {
                        for (list in snapshot){
                            var comic = Comic()
                            comic.comicname = list["title"] as String?
                            comic.image = list["image"] as String?
                            Log.d("name",comic.comicname.toString())
                            comic_load.add(comic)
                        }
                        comiclistener.OnComicLoadListener(comic_load)
                    }
                }
            })
    }

    private fun loadBanner() {
        FirebaseFirestore.getInstance()
            .collection("Banner")
            .document("image")
            .get()
            .addOnCompleteListener(OnCompleteListener { task ->
                if (task.isSuccessful){
                    val snapshot = task.result?.data
                    if (snapshot != null) {
                        Bannerlistener.onBannerLoadDoneListener(snapshot["banner"] as List<String>)
                    }
                }
            })
    }

    override fun onBannerLoadDoneListener(banners: List<String>) {
        binding.slider.setAdapter(SliderAdapter(banners))
    }

    override fun OnComicLoadListener(comic: List<Comic>) {
        alertDialog.dismiss()
        common.comic = comic
        binding.recyclerviewComic.adapter = ComicAdapter(this,comic)
        binding.txtComic.text = "NEW COMIC ("+comic.size+")"
        if (binding.SwipeToRefresh.isRefreshing){
            binding.SwipeToRefresh.isRefreshing = false
        }
    }

}

