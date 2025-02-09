package com.abhijith.music

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.abhijith.music.components.BottomNavigationFragment
import com.abhijith.music.databinding.AppActivityLayoutBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class YTMusicActivity : AppCompatActivity() {

    lateinit var binding: AppActivityLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        binding = AppActivityLayoutBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        replaceViewWithBottomNavigationFragment()
    }

    private fun replaceViewWithBottomNavigationFragment() {
        supportFragmentManager
            .beginTransaction()
            .apply {
                replace(
                    binding.root.id,
                    BottomNavigationFragment::class.java,
                    null
                )
            }.commitNow()
    }
}