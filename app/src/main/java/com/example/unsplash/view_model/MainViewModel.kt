package com.example.unsplash.view_model

import android.Manifest
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.Network
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.unsplash.R
import com.example.unsplash.model.collection_data_classes.CollectionResponse
import com.example.unsplash.model.data_classes.LikeResponse
import com.example.unsplash.model.data_classes.Photos
import com.example.unsplash.model.data_classes.PhotoDetails
import com.example.unsplash.model.data_classes_for_search_result.Photo
import com.example.unsplash.model.get_content.CollectionContentListPagingSource
import com.example.unsplash.model.get_content.DatabasePagingSource
import com.example.unsplash.model.get_content.PhotoCollectionsPagingSource
import com.example.unsplash.model.get_content.SearchPhotoPagingSourceByItId
import com.example.unsplash.model.get_content.UnsplashApi
import com.example.unsplash.model.get_content.UnsplashPagingSource
import com.example.unsplash.model.get_content.UnsplashRepository
import com.example.unsplash.model.get_content.UserLikesPagingSource
import com.example.unsplash.model.liks_user.Like
import com.example.unsplash.model.user_data_classes.Me
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.net.URL
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    application: Application,
    private val unsplashApi: UnsplashApi,
    private val unsplashRepository: UnsplashRepository
) : AndroidViewModel(application) {

    private val _isNetworkAvailable = MutableLiveData<Boolean>()
    val isNetworkAvailable: LiveData<Boolean> = _isNetworkAvailable

    var uriForUri = MutableLiveData<Uri?>()

    val photosFromApi: Flow<PagingData<Photos>>? by lazy {
        val token: String? = getToken()
        if (token != null) {
            Pager(PagingConfig(pageSize = 20)) {
                UnsplashPagingSource(unsplashApi, unsplashRepository, token)
            }.flow.cachedIn(viewModelScope)
        } else {
            null
        }
    }

    val collections: Flow<PagingData<CollectionResponse>>? by lazy {
        val token: String? = getToken()
        if (token != null) {
            Pager(PagingConfig(pageSize = 20)) {
                PhotoCollectionsPagingSource(unsplashApi, "Bearer $token")
            }.flow.cachedIn(viewModelScope)
        } else {
            null
        }
    }


    val photosFromDb: Flow<PagingData<Photos>> = Pager(
        PagingConfig(pageSize = 20)
    ) {
        DatabasePagingSource(unsplashRepository)
    }.flow

    suspend fun getOnePhoto(id: String): PhotoDetails {
        return unsplashApi.getPhotoDetailsById(
            id,
            auth = "Bearer ${getToken()}"
        )
    }

    suspend fun likePhoto(id: String, accessToken: String): Response<LikeResponse> {
        return unsplashApi.likePhoto(
            id,
            auth = "Bearer $accessToken"
        )

    }

    val userInfo = MutableLiveData<Me?>()
    fun loadUserInfo(accessToken: String) {
        viewModelScope.launch {
            val response = getInfoAboutUser(accessToken)
            if (response.isSuccessful) {
                userInfo.value = response.body()
            }
        }
    }

    suspend fun getInfoAboutUser(accessToken: String): Response<Me> {
        return unsplashApi.getInfoAboutMe(
            accessToken
        )
    }


    val likes = MutableStateFlow(PagingData.empty<Like>())
    fun getUserLikes(userName: String, token: String) {
        Log.d("MyViewModel", "getUserLikes called with userName: $userName and token: $token")
        viewModelScope.launch {
            Pager(PagingConfig(pageSize = 2)) {
                UserLikesPagingSource(
                    unsplashApi,
                    userName,
                    token
                )
            }.flow.cachedIn(viewModelScope).collectLatest { pagingData ->
                Log.d("MyViewModel", "getUserLikes received pagingData: $pagingData")
                likes.emit(pagingData)
            }
        }
    }


    suspend fun unlikePhoto(id: String, accessToken: String): Response<LikeResponse> {
        return unsplashApi.unlikePhoto(
            id,
            auth = "Bearer $accessToken"
        )
    }


    val photosFlow = MutableStateFlow(PagingData.empty<Photo>())
    fun searchPhotosByQuery(query: String, token: String) {
        viewModelScope.launch {
            Pager(PagingConfig(pageSize = 20)) {
                SearchPhotoPagingSourceByItId(
                    unsplashApi,
                    query,
                    token
                )
            }.flow.cachedIn(viewModelScope).collectLatest { pagingData ->
                photosFlow.emit(pagingData)
            }
        }
    }

    val collectionsContentFlow =
        MutableStateFlow(PagingData.empty<com.example.unsplash.model.content_collections_data_classes.Photo>())
    fun getCollectionsContentDyId(id: String, token: String) {
        viewModelScope.launch {
            Pager(PagingConfig(pageSize = 20)) {
                CollectionContentListPagingSource(
                    api = unsplashApi,
                    authorization = token,
                    collectionId = id
                )
            }.flow.cachedIn(viewModelScope).collectLatest { pagingData ->
                collectionsContentFlow.emit(pagingData)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun registerNetworkCallback(context: Context) {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                _isNetworkAvailable.postValue(true)
            }

            override fun onLost(network: Network) {
                _isNetworkAvailable.postValue(false)
            }
        }
        connectivityManager.registerDefaultNetworkCallback(networkCallback)
    }

    fun savePhoto(photo: String, context: Context, fileName: String) {
        val url = photo
        viewModelScope.launch(Dispatchers.IO) {
            val inputStream = URL(url).openStream()
            val bitmap = BitmapFactory.decodeStream(inputStream)

            val values = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            }

            val resolver = context.contentResolver
            val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

            viewModelScope.launch {
                withContext(Dispatchers.Main) {
                    uriForUri.value = uri
                }
            }

            uri?.let {
                val outputStream = resolver.openOutputStream(uri)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                outputStream?.close()
            }
        }
        sendNotification(context)

    }

    fun sendNotification(context: Context) {
        val notificationId = 1 // уникальный идентификатор для каждого уведомления
        val channelId = "my_channel_id" // идентификатор канала уведомлений


        // Создание канала уведомлений для Android 8.0 и выше
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "My channel"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance)
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uriForUri.value, "image/*")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.share)
            .setContentTitle("Фотография сохранена")
            .setContentText("Нажмите, чтобы открыть")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent) // установка PendingIntent
            .setAutoCancel(true) // автоматическое закрытие уведомления при нажатии


        // Отправка уведомления
        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            notify(notificationId, builder.build())
        }
    }

    fun getToken(): String? {
        val sharedPreferences =
            getApplication<Application>().applicationContext.getSharedPreferences(
                "my_preferences",
                Context.MODE_PRIVATE
            )
        return sharedPreferences.getString("access_token", null)
    }

}


