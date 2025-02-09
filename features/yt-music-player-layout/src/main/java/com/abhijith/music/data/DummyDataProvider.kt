package com.abhijith.music.data

import com.abhijith.music.R
import com.abhijith.music.models.AlbumViewData
import com.abhijith.music.models.AlbumsViewData
import com.abhijith.music.models.HeaderViewData
import com.abhijith.music.models.ViewData
import com.abhijith.music.models.SongViewData
import com.abhijith.music.models.SongsViewData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DummyDataProvider @Inject constructor(){

    val images = listOf(
        R.drawable.img_1,
        R.drawable.img_2,
        R.drawable.img_3,
        R.drawable.img_4,
        R.drawable.img_5,
        R.drawable.img_6,
    )
    enum class UIFlowContext {
        HOME_SCREEN,
        PLAYER_UP_NEXT,
        PLAYER_RELATED
    }

    fun getSongsSuggestions(
        uiFlowContext: UIFlowContext
    ): Flow<List<ViewData>> = flow {
        when (uiFlowContext) {
            UIFlowContext.HOME_SCREEN -> emit(getHomeScreenSuggestions())
            UIFlowContext.PLAYER_UP_NEXT -> emit(getHomeScreenSuggestions())
            UIFlowContext.PLAYER_RELATED -> emit(getHomeScreenSuggestions())
        }
    }

    private fun getHomeScreenSuggestions(): List<ViewData> {
        return buildList {
            repeat(3) {
                add(
                    HeaderViewData(
                        title = "Speed dial",
                        subtitle = "Lorem ipsum",
                        iconRight = null,
                        iconLeft = images.random()
                    )
                )
                add(
                    SongsViewData(
                        songViewData = listOf(
                            SongViewData(
                                id = "0",
                                image = R.drawable.img_1,
                                name = "Lore ipsum",
                                description = "Lorem ipsum dolor sit amet"
                            ),SongViewData(
                                id = "1",
                                image = R.drawable.img_2,
                                name = "Lore ipsum",
                                description = "Lorem ipsum dolor sit amet"
                            ),SongViewData(
                                id = "2",
                                image = R.drawable.img_3,
                                name = "Lore ipsum",
                                description = "Lorem ipsum dolor sit amet"
                            )
                        )
                    )
                )
                add(
                    HeaderViewData(
                        title = "Speed dial",
                        subtitle = "Lorem ipsum",
                        iconRight = null,
                        iconLeft = images.random()
                    )
                )
                add(
                    AlbumsViewData(
                        albumViewData = listOf(
                            AlbumViewData(
                                id = "0",
                                artistName = "A",
                                bannerImage = images[0]
                            ),
                            AlbumViewData(
                                id = "1",
                                artistName = "A",
                                bannerImage = images[1]
                            ),
                            AlbumViewData(
                                id = "2",
                                artistName = "A",
                                bannerImage = images[2]
                            ),
                            AlbumViewData(
                                id = "3",
                                artistName = "A",
                                bannerImage = images[3]
                            ),
                            AlbumViewData(
                                id = "4",
                                artistName = "A",
                                bannerImage = images[4]
                            ),
                            AlbumViewData(
                                id = "5",
                                artistName = "A",
                                bannerImage = images[5]
                            ),
                            AlbumViewData(
                                id = "6",
                                artistName = "A",
                                bannerImage = images[0]
                            ),
                            AlbumViewData(
                                id = "7",
                                artistName = "A",
                                bannerImage = images[1]
                            ),
                        ).shuffled()
                    )
                )
            }
        }
    }

}