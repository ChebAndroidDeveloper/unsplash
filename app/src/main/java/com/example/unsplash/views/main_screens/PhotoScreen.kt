package com.example.unsplash.views.main_screens

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.example.unsplash.model.data_classes.PhotoDetails
import com.example.unsplash.view_model.MainViewModel
import com.example.unsplash.views.single_screens.PhotoDetailsScreen
import com.example.unsplash.views.single_screens.PhotoItem
import kotlinx.coroutines.launch


@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun PhotoScreen() {
    val context = LocalContext.current
    val mainViewModel: MainViewModel = hiltViewModel()

    mainViewModel.registerNetworkCallback(context = context)

    val isNetworkAvailable by mainViewModel.isNetworkAvailable.observeAsState(true)

    val photosToUse = if (isNetworkAvailable)
        mainViewModel.photosFromApi!!.collectAsLazyPagingItems()
    else
        mainViewModel.photosFromDb.collectAsLazyPagingItems()


    val scope = rememberCoroutineScope()
    val photoDetailsState = remember { mutableStateOf<PhotoDetails?>(null) }
    val photoDetails = photoDetailsState.value

    val snackbarHostState = remember { SnackbarHostState() }

    val showDetails = remember { mutableStateOf(false) }

    val listState = rememberLazyListState()
    var currentScrollPosition by remember { mutableStateOf(0) }

    LaunchedEffect(isNetworkAvailable) {
        if (!isNetworkAvailable) {
            snackbarHostState.showSnackbar(
                message = "Internet connection is lost, only stored data is available for viewing",
                duration = SnackbarDuration.Short
            )
        } else {
            snackbarHostState.showSnackbar(
                message = "Internet connection is active",
                duration = SnackbarDuration.Short
            )
            photosToUse.retry()
        }
    }
    LaunchedEffect(listState) {
        listState.animateScrollToItem(currentScrollPosition)
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        if (showDetails.value) {
            // Отображение экрана с деталями фотографии
            if (photoDetails != null) {
                PhotoDetailsScreen(photoDetails) {
                    showDetails.value = false
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(innerPadding),
                state = listState
            ) {
                items(
                    count = photosToUse.itemCount,
                    key = photosToUse.itemKey(),
                    contentType = photosToUse.itemContentType()
                ) { index ->
                    val item = photosToUse[index]
                    if (item != null) {
                        PhotoItem(item) {
                            listState.firstVisibleItemIndex.let { position ->
                                currentScrollPosition = position
                            }
                            scope.launch {
                                val details = mainViewModel.getOnePhoto(item.id)
                                savePhotoId(context, item.id)
                                photoDetailsState.value = details
                                showDetails.value = true
                            }
                        }
                    }
                }
            }
        }
    }
}

//сохраняем фотоайди в шаред префс
fun savePhotoId(context: Context, photoId: String) {
    val sharedPreferences = context.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
    with(sharedPreferences.edit()) {
        putString("photo_id", photoId)
        apply()
    }
}





