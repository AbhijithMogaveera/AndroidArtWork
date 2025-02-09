package com.abhijith.customviewcollection

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.ui.Modifier
import androidx.navigation.activity
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.abhijith.customviewcollection.navigation.SimpleRoutes
import com.abhijith.customviewcollection.screens.ClockView
import com.abhijith.customviewcollection.screens.CylinderGraphComp
import com.abhijith.customviewcollection.screens.MainMenu
import com.abhijith.customviewcollection.ui.theme.CustomViewCollectionTheme
import com.abhijith.music.YTMusicActivity
import dagger.hilt.android.AndroidEntryPoint

@ExperimentalMaterialApi
@AndroidEntryPoint
class MenuActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CustomViewCollectionTheme {
                val navController = rememberNavController()
                Scaffold (
                    modifier = Modifier.fillMaxSize().background(MaterialTheme.colors.background).systemBarsPadding(),
                ) {
                    NavHost(navController = navController, startDestination = SimpleRoutes.MENU_SCREEN, modifier = Modifier.padding(it)) {
                        composable(SimpleRoutes.MENU_SCREEN) {
                            MainMenu {
                                navController.navigate(it)
                            }
                        }

                        composable(SimpleRoutes.CLOCK_SCREEN) {
                            ClockView()
                        }

                        composable(SimpleRoutes.CYLINDER_SCREEN) {
                            CylinderGraphComp()
                        }

                        activity(
                            route = SimpleRoutes.YT_MUSIC_SCREEN,
                            builder = { this.activityClass = YTMusicActivity::class }
                        )
                    }

                }

            }

        }

    }
}

