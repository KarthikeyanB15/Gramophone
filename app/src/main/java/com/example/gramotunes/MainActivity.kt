package com.example.gramotunes

import android.Manifest
import android.animation.ValueAnimator
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
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
import com.example.gramotunes.utils.MusicUtils
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    val musicViewmodel: MusicViewmodel by viewModels()

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                musicViewmodel.loadMusic()
            }
        }


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

        binding.miniPlayerCard.playerSheet.setOnClickListener {
            val miniPlayerCardFragment = PlayerBottomSheetFragment()
            miniPlayerCardFragment.show(supportFragmentManager, miniPlayerCardFragment.tag)
        }
        binding.miniPlayerCard.tvTitle.isSelected = true
        binding.miniPlayerCard.tvArtist.isSelected = true
        binding.miniPlayerCard.btnPause.setOnClickListener {
            musicViewmodel.pause()
            binding.miniPlayerCard.btnPause.visibility = View.GONE
            binding.miniPlayerCard.btnPlayPause.visibility = View.VISIBLE
        }

        binding.miniPlayerCard.btnPlayPause.setOnClickListener {
            musicViewmodel.resume()
            binding.miniPlayerCard.btnPause.visibility = View.VISIBLE
            binding.miniPlayerCard.btnPlayPause.visibility = View.GONE
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                musicViewmodel.uiState.collect {
                    binding.miniPlayerCard.tvTitle.text = it.title
                    binding.miniPlayerCard.tvArtist.text = it.artist
                    Glide.with(binding.miniPlayerCard.ivMiniArt)
                        .asBitmap()
                        .load(MusicUtils.getAlbumArtUri(it.albumId))
                        .placeholder(R.drawable.ic_gramatune_placeholder)
                        .error(R.drawable.ic_gramatune_placeholder)
                        .into(object : CustomTarget<Bitmap>() {

                            override fun onResourceReady(
                                resource: Bitmap,
                                transition: com.bumptech.glide.request.transition.Transition<in Bitmap>?
                            ) {
                                binding.miniPlayerCard.ivMiniArt.setImageBitmap(resource)
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

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                musicViewmodel.showMiniPlayer.collect { show ->
                    if (show && binding.miniPlayerCard.playerSheet.visibility != View.VISIBLE) {
                        showMiniPlayer()
                    }
                }
            }
        }
    }

    private fun showMiniPlayer() {
        val view = binding.miniPlayerCard.playerSheet

        view.measure(
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )

        val height = view.measuredHeight.toFloat()

        view.translationY = height
        view.alpha = 0f
        view.visibility = View.VISIBLE

        view.animate()
            .translationY(0f)
            .alpha(1f)
            .setDuration(300)
            .setInterpolator(DecelerateInterpolator())
            .start()
    }

    private fun animateMiniPlayerColor(targetColor: Int) {
        val card = binding.miniPlayerCard.playerSheet
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


    private fun getStoragePermission() {
        val permission =
            if (Build.VERSION.SDK_INT >= 33)
                Manifest.permission.READ_MEDIA_AUDIO
            else
                Manifest.permission.READ_EXTERNAL_STORAGE

        permissionLauncher.launch(permission)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

}
