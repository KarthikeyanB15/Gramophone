package com.example.gramotunes

import android.Manifest
import android.animation.ValueAnimator
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.graphics.ColorUtils
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.example.gramotunes.databinding.ActivityMainBinding
import com.example.gramotunes.ui.view.PlayerBottomSheetFragment
import com.example.gramotunes.ui.viewmodel.MusicViewmodel
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    val musicViewmodel: MusicViewmodel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }
        getStoragePermission()

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHost) as NavHostFragment

        val navController = navHostFragment.navController

        findViewById<BottomNavigationView>(R.id.bottomNav)
            .setupWithNavController(navController)

        binding.playerBottomSheet.playerSheet.setOnClickListener {
            val playerBottomSheetFragment = PlayerBottomSheetFragment()
            playerBottomSheetFragment.show(supportFragmentManager, playerBottomSheetFragment.tag)
        }
        binding.playerBottomSheet.tvTitle.isSelected = true
        binding.playerBottomSheet.tvArtist.isSelected = true
        binding.playerBottomSheet.btnPause.setOnClickListener {
            musicViewmodel.pause()
            binding.playerBottomSheet.btnPause.visibility = View.GONE
            binding.playerBottomSheet.btnPlayPause.visibility = View.VISIBLE
        }

        binding.playerBottomSheet.btnPlayPause.setOnClickListener {
            musicViewmodel.resume()
            binding.playerBottomSheet.btnPause.visibility = View.VISIBLE
            binding.playerBottomSheet.btnPlayPause.visibility = View.GONE

        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                musicViewmodel.uiState.collect {
                    Log.d("MusicPlayer", "called")
                    binding.playerBottomSheet.tvTitle.text = it.title
                    binding.playerBottomSheet.tvArtist.text = it.artist
                    Glide.with(binding.playerBottomSheet.ivMiniArt)
                        .asBitmap()
                        .load(it.albumArt)
                        .placeholder(R.drawable.ic_gramatune_placeholder)
                        .error(R.drawable.ic_gramatune_placeholder)
                        .into(object : CustomTarget<Bitmap>() {

                            override fun onResourceReady(
                                resource: Bitmap,
                                transition: com.bumptech.glide.request.transition.Transition<in Bitmap>?
                            ) {
                                binding.playerBottomSheet.ivMiniArt.setImageBitmap(resource)
                                extractDominantColor(resource) { color ->
                                    animateMiniPlayerColor(color)
                                }
                            }

                            override fun onLoadCleared(placeholder: Drawable?) {
                            }
                        })
                }
            }
        }
    }


    private fun animateMiniPlayerColor(targetColor: Int) {
        val card = binding.playerBottomSheet.playerSheet
        val currentColor = card.cardBackgroundColor.defaultColor

        val mixedColor = ColorUtils.blendARGB(
            targetColor,
            Color.BLACK,
            0.4f
        )

        ValueAnimator.ofArgb(currentColor, mixedColor).apply {
            duration = 400
            addUpdateListener {
                card.setCardBackgroundColor(it.animatedValue as Int)
            }
            start()
        }
    }


    fun extractDominantColor(
        bitmap: Bitmap,
        onColorReady: (Int) -> Unit
    ) {
        Palette.from(bitmap).generate { palette ->
            val color = palette?.getVibrantColor(
                palette.getDominantColor(Color.BLACK)
            ) ?: Color.BLACK
            onColorReady(color)
        }
    }


    fun getStoragePermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                if (Build.VERSION.SDK_INT >= 33)
                    Manifest.permission.READ_MEDIA_AUDIO
                else
                    Manifest.permission.READ_EXTERNAL_STORAGE
            ),
            100
        )
    }

    override fun onDestroy() {
        super.onDestroy()
    }

}
