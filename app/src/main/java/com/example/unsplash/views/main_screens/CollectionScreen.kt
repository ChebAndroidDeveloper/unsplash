package com.example.unsplash.views.main_screens

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.example.unsplash.model.collection_data_classes.CollectionResponse
import com.example.unsplash.model.content_collections_data_classes.PhotoCollections
import com.example.unsplash.model.data_classes.PhotoDetails
import com.example.unsplash.view_model.MainViewModel
import com.example.unsplash.views.single_screens.PhotoDetailsScreen
import com.example.unsplash.views.single_screens.getToken
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.M)
@Composable
fun CollectionScreen() {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val mainViewModel: MainViewModel = hiltViewModel()

    val showCollectionsOrItsContent = remember { mutableStateOf(true) }
    val showContentCollectionsOrItsItemDetails = remember { mutableStateOf(true) }

    val listState = rememberLazyListState()
    var currentScrollPosition by remember { mutableStateOf(0) }

    val dataToUse = mainViewModel.collections?.collectAsLazyPagingItems()

    val collectionContent = mainViewModel.collectionsContentFlow.collectAsLazyPagingItems()
    Log.d("CollectionScreen", "collectionContent: ${collectionContent.itemCount}")

    var showEmptyListMessage by remember { mutableStateOf(false) }
    var itemClicked by remember { mutableStateOf(false) }
    LaunchedEffect(collectionContent.loadState) {
        showEmptyListMessage = itemClicked && collectionContent.loadState.refresh is LoadState.NotLoading && collectionContent.itemCount == 0
    }




    val photoDetailsState = remember { mutableStateOf<PhotoDetails?>(null) }
    val photoDetails = photoDetailsState.value



    LaunchedEffect(listState) {
        listState.animateScrollToItem(currentScrollPosition)
    }

    Scaffold() { innerPadding ->
        if (showCollectionsOrItsContent.value) {
            LazyColumn(
                modifier = Modifier.padding(innerPadding),
                state = listState
            ) {
                if (dataToUse != null) {

                    items(
                        count = dataToUse.itemCount,
                        key = dataToUse.itemKey(),
                        contentType = dataToUse.itemContentType()
                    ) { index ->
                        val item = dataToUse[index]
                        if (item != null) {
                            Log.d("CollectionContent", "item: ${item.id}")
                            CollectionListItem(item) {
                                listState.firstVisibleItemIndex.let { position ->
                                    currentScrollPosition = position
                                }
                                scope.launch {
                                    getToken(context)?.let { token ->
                                        Log.d("CollectionScreen", "Loading collection with ID: ${item.id}") // Добавлено
                                        mainViewModel.getCollectionsContentDyId(
                                            item.id,
                                            "Bearer $token"
                                        )
                                    }

                                }
                                itemClicked = true
                                showCollectionsOrItsContent.value = false
                            }
                        }
                    }
                }
            }
        }else if (showContentCollectionsOrItsItemDetails.value) {
            LazyColumn(
                modifier = Modifier.padding(innerPadding),
                state = listState
            ) {
                items(
                    count = collectionContent.itemCount,

                    key = collectionContent.itemKey(),
                    contentType = collectionContent.itemContentType()
                ) { index ->
                    Log.d("CollectionContent", "itemCount: ${collectionContent.itemCount}")
                    val item = collectionContent[index]
                    Log.d("CollectionContent", "item: $item")
                    if (item != null) {

                        CollectionContentItem(item) {
                            listState.firstVisibleItemIndex.let { position ->
                                currentScrollPosition = position
                            }
                            scope.launch {
                                val details = mainViewModel.getOnePhoto(item.id)
                                photoDetailsState.value = details
                                showContentCollectionsOrItsItemDetails.value = false
                            }
                        }
                    }
                }
            }

        } else {
            if (photoDetails != null) {
                PhotoDetailsScreen(photoDetails) {
                    showContentCollectionsOrItsItemDetails.value = true
                }
            }
        }

        AnimatedVisibility(visible = showEmptyListMessage) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFFFEFD5),
                shadowElevation = 4.dp,
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    "Список с сервера пришол пустой, попробуйте другую коллекцию",
                    color = Color.Black,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
        LaunchedEffect(showEmptyListMessage) {
            if (showEmptyListMessage) {
                delay(3000) // Задержка перед сбросом значения переменной
                itemClicked = false
            }
        }

    }
}

@Composable
fun CollectionListItem(item: CollectionResponse, onClick: (String) -> Unit) {
    Card(
        shape = RoundedCornerShape(4.dp),
        modifier = Modifier
            .padding(top = 8.dp)
            .clickable(onClick = { onClick(item.id) })
    ) {
        Box {
            Image(
                painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(LocalContext.current).data(
                        data = item.cover_photo.urls.regular
                    )
                        .apply(block = fun ImageRequest.Builder.() {
                            memoryCachePolicy(CachePolicy.ENABLED)
                            diskCachePolicy(CachePolicy.ENABLED)
                        }).build()
                ),
                contentDescription = "Collection by ${item.user.name}",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                contentScale = ContentScale.Crop
            )

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .fillMaxWidth()
                    .padding(8.dp)
                    .background(Color.Transparent)
                    .align(Alignment.BottomCenter)
                    .background(Color.Black.copy(0.5f)),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    color = Color.White,
                    text = item.title,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    color = Color.White,
                    text = "${item.total_photos} photos",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

            }
        }
    }
}

@Composable
fun CollectionContentItem(photo: PhotoCollections, onClick: (String) -> Unit) {
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
                        text = "${photo.likes} likes")
                }
            }
        }
    }
}



