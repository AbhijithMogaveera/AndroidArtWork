package com.abhijith.music.components.player.screens

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.doOnLayout
import com.abhijith.music.R
import com.abhijith.music.components.player.PanelType
import com.abhijith.music.components.player.RepeatMode
import com.abhijith.music.databinding.ControllerScreenBinding
import com.abhijith.music.state.MusicViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlin.math.roundToInt

/**
* Child of [com.abhijith.music.components.MainPanelLayout]
* */
@AndroidEntryPoint
class MusicControllerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) {

    lateinit var musicViewModel: MusicViewModel
    fun setMusicViewModel(musicViewModel: MusicViewModel, scope: CoroutineScope) {
        this.musicViewModel = musicViewModel
        musicViewModel.currentPlayerState().onEach { state ->
            when (state.repeatMode) {
                RepeatMode.NO_REPEAT -> {
                    binding.expandControlView.ivRepeat.alpha = 0.5f
                    binding.expandControlView.ivRepeat.setImageResource(R.drawable.ic_repeat)
                }

                RepeatMode.REPEAT -> {
                    binding.expandControlView.ivRepeat.alpha = 1f
                    binding.expandControlView.ivRepeat.setImageResource(R.drawable.ic_repeat)
                }
            }
        }.launchIn(scope)

        binding.ivMore.setOnClickListener {
            musicViewModel.requestDisplayPlayerMoreMenu()
        }
        binding.ivCollapse.setOnClickListener {
            musicViewModel.requestCollapsePrimaryPanel()
        }
        binding.expandControlView.also {
            it.ivPrevious.setOnClickListener { musicViewModel.requestPlayPreviousSong() }
            it.ivShuffle.setOnClickListener { musicViewModel.requestShuffle() }
            it.ivPause.setOnClickListener { musicViewModel.requestPauseCurrentSong() }
            it.ivNext.setOnClickListener { musicViewModel.requestPlayNextSong() }
            it.ivRepeat.setOnClickListener { musicViewModel.requestRepeat() }
        }
        binding.ivPause2.setOnClickListener { musicViewModel.requestPauseCurrentSong() }
        binding.ivNext2.setOnClickListener { musicViewModel.requestPlayNextSong() }

        val appState = musicViewModel.currentPlayerState()
        listOf(
            appState.map { it.primaryBSOffset to PanelType.PRIMARY_BOTTOM_SHEET }.distinctUntilChanged(),
            appState.map { it.secondaryBSOffset to PanelType.SECONDARY_BOTTOM_SHEET }.distinctUntilChanged()
        ).map {
            it.onEach { (slidingOffset, panelType) ->
                onSlidingOffsetChanged(slidingOffset, panelType)
            }
        }.let { flowList ->
            combine(flowList) {}
                .onStart {
                doOnLayout {
                    onSlidingOffsetChanged(0f, PanelType.PRIMARY_BOTTOM_SHEET)
                }
            }.launchIn(scope)
        }
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        return try {
            super.dispatchTouchEvent(event)
        } catch (_: Exception) {
            return true
        }
    }

    val binding: ControllerScreenBinding =
        ControllerScreenBinding.inflate(LayoutInflater.from(context), this)

    private val collapseViewSet: Set<View> =
        setOf(binding.ivNext2, binding.ivPause2, binding.tvSongDesc, binding.tvSongTitle)
    private val expandViewSet: Set<View> = setOf(binding.ivMore, binding.ivCollapse)

    init {
        (expandViewSet).forEach { it.visibility = View.GONE }
    }

    private val topInset get() = ViewCompat.getRootWindowInsets(this)?.getInsets(WindowInsetsCompat.Type.systemBars())?.top ?: 0
    private val bottomInsets get() = ViewCompat.getRootWindowInsets(this)?.getInsets(WindowInsetsCompat.Type.systemBars())?.bottom ?: 0

    private fun onSlidingOffsetChanged(
        offset: Float,
        panelType: PanelType,
    ) {
        updatePadding(offset,panelType)
        setVisibility(offset, panelType)
    }

    private fun setVisibility(
        offset: Float,
        panelType: PanelType
    ) {
        val offset = when (panelType) {
            PanelType.PRIMARY_BOTTOM_SHEET -> offset
            PanelType.SECONDARY_BOTTOM_SHEET -> 1 - offset
        }
        (expandViewSet).forEach {
            it.visibility = if (offset > 0.7f) VISIBLE else GONE
        }
        (collapseViewSet).forEach {
            it.visibility = if (offset < 0.4f) VISIBLE else GONE
        }
    }

    private fun updatePadding(slidingOffset: Float,panelType: PanelType) {
        if (panelType == PanelType.PRIMARY_BOTTOM_SHEET) {
            setPadding(
                paddingLeft,
                topInset * slidingOffset.roundToInt(),
                paddingRight,
                bottomInsets * slidingOffset.roundToInt()
            )
        }
    }

}
