package com.example.gramotunes.ui.view

import android.animation.ValueAnimator
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.graphics.ColorUtils
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.example.gramotunes.R
import com.example.gramotunes.databinding.PlayerScreenBinding
import com.example.gramotunes.ui.viewmodel.MusicViewmodel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch

class PlayerBottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var binding: PlayerScreenBinding

    val musicViewmodel: MusicViewmodel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = PlayerScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                musicViewmodel.uiState.collect {
                    Glide.with(binding.albumImage)
                        .asBitmap()
                        .load(it.albumArt)
                        .placeholder(R.drawable.ic_gramatune_placeholder)
                        .error(R.drawable.ic_gramatune_placeholder)
                        .into(object : CustomTarget<Bitmap>() {

                            override fun onResourceReady(
                                resource: Bitmap,
                                transition: com.bumptech.glide.request.transition.Transition<in Bitmap>?
                            ) {
                                binding.albumImage.setImageBitmap(resource)
                                extractDominantColor(resource) { color ->
                                    animateMiniPlayerColor(color)
                                }                            }

                            override fun onLoadCleared(placeholder: Drawable?) {
                            }
                        })
                }
            }
        }

    }

    private fun animateMiniPlayerColor(targetColor: Int) {
        val layout = binding.playerRoot  // ConstraintLayout

        val currentColor = (layout.background as? ColorDrawable)?.color
            ?: Color.TRANSPARENT

        val mixedColor = ColorUtils.blendARGB(
            targetColor,
            Color.BLACK,
            0.4f // adjust darkness
        )

        ValueAnimator.ofArgb(currentColor, mixedColor).apply {
            duration = 400
            addUpdateListener { animator ->
                layout.setBackgroundColor(animator.animatedValue as Int)
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

    override fun onStart() {
        super.onStart()

        val dialog = dialog as BottomSheetDialog?
        dialog?.let { it ->
            val bottomSheet =
                it.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let {
                val behavior = BottomSheetBehavior.from(it)

                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.peekHeight = 0
                behavior.isHideable = true
                behavior.skipCollapsed = true

                val layoutParams = it.layoutParams as CoordinatorLayout.LayoutParams
                layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
                it.layoutParams = layoutParams

                behavior.isDraggable = true
            }
        }
    }
}