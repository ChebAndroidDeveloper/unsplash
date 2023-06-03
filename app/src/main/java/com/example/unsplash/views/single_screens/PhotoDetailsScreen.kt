package com.example.unsplash.views.single_screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarResult
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.unsplash.R
import com.example.unsplash.model.data_classes.PhotoDetails
import com.example.unsplash.model.data_classes.Position
import com.example.unsplash.view_model.MainViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.M)
@Composable
fun PhotoDetailsScreen(photoDetails: PhotoDetails, onBack: () -> Unit) {

    val latitude = photoDetails.location?.position?.latitude
    val longitude = photoDetails.location?.position?.longitude
    var likesCount by remember { mutableStateOf(photoDetails.likes) }

    val mainViewModel: MainViewModel = hiltViewModel()
    val isNetworkAvailable by mainViewModel.isNetworkAvailable.observeAsState(false)
    var isShowedWhenInternet by remember { mutableStateOf(true) }
    var isShowedWhenLocation by remember { mutableStateOf(true) }


    val scope = rememberCoroutineScope()

    val context = LocalContext.current

    val exif = photoDetails.exif
    val noInfo = "N/A"

    var showDialog by remember { mutableStateOf(false) }

    val scaffoldState = rememberScaffoldState()

    val uri by mainViewModel.uriForUri.observeAsState()
    val previousUri = remember { mutableStateOf<Uri?>(null) }
    val isSnackbarShown = remember { mutableStateOf(false) }

    var methodChange by remember { mutableStateOf(photoDetails.liked_by_user) }
    val colorChange = if (methodChange) Color.Red else Color.Gray

    Scaffold(
        scaffoldState = scaffoldState
    )
    { innerPadding ->
        if (showDialog) {
            SaveImageDialog(
                onSave = { fileName ->
                    mainViewModel.savePhoto(photoDetails.urls.raw, context, fileName)
                    previousUri.value = uri
                    showDialog = false
                },
                onCancel = { showDialog = false },
                text = "Сохранить изображение"
            )
        } else {
            Column()
            {
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(modifier = Modifier.padding(innerPadding)) {
                        Image(
                            painter = rememberAsyncImagePainter(photoDetails.urls.regular),
                            contentDescription = null,
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
                                text = photoDetails.user.name,
                                style = TextStyle(fontWeight = FontWeight.Light),
                                fontSize = 16.sp
                            )

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (methodChange) {
                                    IconButton(onClick = {
                                        if (isNetworkAvailable) {
                                            likesCount -= 1
                                            scope.launch {
                                                getToken(context)?.let {
                                                    mainViewModel.unlikePhoto(
                                                        photoDetails.id,
                                                        it
                                                    )
                                                }
                                            }
                                            methodChange = !methodChange
                                        } else {
                                            isShowedWhenInternet = !isShowedWhenInternet
                                            scope.launch {
                                                isShowedWhenInternet =
                                                    clearAnimatedVisibility(isShowedWhenInternet)
                                            }
                                        }
                                    }
                                    )
                                    {
                                        Icon(
                                            imageVector = Icons.Default.Favorite,
                                            contentDescription = "Liked by user",
                                            tint = colorChange
                                        )
                                    }
                                } else {
                                    IconButton(onClick = {
                                        if (isNetworkAvailable) {
                                            likesCount += 1
                                            scope.launch {
                                                getToken(context)?.let {
                                                    mainViewModel.likePhoto(
                                                        photoDetails.id,
                                                        it
                                                    )
                                                }
                                            }
                                            methodChange = !methodChange
                                        } else {
                                            isShowedWhenInternet = !isShowedWhenInternet
                                            scope.launch {
                                                isShowedWhenInternet =
                                                    clearAnimatedVisibility(isShowedWhenInternet)
                                            }

                                        }
                                    }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.FavoriteBorder,
                                            contentDescription = "Not liked by user",
                                            tint = colorChange
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    color = Color.White,
                                    text = "${likesCount} likes"
                                )
                            }
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .background(Color.Transparent)
                                .align(Alignment.TopCenter),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                color = Color.White,
                                text = "created: ${photoDetails.created_at.take(10)}",
                                style = TextStyle(fontWeight = FontWeight.Light)
                            )


                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    color = Color.White,
                                    text = "Downloads: ${photoDetails.downloads}"
                                )
                            }
                        }
                    }
                }
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        Color.LightGray
                    ),
                    title = { Text("") },
                    actions = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            IconButton(onClick = onBack) {
                                Icon(
                                    painter = painterResource(
                                        id = R.drawable.back
                                    ),
                                    contentDescription = "back"
                                )
                            }
                            IconButton(onClick = {
                                previousUri.value = uri
                                isSnackbarShown.value = true
                                showDialog = true
                            }) {
                                Icon(
                                    painter = painterResource(
                                        id = R.drawable.add
                                    ),
                                    contentDescription = "add"
                                )
                            }
                            IconButton(onClick = {
                                if (latitude != null && longitude != null) {
                                    openMap(context, latitude, longitude)
                                } else {
                                    isShowedWhenLocation = !isShowedWhenLocation
                                    scope.launch {
                                        isShowedWhenLocation =
                                            clearAnimatedVisibility(isShowedWhenLocation)
                                    }
                                }
                            }) {
                                Icon(
                                    painter = painterResource(
                                        id = R.drawable.location
                                    ),
                                    contentDescription = "location"
                                )
                            }
                            IconButton(onClick = { sharePhoto(context) }) {
                                Icon(
                                    painter = painterResource(
                                        id = R.drawable.share
                                    ),
                                    contentDescription = "Share"
                                )
                            }
                        }
                    }
                )
                Spacer(modifier = Modifier.height(10.dp))

                // Отображение тегов
                Text(
                    text = "Tags: ${photoDetails.tags?.joinToString { it.title }}",
                    style = TextStyle(fontStyle = FontStyle.Italic)
                )
                Spacer(modifier = Modifier.height(10.dp))

                // Отображение EXIF
                if (exif != null) {
                    Text(
                        text = "EXIF:",
                        style = TextStyle(fontWeight = FontWeight.Black)
                    )
                    Text(text = "Make: ${exif.make ?: noInfo}")
                    Text(text = "Model: ${exif.model ?: noInfo}")
                    Text(text = "Exposure time: ${exif.exposure_time ?: noInfo}")
                    Text(text = "Aperture: ${exif.aperture ?: noInfo}")
                    Text(text = "Focal length: ${exif.focal_length ?: noInfo}")
                    Text(text = "ISO: ${exif.iso ?: noInfo}")
                }
            }
        }
    }
    AnimatedVisibility(visible = !isShowedWhenInternet) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color(0xFFFFEFD5),
            shadowElevation = 4.dp,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                "Only when internet is available",
                color = Color.Black,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
    AnimatedVisibility(visible = !isShowedWhenLocation) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color(0xFFFFEFD5),
            shadowElevation = 4.dp,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                "For this photo location data is not available",
                color = Color.Black,
                modifier = Modifier.padding(16.dp)
            )
        }
    }

    LaunchedEffect(uri) {
        if (uri != null && previousUri.value != uri && isSnackbarShown.value) {
            val snackbarResult = scaffoldState.snackbarHostState.showSnackbar(
                message = "Фотография сохранена",
                actionLabel = "Открыть"
            )
            if (snackbarResult == SnackbarResult.ActionPerformed) {
                previousUri.value = uri
                // Открываем фотографию во внешнем приложении по нажатию на Snackbar
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(uri, "image/*")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                context.startActivity(intent)
            }
        }
    }


}

//убрать анимацию отображения информации
suspend fun clearAnimatedVisibility(showed: Boolean): Boolean {
    delay(3000)
    return !showed
}

//Поделиться фото
fun sharePhoto(context: Context) {
    val photoId = getPhotoId(context)
    if (photoId != null) {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "https://unsplash.com/photos/$photoId")
            type = "text/plain"
        }
        context.startActivity(Intent.createChooser(shareIntent, "Поделиться фото"))
    }
}

//Получить id фото из шаредпрефс
fun getPhotoId(context: Context): String? {
    val sharedPreferences = context.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
    return sharedPreferences.getString("photo_id", null)
}

//получить токе из шаред префс
fun getToken(context: Context): String? {
    val sharedPreferences = context.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
    return sharedPreferences.getString("access_token", null)
}

//открыть карту по локационным данныи фото
fun openMap(context: Context, latitude: Double, longitude: Double) {
    val gmmIntentUri = Uri.parse("geo:$latitude,$longitude")
    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
    mapIntent.setPackage("com.google.android.apps.maps")
    if (mapIntent.resolveActivity(context.packageManager) != null) {
        context.startActivity(mapIntent)
    }
}


