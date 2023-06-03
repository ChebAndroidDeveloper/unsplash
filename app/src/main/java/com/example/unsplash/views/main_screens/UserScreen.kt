package com.example.unsplash.views.main_screens

import android.app.Activity
import android.content.Context
import android.os.Build
import android.util.Log
import android.webkit.CookieManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import androidx.paging.compose.items
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.example.unsplash.model.liks_user.Like
import com.example.unsplash.view_model.MainViewModel
import com.example.unsplash.views.single_screens.getToken


@Composable
fun UserScreen() {
    val mainViewModel: MainViewModel = hiltViewModel()
    val context = LocalContext.current

    val showUserInfoOrItLikes = remember { mutableStateOf(true) }

    val userInfo by mainViewModel.userInfo.observeAsState()
    val lazyLikes = mainViewModel.likes.collectAsLazyPagingItems()
    Log.d(" lazyLikes", "${lazyLikes.itemCount}")

    LaunchedEffect(Unit) {
        getToken(context)?.let { mainViewModel.loadUserInfo("Bearer $it") }
    }

    var showDialog by remember { mutableStateOf(false) }
    var checkedState by remember { mutableStateOf(false) }


    if (showUserInfoOrItLikes.value) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                Image(
                    painter = rememberAsyncImagePainter(
                        ImageRequest.Builder(LocalContext.current)
                            .data(userInfo?.profile_image?.medium)
                            .build()
                    ),
                    contentDescription = "Profile Picture",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (userInfo != null) {
                    Text(text = "Name: ${userInfo!!.first_name}  ${userInfo!!.last_name}")
                    Text(text = "Username: ${userInfo!!.username}")
                    Text(text = "Email: ${userInfo!!.email}")

                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        onClick = {
                            getToken(context)?.let {
                                mainViewModel.getUserLikes(
                                    userInfo!!.username,
                                    "Bearer $it"
                                )
                            }
                            showUserInfoOrItLikes.value = !showUserInfoOrItLikes.value
                        }

                    ) {
                        Text("Смотреть лайки")
                    }
                    Button(onClick = {
                        showDialog = true
                    }) {
                        Text("Выйти из приложения")
                    }
                }
            }
        }
    } else  {
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn {
                Log.d("MyScreen", "LazyColumn itemCount: ${lazyLikes.itemCount}")
                items(
                    count = lazyLikes.itemCount,
                    key = lazyLikes.itemKey(),
                    contentType = lazyLikes.itemContentType(
                    )
                ) { index ->
                    val item = lazyLikes[index]
                    Log.d("MyScreen", "LazyColumn item at index $index: $item")
                    if (item != null) {
                        ItemsForUserLikes(item)
                    }
                }
            }
            Button(
                onClick = { showUserInfoOrItLikes.value = !showUserInfoOrItLikes.value },
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
            ) {
                Text("назад к странице пользователя")
            }
        }
    }
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(text = "Выход") },
            text = {
                Column {
                    Text(text = "Точно выйти?")
                    Row {
                        Checkbox(
                            checked = checkedState,
                            onCheckedChange = { checkedState = it }
                        )
                        Text(text = "Запомнить мой выбор")
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    // Удаление токена из SharedPreferences
                    val sharedPreferences =
                        context.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.remove("access_token")
                    editor.apply()
                    //удалаяем куки чтобы не происходило автоматическая авторизация после удаления токена
                    val cookieManager = CookieManager.getInstance()
                    cookieManager.removeAllCookies(null)
                    // Закрытие приложения
                    (context as Activity).finishAffinity()

                    showDialog = false
                }) {
                    Text(text = "ОК")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text(text = "Отмена")
                }
            }
        )
    }
}

@Composable
fun ItemsForUserLikes(photo: Like) {
    Card(
        shape = RoundedCornerShape(4.dp),
        modifier = Modifier
            .padding(top = 8.dp)
    ) {
        Box {
            Image(
                painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(LocalContext.current).data(data = photo.urls.regular)
                        .apply(block = fun ImageRequest.Builder.() {
                            memoryCachePolicy(CachePolicy.ENABLED)
                            diskCachePolicy(CachePolicy.ENABLED)
                        }).build()
                ),
                contentDescription = "Photo by ${photo.user.name}",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                contentScale = ContentScale.Crop
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .background(Color.Transparent)
                    .align(Alignment.BottomCenter),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    color = Color.White,
                    text = photo.user.name,
                    style = TextStyle(fontWeight = FontWeight.Light),
                    fontSize = 16.sp
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (photo.liked_by_user) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Liked by user",
                            tint = Color.Red
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.FavoriteBorder,
                            contentDescription = "Not liked by user",
                            tint = Color.Gray
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        color = Color.White,
                        text = "${photo.likes} likes"
                    )
                }
            }
        }
    }
}


