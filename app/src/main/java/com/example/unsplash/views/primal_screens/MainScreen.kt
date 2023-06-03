package com.example.unsplash.views.primal_screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.example.unsplash.R
import com.example.unsplash.views.main_screens.UserScreen
import com.example.unsplash.views.main_screens.CollectionScreen
import com.example.unsplash.views.main_screens.OpenScreen
import com.example.unsplash.views.main_screens.PhotoScreen
import com.example.unsplash.views.main_screens.SearchScreen

@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun MainScreen() {
    var currentScreen by remember { mutableStateOf("photos") }
    var colorsOfBottomMenu = mutableListOf(false, true, true, true,true)

    fun doTheColor(index : Int) : Color {
        return if (colorsOfBottomMenu[index]) Color.Gray else Color.Black
    }

    BoxWithConstraints {
        val maxHeight = maxHeight
        Column {
            Box(
                modifier = Modifier
                    .height(maxHeight * 0.93f)
            )
            {
                when (currentScreen) {
                    "photos" -> PhotoScreen()
                    "collection" -> CollectionScreen()
                    "user" -> UserScreen()
                    "open" -> OpenScreen()
                    "search" -> SearchScreen()

                }
            }
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(maxHeight * 0.1f)
            ) {

                Box(
                    modifier = Modifier
                        .weight(1f, true)
                        .clickable
                        {
                            currentScreen = "photos"
                            colorsOfBottomMenu = mutableListOf(false, true, true, true,true)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.photo),
                            contentDescription = "photos",
                            tint = doTheColor(0)
                        )
                        Text(
                            color = doTheColor(0),
                            text = "photos"
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .weight(1f, true)
                        .clickable {
                            currentScreen = "collection"
                            colorsOfBottomMenu = mutableListOf(true, false, true, true,true)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.collections),
                            contentDescription = "collections",
                            tint = doTheColor(1)
                        )
                        Text(
                            color = doTheColor(1),
                            text = "collections"
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .weight(1f, true)
                        .clickable {
                            currentScreen = "user"
                            colorsOfBottomMenu = mutableListOf(true, true, false, true,true)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.user),
                            contentDescription = "user",
                            tint = doTheColor(2)
                        )
                        Text(
                            color = doTheColor(2),
                            text = "profile"
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .weight(1f, true)
                        .clickable {
                            currentScreen = "open"
                            colorsOfBottomMenu = mutableListOf(true, true, true, false,true)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.open_link),
                            contentDescription = "open",
                            tint = doTheColor(3)
                        )
                        Text(
                            color = doTheColor(3),
                            text = "open"
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .weight(1f, true)
                        .clickable {
                            currentScreen = "search"
                            colorsOfBottomMenu = mutableListOf(true, true, true,true, false)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.search),
                            contentDescription = "search",
                            tint = doTheColor(4)
                        )
                        Text(
                            color = doTheColor(4),
                            text = "search"
                        )
                    }
                }
            }
        }
    }
}
