package com.example.unsplash.views.main_screens

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarResult
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.Card
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
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.example.unsplash.R
import com.example.unsplash.model.data_classes.PhotoDetails
import com.example.unsplash.model.data_classes_for_search_result.Photo
import com.example.unsplash.view_model.MainViewModel
import com.example.unsplash.views.single_screens.SaveImageDialog
import com.example.unsplash.views.single_screens.clearAnimatedVisibility
import com.example.unsplash.views.single_screens.getToken
import com.example.unsplash.views.single_screens.openMap
import com.example.unsplash.views.single_screens.sharePhoto
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun SearchScreen() {
    val context = LocalContext.current

    val scope = rememberCoroutineScope()

    val showDetails = remember { mutableStateOf(false) }
    val photoDetailsState = remember { mutableStateOf<PhotoDetails?>(null) }
    val photoDetails = photoDetailsState.value

    val mainViewModel: MainViewModel = hiltViewModel()
    val showSearchBarOrSearchScreen = remember { mutableStateOf(true) }
    val showSearchScreenOrDetailsOfPhoto = remember { mutableStateOf(true) }

    val photos = mainViewModel.photosFlow.collectAsLazyPagingItems()

    val listState = rememberLazyListState()
    var currentScrollPosition by remember { mutableStateOf(0) }
    LaunchedEffect(listState) {
        listState.animateScrollToItem(currentScrollPosition)
    }

    if (showSearchBarOrSearchScreen.value) {

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            SaveImageDialog(
                onSave = { text ->
                    getToken(context)?.let { mainViewModel.searchPhotosByQuery(text, "Bearer $it") }

                    showSearchBarOrSearchScreen.value = false
                },
                onCancel = { showSearchBarOrSearchScreen.value = false },
                text = "Введите слово для поиска"
            )
        }
    } else if (showSearchScreenOrDetailsOfPhoto.value) {
        LazyColumn(
            state = listState
        ) {
            items(
                count = photos.itemCount,
                key = photos.itemKey(),
                contentType = photos.itemContentType(
                )
            ) { index ->
                val item = photos[index]
                if (item != null) {
                    PhotoItemForSearchResults(item) {
                        listState.firstVisibleItemIndex.let { position ->
                            currentScrollPosition = position
                        }
                        scope.launch {
                            val details = mainViewModel.getOnePhoto(item.id)
                            savePhotoId(context, item.id)
                            photoDetailsState.value = details
                            showSearchScreenOrDetailsOfPhoto.value = false
                        }

                    }
                }
            }
        }
    } else {
        if (photoDetails != null) {
            PhotoItemForDetailsOfPhoto(photoDetails) {
                showSearchScreenOrDetailsOfPhoto.value = true
            }
        }
    }
}

@Composable
fun PhotoItemForSearchResults(photo: Photo, onClick: (String) -> Unit) {
    Card(
        shape = RoundedCornerShape(4.dp),
        modifier = Modifier
            .padding(top = 8.dp)
            .clickable(onClick = { onClick(photo.id) })
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoItemForDetailsOfPhoto(photoDetails: PhotoDetails, onBack: () -> Unit) {
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
                                    text = "Downloads: ${noInfo}"
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
                            IconButton(onClick = {if (latitude != null && longitude != null) {
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
                    text = "Tags: $noInfo",
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
