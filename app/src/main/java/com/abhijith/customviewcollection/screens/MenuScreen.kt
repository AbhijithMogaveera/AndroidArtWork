package com.abhijith.customviewcollection.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.abhijith.customviewcollection.navigation.SimpleRoutes

@ExperimentalMaterialApi
@Composable
fun MainMenu(
    onRouteSelection:(String)->Unit
) {
    Column(modifier = Modifier) {
        MenuItem("CLock 🕑") {
            onRouteSelection(SimpleRoutes.CLOCK_SCREEN)
        }
        MenuItem("CylinderGraph 📊") {
            onRouteSelection(SimpleRoutes.CYLINDER_SCREEN)
        }

        MenuItem("Youtube Music Layout Clone 🎧") {
            onRouteSelection(SimpleRoutes.YT_MUSIC_SCREEN)
        }
    }
}

@ExperimentalMaterialApi
@Composable
private fun MenuItem(
    name: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Text(
            text = name, modifier = Modifier.padding(20.dp)
        )
    }
}