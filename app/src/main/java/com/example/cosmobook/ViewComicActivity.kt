package com.example.cosmobook

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import com.example.cosmobook.Adapter.ViewPagerAdapter
import com.example.cosmobook.databinding.ActivityViewComicBinding
import com.google.android.gms.ads.*
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.FirebaseFirestore
import com.wajahatkarim3.easyflipviewpager.BookFlipPageTransformer
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ViewComicActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewComicBinding
    private var mRewardedAd: RewardedAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewComicBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var title = intent.getStringExtra("title")

        binding.comicTitle.text = title


        MobileAds.initialize(this) {}

        val adRequest = AdRequest.Builder().build()

        RewardedAd.load(this,"ca-app-pub-7136996864447431/7484654769", adRequest, object : RewardedAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.d("error", adError.message)
                mRewardedAd = null
            }

            override fun onAdLoaded(rewardedAd: RewardedAd) {
                Log.d("message", "Ad was loaded.")
                mRewardedAd = rewardedAd
                Log.d("mreward",mRewardedAd.toString())
            }
        })
        if (title != null) {
            fetchlink(title)
        }
       Handler().postDelayed(Runnable {
           load()
       },50000)
    }

    private fun fetchlink(title: String) {
        FirebaseFirestore.getInstance()
            .collection("Comic")
            .whereEqualTo("title", title)
            .get()
            .addOnCompleteListener(OnCompleteListener {
                if (it.isSuccessful) {
                    var snap = it.result?.documents
                    if (snap != null) {
                        for (snapshot in snap) {
                            val list:List<String> = snapshot ["link"] as List<String>
                            var adapter = ViewPagerAdapter(this, list)
                            binding.viewPager.adapter = adapter

                            //create Book flip anim
                            val bookFlipPageTransformer = BookFlipPageTransformer()
                            bookFlipPageTransformer.scaleAmountPercent = 10f
                            binding.viewPager.setPageTransformer(true, bookFlipPageTransformer)

                        }
                    }
                }
            })
    }
private fun load(){
    if (mRewardedAd != null) {
        mRewardedAd?.show(this, OnUserEarnedRewardListener() {
            fun onUserEarnedReward(rewardItem: RewardItem) {
                var rewardAmount = rewardItem.amount
                var rewardType = rewardItem.getType()
                Log.d("tag", "User earned the reward.")
            }
        })
    } else {
        Log.d("rew", "The rewarded ad wasn't ready yet.")
    }
}

    override fun onBackPressed() {
        super.onBackPressed()
        load()
        finish()
    }
}